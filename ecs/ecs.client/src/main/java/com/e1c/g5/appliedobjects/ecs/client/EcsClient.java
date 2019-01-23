/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.SettableFuture;

import com.e1c.annotations.Nullable;
import com.e1c.g5.appliedobjects.ecs.client.apidata.AppParams;
import com.e1c.g5.appliedobjects.ecs.client.apidata.IbKeys;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiProtocolException;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiTimeoutException;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiUnexpectedException;
import com.e1c.g5.appliedobjects.ecs.client.protodata.AuthApplicationResponse;
import com.e1c.g5.appliedobjects.ecs.client.protodata.IbAuthData;
import com.e1c.g5.appliedobjects.ecs.client.request.IEcsRequest;
import com.e1c.g5.appliedobjects.ecs.client.request.requests.AuthenticateRequestPhase1;
import com.e1c.g5.appliedobjects.ecs.client.request.requests.AuthenticateRequestPhase2;
import com.e1c.g5.appliedobjects.ecs.client.request.requests.GetUnreadConversationMessagesRequest;
import com.e1c.g5.appliedobjects.ecs.client.response.EcsResponseBase;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseAction;
import com.e1c.g5.appliedobjects.ecs.client.response.responses.AuthenticateResponse;
import com.e1c.g5.appliedobjects.ecs.client.response.responses.ErrorResponse;
import com.e1c.g5.appliedobjects.ecs.client.response.responses.GetUnreadConversationMessagesResponse;
import com.e1c.g5.appliedobjects.ecs.client.wss.EcsWssClient;
import com.e1c.g5.appliedobjects.ecs.crypto.Crypto;
import com.e1c.g5.appliedobjects.ecs.crypto.SessionKeys;

public final class EcsClient implements EcsWssClient.IEcsWssClientOutOfBandResponseProcessor, AutoCloseable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EcsClient.class);

    // (возможно) полученный извне пул исполнения
    private ScheduledExecutorService externalSchedulerExecutorService;

    /**
     * Используемый пул исполнения<br><br>
     * <p>
     * Если исполнителю явно не сделать shutdown(), то треды могут/будут крутиться и приложение НЕ завершится.<br>
     * Поэтому наверху нужно либо аккуратно отлавливать все исключения и доводить до того, чтобы тут происходил close(),
     * который и выполнит остановку созданного тут исполнителя; либо передавать сюда свой пул и самому его потом закрывать.
     */
    private final ScheduledExecutorService wssSchedExecSvc;

    private final EcsWssClient ecsWssClient;

    private final SessionKeys sessionKeys = new SessionKeys();

    // id сессии, будет получен в пакете "ping"
    private UUID sessionId;

    // "Событие-в-будущем" на поступление sessionID
    private final AtomicReference<SettableFuture<UUID>> atomRefSfSessionId = new AtomicReference<>(null);

    // Исполнитель .withTimeout для "события-в-будущем" (создается и завершается в ходе connect)
    private ScheduledExecutorService oobSchedExecSvc;

    public EcsClient(@Nullable ScheduledExecutorService externalSchedulerExecutorService)
    {
        this.externalSchedulerExecutorService = externalSchedulerExecutorService;
        // Если получили пул извне - используем его, иначе - сами создаем пул
        if (externalSchedulerExecutorService != null)
        {
            this.wssSchedExecSvc = externalSchedulerExecutorService;
        }
        else
        {
            this.wssSchedExecSvc = Executors.newScheduledThreadPool(1);
        }

        // Передаем EcsWssClient'у этот пул свой обработчик OOB-данных
        this.ecsWssClient = new EcsWssClient(this, this.wssSchedExecSvc);
    }

    @Override
    public void processOutOfBandResponse(EcsResponseBase ecsResponseOOB)
    {
        // ожидается, что внеполосно могут приходить только OobResponse объекты ("0{...ping...}" и "40")
        if (!(ecsResponseOOB instanceof OobResponse))
        {
//            TODO G5RT-2331: молча игнорировать неизвестное или как-то в лог сообщить?
//            throw new EcsApiProtocolException(
//                    new IllegalStateException(
//                            "Ошибка протокола: внеполосные данные не соответствуют допустимому формату"));
            return;
        }
        LOGGER.trace("processOutOfBandResponse() : {}", ecsResponseOOB);

        String action = ecsResponseOOB.getAction().getSubject();
        String correlation = ecsResponseOOB.getCorrelation().getCorrelationId();
        // нас интересует только сообщение "ping"
        if (ResponseAction.PING.equals(action))
        {
            try
            {   // в correlation должен содержаться sessionId (UUID) - передаем это в качестве результата (в connect)
                SettableFuture<UUID> sfSessionId = atomRefSfSessionId.get();
                if (sfSessionId != null)
                {
                    sfSessionId.set(UUID.fromString(correlation));
                }
            }
            catch (IllegalArgumentException e)
            {
                throw new EcsApiProtocolException(e);
            }
        }
    }

    @Override
    public void close()
    {
        LOGGER.info("EcsClient::close()");

        disconnect();

        // Если НЕ получили пул извне, а создали здесь - закрываем его
        if (externalSchedulerExecutorService == null) {
            wssSchedExecSvc.shutdown();
        }
    }

    public void disconnect()
    {
        ecsWssClient.disconnect();
    }

    public void connect(String uri, String keyStoreFilePath, String keyStorePasswd, String keyPasswd, long timeoutSeconds)
    {
        try
        {
            // SettableFuture, ожидающая поступления sessionID.
            // Создаём-публикуем её ДО ТОГО КАК начинаем connect (который может создавать потоки, вызывать коллбэки...),
            // чтобы (как предполагается) наш коллбэк processOutOfBandResponse уже заведомо видел её.
            SettableFuture<UUID> sfSessionId = SettableFuture.create();
            oobSchedExecSvc = Executors.newScheduledThreadPool(1);
            FluentFuture<UUID> futureMessage = sfSessionId.withTimeout(timeoutSeconds, TimeUnit.SECONDS, oobSchedExecSvc);
            atomRefSfSessionId.getAndSet(sfSessionId);

            // Инициируем начало подключения
            ecsWssClient.connect(uri, keyStoreFilePath, keyStorePasswd, keyPasswd, timeoutSeconds);

            // Дожидаемся результата (или срабатывания таймаута)
            this.sessionId = futureMessage.get();

            LOGGER.debug("sessionId : {}", sessionId);

            // НЕ удаляем эту SettableFuture НИ здесь, НИ в processOutOfBandResponse (и поэтому нет никаких null/race).
            // atomRefSfSessionId.getAndSet(null);
            // Полагаемся на то, что можно без вреда и без результата повторно и многократно set()'ить и set()'ить
            // эту SettableFuture, и это уже не оказывает на неё никакого влияния.
            // А когда будет вызван НОВЫЙ connect(), то будет создана и НОВАЯ SettableFuture, которая, как и нужно,
            // при первом set()'е "сработает", а дальше опять может безопасно многократно безрезультатно set()'иться.
        }
        catch (InterruptedException | ExecutionException e)
        {
            if (e.getCause() instanceof TimeoutException)
            {
                throw new EcsApiTimeoutException(e);
            }
            throw new EcsApiUnexpectedException(e);
        }
        finally
        {
            // После отработки FluentFuture, результативно или с исключением, не забываем, выключаем исполнитель
            oobSchedExecSvc.shutdown();
            oobSchedExecSvc = null;
        }
        // Таким образом, сюда, в обработчик протокола, привнесены методики работы с Future из Wss-клиента.
        // Здесь они логично отражают именно обработку протокола, но замусоривают код и при вносят сложности "а если?".

        // Можно сделать другой вариант.
        // 1) Немного поменять формирование "ping" OOB-response, так, чтобы у него в correlationId был известный ID,
        // например, 000...-...-...-0, который НИКОГДА НЕ ожидается получить от сервера. А сам sid класть в поле "info".
        // 2) Затем ecsWssClient.query() разбить на 3 части, сделать отдельными функциями:
        // - создание "события-в-будущем", добавка в очередь
        // - отправка сообщения (если НЕпустое)
        // - ожидание результата "события-в-будущем"
        // 3) И сделать внутри ecsWssClient.connect() специальный финт:
        // - вызываем создание "события-в-будущем" с correlationId = 000...-...-...-0
        // - вызываем connect() сокета
        // - вызываем ожидание результата "события-в-будущем" с correlationId = 000...-...-...-0
        // Получаем, что если/когда придёт "ping", то его будет "как-бы ожидать фиктивный запрос",
        // поэтому для него вызывается .set() и возвращается из ожидания результата "события-в-будущем",
        // которое возвращается и получается на выход успешный результат ecsWssClient.connect().
        // И ecsWssClient.connect() будет возвращать этот самый sid.
        // Минусы - ecsWssClient становится более специфическим, завязанным на протокол сервера СистемыВзаимодействия.
        // Плюсы - вся нутрянка с ...Future и таймаутами инкапсулируется только в рамках ecsWssClient и не лезет сюда.
    }

//...

    public void authenticatePhase1(IbKeys ibKeys, AppParams appParams, long timeoutSeconds)
    {
        sessionKeys.setIbPublicKey(ibKeys.getPublicKey());
        sessionKeys.setIbPrivateKey(ibKeys.getPrivateKey());
        sessionKeys.setEcsPublicKey(appParams.getEcsPublicKey());

        IEcsRequest request = new AuthenticateRequestPhase1.Builder()
                .setApplicationId(appParams.getApplicationId())
                .build();
        LOGGER.debug("request for \"authenticatePhase1\" = {}", request.asMessage());

        EcsResponseBase response = ecsWssClient.query(
                request,
                timeoutSeconds
        );
        LOGGER.debug("response for \"authenticatePhase1\" = {}", response);

        if (!(response instanceof ErrorResponse))
        {
            throw new EcsApiProtocolException(new IllegalStateException("UNEXPECTED protocol behaivour"));
        }

        ErrorResponse.Information responseErrorInfo = ((ErrorResponse) response).getInformation();
        // "нормальная" ошибка - это ИЛИ "уже авторизован" ИЛИ "accessDenied"
        if (responseErrorInfo.isCode_exists_And_Source_auth())
        {

            LOGGER.info("\"authenticatePhase1\" ALREADY AUTHENTICATED");
            return;     // OK1 - уже авторизован
        }
        if (responseErrorInfo.isAccessDenied())
        {
            LOGGER.info("\"authenticatePhase1\" CODE_ACCESS_DENIED - wait for phase2");
            return;     // OK2 - продолжение аутентификации
        }
        // FAIL
        LOGGER.error("\"authenticatePhase1\" : {}", response);
        throw new EcsApiProtocolException(new IllegalStateException("authenticatePhase1 ERROR"));
    }

    public UUID authenticatePhase2(IbKeys ibKeys,
                                   UUID ibLoginId, String ibLoginName, String ibLoginFullName, boolean isPrivileged,
                                   AppParams appParams,
                                   long timeoutSeconds)
    {
        sessionKeys.setIbPublicKey(ibKeys.getPublicKey());
        sessionKeys.setIbPrivateKey(ibKeys.getPrivateKey());
        sessionKeys.setEcsPublicKey(appParams.getEcsPublicKey());

        // Формируем и шифруем объект recoveryData
        AuthApplicationResponse recoveryData = new AuthApplicationResponse();
        Date dt = new Date();
        recoveryData.setExpirationTime(dt);
        recoveryData.setSessionId(sessionId);

        IbAuthData authData = new IbAuthData();
        authData.setLogin(ibLoginId.toString());
        authData.setName(ibLoginName);
        authData.setFullName(ibLoginFullName);
        authData.setClientId(appParams.getApplicationId());
        authData.setPrivileged(isPrivileged);

        recoveryData.setData(authData);
        LOGGER.trace("recoveryData for \"authenticatePhase2\" = {}", recoveryData);

        String encRecoveryData = Crypto.encrypt(recoveryData, sessionKeys.getEcsPublicKey(), sessionKeys.getIbPrivateKey());

        // Выполняем запрос-ответ
        IEcsRequest request = new AuthenticateRequestPhase2.Builder()
                .setApplicationId(appParams.getApplicationId())
                .setRecoveryData(encRecoveryData)
                .build();
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("request for \"authenticatePhase2\" = {}", request.asMessage());
        }

        EcsResponseBase response = ecsWssClient.query(
                request,
                timeoutSeconds
        );
        LOGGER.debug("response for \"authenticatePhase2\" = {}", response);

        if (response instanceof ErrorResponse)
        {
            throw new EcsApiProtocolException(new IllegalStateException("authenticatePhase2 ERROR"));
        }
        if (!(response instanceof AuthenticateResponse))
        {
            throw new EcsApiProtocolException(new IllegalStateException("UNEXPECTED protocol behaivour"));
        }
        // OK
        AuthenticateResponse responseAuthenticate = (AuthenticateResponse) response;
        LOGGER.trace("\"authenticatePhase2\" = {}", responseAuthenticate);
        LOGGER.info("\"authenticatePhase2\" OK");

        LOGGER.info("ECS login ID : {}", responseAuthenticate.getInformation().getEcsLoginid());

        return responseAuthenticate.getInformation().getEcsLoginid();
    }

//...

    public List<GetUnreadConversationMessagesResponse.Information.Message> getUnreadConversationMessages(
            UUID conversationId, int limitMessagesCount, long timeoutSeconds)
    {
        IEcsRequest request = new GetUnreadConversationMessagesRequest.Builder()
                .setConversationId(conversationId)
                .setLimit(limitMessagesCount)
                .build();
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("request for \"getUnreadConversationMessages\" = {}", request.asMessage());
        }

        EcsResponseBase response = ecsWssClient.query(
                request,
                timeoutSeconds
        );
        LOGGER.debug("response for \"getUnreadConversationMessages\" = {}", response);

        if (response instanceof ErrorResponse)
        {
            throw new EcsApiProtocolException(new IllegalStateException("getUnreadConversationMessages ERROR"));
        }
        if (!(response instanceof GetUnreadConversationMessagesResponse))
        {
            throw new EcsApiProtocolException(new IllegalStateException("UNEXPECTED protocol behaivour"));
        }
        // OK
        GetUnreadConversationMessagesResponse responseGetUnreadConversationMessages = (GetUnreadConversationMessagesResponse) response;
        LOGGER.trace("\"getUnreadConversationMessages\" = {}", responseGetUnreadConversationMessages);
        LOGGER.info("\"getUnreadConversationMessages\" OK");

        List<GetUnreadConversationMessagesResponse.Information.Message> messages = responseGetUnreadConversationMessages
                .getInformation()
                .getMessages();

        return messages;
    }

//...

}
