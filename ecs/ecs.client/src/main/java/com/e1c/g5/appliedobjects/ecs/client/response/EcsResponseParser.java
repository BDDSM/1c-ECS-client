package com.e1c.g5.appliedobjects.ecs.client.response;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.e1c.g5.appliedobjects.ecs.client.IMessagesList;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiProtocolException;
import com.e1c.g5.appliedobjects.ecs.client.response.responses.AuthenticateResponseFactory;
import com.e1c.g5.appliedobjects.ecs.client.response.responses.ErrorResponseFactory;
import com.e1c.g5.appliedobjects.ecs.client.response.responses.GetUnreadConversationMessagesResponseFactory;

/**
 * Протокол представляет собой JSON объект "массив" из 3-х (или 2 из 3-х) разных и НЕизвестных (для маппера) подобъектов:
 * <pre>{@code
 * [ "строка действия", { объект correlationId }, { объект параметров запроса-ответа } ]
 * }</pre>
 * <p>
 * Если бы его самого и ВСЕ его подобъекты преобразовать к "нормальному" виду:
 * <pre>{@code
 * {
 * "название" : "строка действия",
 * "название" : { подобъект correlationId },
 * "название" : { подобъект параметров запроса-ответа },
 * }
 * }</pre>
 * то он бы вытаскивался обратным парсингом сразу в разные сложные-вложенные объекты.<br><br>
 * <p>
 * Разбор ответа проводим строго исходя из формата ответа, или сразу возвращаем ошибку.<br><br>
 * <p>
 * Есть три варианта разборки:<br><br>
 * <p>
 * 1. потоковым парсером, исходя из формата, описываем в виде портянки токенов все возможные ветки потока разбора объекта;<br>
 * --- "нечитаемая" портянка спагетти-кода, читать - ломать голову;<br>
 * --- при изменениях-добавлениях протокола будет головняк с этой портянкой, переписывать - убиться можно;<br><br>
 * <p>
 * 2. "объектовым" маппером, исходя из формата, пытаемся "подправить" строку в нормальный/нужный вид и разом смапить в полный объект;<br>
 * + почти нет кода, использует .class'ы;<br>
 * "-" строку из нашего протокола нужно подправлять в нормальный вид;<br><br>
 * <p>
 * 3. "нодовым" маппером, исходя из формата, явно указываем из какой ноды в какой тип объекта мапить;<br>
 * + мало кода, использует .class'ы;<br>
 * - нужно определять что это за ответ, какие типы подобъектов в нём ожидать и мапить;<br><br>
 * <p>
 * Выбран РАБОЧИЙ вариант 3 - компромиссное решение, один простой удобный понятный switch по определению типов ответа.
 * <p>
 * В результате разбора формируется объект ответа (см. {@link EcsResponseBase}).
 */
public final class EcsResponseParser
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private EcsResponseParser()
    {
    }

    /**
     * Выделяет первую часть из строки ответа, и на основе ее анализа выбирает построение соответствующего объект ответа.
     *
     * @param msg JSON-строка ответа
     * @return объект-ответ конкретного типа (класса)
     */
    public static EcsResponseBase fromMessage(String msg)
    {
        // TODO G5RT -2331:более формально закодить специализированный разбор сообщений "ping" и "40"
        {
            /* ОСОБЫЕ СЛУЧАИ - неожиданные пакеты, которые приходят без запроса. Типа такого:
             *
             * 0{"sid":"6a16a92f-cde9-42de-9d3c-a30da518f600","upgrades":["websocket"],"pingInterval":25000,"pingTimeout":60000}
             *
             * Разобираем их и кладем в фиктивный EcsResponseBase типа "OobResponse", у которого, например,
             * {
             *  Action = "ping"
             *  Correlation = "sid"
             *  Information = вся остальная инфа
             * }
             * Ловить их нужно ПЕРЕД тем как начнется 'json = msg.substring(msg.indexOf("[\""))...'
             * (потому что оно вернет остаток строки после ["websocket...)
             *
             * ПОКА вот так, по-простому, чтоб не париться с мапингом
             */
            if (msg.length() > 45 && "0{\"sid\":\"".equals(msg.substring(0, 9)) && msg.charAt(msg.length() - 1) == '}')
            {
                String sid = msg.substring(9, 45);
                String tail = msg.substring(45 + 2, msg.length() - 1).replaceAll("\"", "\\\\\"");
                msg = "[\"" + ResponseAction.PING + "\"," +
                        "{\"correlationId\":\"" + sid + "\"}," +
                        "{\"info\":\"" + tail + "\"}]";
            }
            if ("40".equals(msg))
            {
                msg = "[\"" + ResponseAction.MAGIC40 + "\",{\"correlationId\":\"x\"},{}]";
            }
        }

        // Выделяем сам JSON из строки протокола (перед ним там "магическое число")
        String json = msg.substring(msg.indexOf("[\""));

        // Выделяем action из первого подобъекта-строки, чтобы определить какой объект ответа строить
        String actionString;
        try
        {
            JsonNode root = OBJECT_MAPPER.readTree(json);
            // 1-й (idx:0) элемент -- объект ResponseAction -- строка
            actionString = OBJECT_MAPPER.convertValue(root.get(0), String.class);
        }
        catch (IOException e)
        {
            throw new EcsApiProtocolException(
                    new IllegalStateException(
                            IMessagesList.Messages
                                    .protocol__cant_transform_JSON_string_to_response_object(), e));
        }

        switch (actionString)
        {
            case ResponseAction.PING:
            case ResponseAction.MAGIC40:
            {
                EcsResponseFactory factory = new OobResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.ERROR:
            {
                EcsResponseFactory factory = new ErrorResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.CREATE_OR_UPDATE_APPLICATION_OK:
            {
                EcsResponseFactory factory = new CreateOrUpdateApplicationResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.ENABLE_APPLICATION_OK:
            {
                EcsResponseFactory factory = new EnableApplicationResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.AUTHENTICATE_OK:
            {
                EcsResponseFactory authenticateResponseFactory = new AuthenticateResponseFactory();
                return authenticateResponseFactory.createResponse(json);
            }
            case ResponseAction.CREATE_CONVERSATION_OK:
            {
                EcsResponseFactory factory = new CreateConversationResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.UPDATE_CONVERSATION_OK:
            {
                EcsResponseFactory factory = new UpdateConversationResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.GET_CONVERSATION_OK:
            {
                EcsResponseFactory factory = new GetConversationResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.GET_UNREAD_CONVERSATIONS_OK:
            {
                EcsResponseFactory factory = new GetUnreadConversationsResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.CREATE_CONVERSATION_MESSAGE_OK:
            {
                EcsResponseFactory factory = new CreateConversationMessageResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.GET_UNREAD_CONVERSATION_MESSAGES_OK:
            {
                EcsResponseFactory factory = new GetUnreadConversationMessagesResponseFactory();
                return factory.createResponse(json);
            }
            case ResponseAction.SET_LAST_READ_MESSAGE_ID_OK:
            {
                EcsResponseFactory factory = new SetLastReadMessageIdResponseFactory();
                return factory.createResponse(json);
            }
        }
        throw new EcsApiProtocolException(
                new IllegalStateException(
                        IMessagesList.Messages
                                .protocol__unknown_response_action_type()));
    }
}
