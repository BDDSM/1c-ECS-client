/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.wss;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.SettableFuture;

import com.e1c.annotations.Nonnull;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiCertificateException;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiTimeoutException;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiUnexpectedException;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiUriException;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiWssException;
import com.e1c.g5.appliedobjects.ecs.client.request.IEcsRequest;
import com.e1c.g5.appliedobjects.ecs.client.response.EcsResponseBase;
import com.e1c.g5.appliedobjects.ecs.client.response.EcsResponseParser;

import static com._1c.g5.commons.utils.G5Preconditions.checkArgumentNotNull;

/**
 * Класс клиента для сервера системы взаимодействия.
 * Реализует механизм блокирующего "вызова" метода протокола:
 * отправляет запрос и возвращает ответ либо исключение по наступлении таймаута.
 * <br><br>
 * Инкапсулирует в себе объект WebSocket-клиента {@link WssClient}.
 * <br><br>
 * Для обработки поступающих "внеполосных" (не ожидаемых) данных
 * вызывается переданный в аргументе конструктора объект-обработчик,
 * реализующий {@link IEcsWssClientOutOfBandResponseProcessor}
 */
public final class EcsWssClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EcsWssClient.class);

    public static final String STORETYPE = "JKS";

    private SSLSocketFactory sslSocketFactory;

    private WssClient wssClient;

    /**
     * Коллекция "запросов, ожидающих ответы"; ключи - correlationId'ы.
     * <p>
     * На первом этапе, пока correlation в основном один ("А" или "В"), запросы предполагаются идти ПО ОДНОМУ,
     * (иначе они в мапе НЕ усидят), и если по ошибке запихать одновременно больше одного,
     * то остальные не будут обнаруживаться и будут фэйлиться по таймаутам.
     * <p>
     * На следующем этапе, когда correlation уже становится UUID'ом - всё пучком, можно гнать запросы валом,
     * ключи будут уникальные и все ответы будут им четко сопоставлены (а таймаут - только если реально таймаут).
     */
    private final Map<String, SettableFuture<EcsResponseBase>> pendingRequests = new ConcurrentHashMap<>();

    /**
     * Исполнитель .withTimeout для "события-в-будущем"
     */
    private ScheduledExecutorService schedExecSvc;

    /**
     * Объект обработчик "внеполосных данных" (см. {@link #onMessage});
     * предоставлен в конструктре ({@link #EcsWssClient}).
     */
    private final IEcsWssClientOutOfBandResponseProcessor OOBprocessor;

    /**
     * Интерфейс обработчика "внеполосных данных" (см. {@link #onMessage}).
     */
    public interface IEcsWssClientOutOfBandResponseProcessor
    {
        void processOutOfBandResponse(EcsResponseBase ecsResponseOOB);
    }

    /**
     * Внутренний класс WebSocket-клиента, реализует класс из библиотеки java_websocket.<br>
     * Сделан так для того, чтобы возбуждаемое его конструктором исключение обернуть в "собственное"
     * (стандарт: за пределы внешнего класса должны выходить только "собственные" исключения).<br>
     * Этот внутренний класс во всех своих callback'ах перевызывает callback'и внешнего класса.
     */
    private final class WssClient extends WebSocketClient
    {
        public WssClient(String serverUri) throws URISyntaxException
        {
            super(new URI(serverUri));
        }

        @Override
        public void onOpen(ServerHandshake handshakedata)
        {
            EcsWssClient.this.onOpen();
        }

        @Override
        public void onMessage(String message)
        {
            EcsWssClient.this.onMessage(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote)
        {
            EcsWssClient.this.onClose();
        }

        @Override
        public void onError(Exception ex)
        {
            EcsWssClient.this.onError(ex);
        }
    }

    /**
     * @param oobProcessor обработчик "внеполосных данных" (см. {@link #onMessage})
     * @param schedExecSvc исполнитель .withTimeout для "события-в-будущем" (см. {@link #query})
     */
    public EcsWssClient(@Nonnull IEcsWssClientOutOfBandResponseProcessor oobProcessor,
                        @Nonnull ScheduledExecutorService schedExecSvc)
    {
        checkArgumentNotNull(oobProcessor);
        checkArgumentNotNull(schedExecSvc);
        this.OOBprocessor = oobProcessor;
        this.schedExecSvc = schedExecSvc;
    }

    public void onOpen()
    {
    }

    public void onClose()
    {
        if (wssClient != null)
        {
            wssClient = null;
        }
    }

    public void onError(Exception ex)
    {
        this.disconnect();
        throw new EcsApiUnexpectedException(ex);
    }

    /**
     * Обработчик (callback) поступающих на WebSocket сообщений.<br>
     * Поступившее сообщение конвертируется в объект-ответ, из которого выбирается идентификатор - correlationId.<br>
     * (На этапе основной работы с СВ - обсуждения, сообщения и т.п, т.е. уже после создания приложения и авторизации,
     * этот идентификатор уникален - представляет собой случайный UUID).<br><br>
     * <p>
     * Затем выполняется поиск в коллекции ожидающих запросов, соответствующих этому идентификатору.<br><br>
     * <p>
     * Если такой запрос обнаружен, он удаляется из коллекции ожидающих, и его "событие-в-будущем"
     * устанавливается в завершенное состояние с полученным объектом-ответом.<br><br>
     * <p>
     * Если такого запроса НЕ обнаружено, то полученный объект-ответ передается в обработчик "внеполосных данных"
     * (см. {@link #EcsWssClient} и {@link IEcsWssClientOutOfBandResponseProcessor})
     *
     * @param message поступившее сообщение
     */
    public void onMessage(String message)
    {
        LOGGER.trace("onMessage() : {}", message);

        // Пробуем сформировать объект-ответ из полученной строки и получить его correlationId (может оказаться null)
        EcsResponseBase response = EcsResponseParser.fromMessage(message);

        String correlationId = (response).getCorrelation().getCorrelationId();

        // Пробуем найти ожидающий ответа запрос с таким correlationId (null - норм)
        SettableFuture<EcsResponseBase> futureRequest = pendingRequests.remove(correlationId);
        if (futureRequest != null)
        {
            futureRequest.set(response);
        }
        else
        {
            OOBprocessor.processOutOfBandResponse(response);
        }
    }

    /**
     * @param uri              URI сервера WebSocket, с которым будет работать данный объект
     * @param keyStoreFilePath path к файлу-хранилищу с сертификатом (.jks-файл)
     * @param keyStorePasswd   пароль к файлу-хранилищу
     * @param keyPasswd        пароль к ключу с сертификатом
     * @param timeout          таймаут подключения
     */
    public void connect(String uri,
                        String keyStoreFilePath, String keyStorePasswd, String keyPasswd,
                        long timeout)
    {
        File keyFile = new File(keyStoreFilePath);
        try (FileInputStream keyFileInputStream = new FileInputStream(keyFile))
        {
            this.wssClient = new WssClient(uri);

            KeyStore keyStore = KeyStore.getInstance(STORETYPE);
            keyStore.load(keyFileInputStream, keyStorePasswd.toCharArray());

            KeyManagerFactory keyManagerFactory = getKeyManagerFactory(keyStore, keyPasswd);

            // Должен будет использоваться
            // TrustManagerFactory trustManagerFactory = getTrustManagerFactory(keyStore);
            TrustManager[] trustAllCerts = new TrustManager[]{  // Create a trust manager that does not validate certificate chains
                    new X509TrustManager()
                    {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers()
                        {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType)
                        {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType)
                        {
                        }
                    }
            };

            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLS");
            // Должен будет использоваться
            // sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustAllCerts, null);

            sslSocketFactory = sslContext.getSocketFactory();
            // Должен будет использоваться
            // (SSLSocketFactory) SSLSocketFactory.getDefault();
            wssClient.setSocket(sslSocketFactory.createSocket());

            LOGGER.trace("connect() : connectBlocking...");
            wssClient.connectBlocking(timeout, TimeUnit.SECONDS);
        }
        catch (URISyntaxException e)
        {   // WssClient :: URI
            throw new EcsApiUriException(e);
        }
        catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | KeyManagementException e)
        {   // KeyStore :: load
            // SSLContext :: init, getInstance
            // SSLSocketFactory :: createSocket
            throw new EcsApiCertificateException(e);
        }
        catch (InterruptedException e)
        {   // WebSocketClient :: connectBlocking
            throw new EcsApiWssException(e);
        }
    }

    private KeyManagerFactory getKeyManagerFactory(KeyStore keyStore, String keyPasswd)
    {
        KeyManagerFactory keyManagerFactory = null;
        try
        {
            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keyPasswd.toCharArray());
        }
        catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e)
        {
            throw new EcsApiCertificateException(e);
        }
        return keyManagerFactory;
    }

    // Должен будет использоваться
    private TrustManagerFactory getTrustManagerFactory(KeyStore keyStore)
    {
        TrustManagerFactory trustManagerFactory = null;
        try
        {
            trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(keyStore);
        }
        catch (KeyStoreException | NoSuchAlgorithmException e)
        {
            throw new EcsApiCertificateException(e);
        }
        return trustManagerFactory;
    }

    public void disconnect()
    {
        // сбрасываем все ожидающие запросы
        for (SettableFuture<EcsResponseBase> futureRequest : pendingRequests.values())
        {
            futureRequest.cancel(true);
        }
        pendingRequests.clear();

        if (wssClient != null)
        {
            wssClient.closeConnection(0, "");
        }
    }

    public EcsResponseBase query(IEcsRequest request, long timeout)
    {
        {
            if (wssClient == null || !wssClient.isOpen() || schedExecSvc == null)
            {
                throw new IllegalStateException("query() while connection not open or executor not existed");
            }
        }
        try
        {
            // СНАЧАЛА создаем-готовим объекты ожидания "события-в-будущем" (СООБЩЕНИЯ-ответа или ТАЙМАУТА)...
            SettableFuture<EcsResponseBase> future = SettableFuture.create();
            FluentFuture<EcsResponseBase> futureMessage = future.withTimeout(timeout, TimeUnit.SECONDS, schedExecSvc);

            // ...заносим этот объект в "очередь запросов ожидающих ответа" (null - норм, хотя и странно)...
            final String correlationId = request.getCorrelation().getCorrelationId();
            if (correlationId != null)
            {
                pendingRequests.put(correlationId, future);
            }

            if (LOGGER.isTraceEnabled())
            {
                LOGGER.trace("query() : {}", request.asMessage());
            }
            // ...и только ПОТОМ собственно отправляем сообщение-запрос.
            wssClient.send(request.asMessage());

            // Такая последовательность сделана для того, чтобы, если "ВСКОРЕ после этого" некий ответ уже успеет прийти
            // и попасть в коллбэк приёма сообщений, то ожидающий его вышесозданный объект уже можно будет найти-взять
            // из "очереди запросов ожидающих ответа".
            // Тут тонкость: увидит ли коллбэк из другого треда этот объект (созданный в нашем треде)?
            // (рассуждения про эту ситуацию - https://shipilev.net/blog/archive/settable-future/)
            // Полагаемся на ConcurrentHashMap, что она правильно решает эти гонки.

            // Возвращаем результат (или таймаут), который положит нам коллбэк приёма сообщений.
            return futureMessage.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            if (e.getCause() instanceof TimeoutException)
            {
                throw new EcsApiTimeoutException(e);
            }
            throw new EcsApiUnexpectedException(e);
        }
    }

    public boolean isOpen()
    {
        if (wssClient != null)
        {
            return wssClient.isOpen();
        }
        return false;
    }

    public boolean isClose()
    {
        return !isOpen();
    }
}
