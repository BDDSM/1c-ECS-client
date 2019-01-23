/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.request;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.e1c.g5.appliedobjects.ecs.client.IMessagesList;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiProtocolException;

/**
 * Базовый класс "запрос на СВ"
 * <br>
 * Содержит 3 части, из которых состоит сообщение протокола СВ:<br>
 * - строка "действие" ({@link RequestAction})<br>
 * - объект "correlation" ({@link RequestCorrelation})<br>
 * - поле с информацией разного формата
 */
public abstract class EcsRequestBase<T> implements IEcsRequest
{
    /**
     * первый элемент протокола - "действие"
     */
    protected RequestAction action;
    /**
     * второй элемент протокола - "связка"
     */
    protected RequestCorrelation correlation;
    /**
     * третий элемент протокола - "информация"
     */
    protected T information;

    // "полностью нулевой" UUID для использования в качестве инциализатора
    public static final UUID ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    // объекты для мапинга JSON
    protected static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    protected static JsonFactory JSON_FACTORY = new JsonFactory();

    // magic string протокола СВ, требование разработчиков протокола (подробности не описаны).
    protected static final String REQUEST_PREPENDER = "42";

    /**
     * @return Конвертирует объект в JSON-строку
     */
    protected String pojoToJsonString(Object pojo)
    {
        StringWriter sw = new StringWriter();
        try
        {
            JsonGenerator jg = JSON_FACTORY.createGenerator(sw);
            // для более удобной отладки можно включить jg.useDefaultPrettyPrinter();
            OBJECT_MAPPER.writeValue(jg, pojo);
        }
        catch (IOException e)
        {
            throw new EcsApiProtocolException(
                    new IllegalStateException(
                            IMessagesList.Messages
                                    .protocol__cant_transform_request_object_to_JSON_string(), e));
        }
        return sw.toString();
    }

    /**
     * Формирует строку запроса согласно протоколу СВ.<br><br>
     * Заносит составные части сообщения в массив и переводит его в JSON-строку, используя pojoToJsonString().<br>
     * С головы пакета добавляется "магическое число" 42 - оно что-то означает согласно протоколу.
     *
     * @return строка запроса для отправки на СВ
     * @see #pojoToJsonString
     */
    public String asMessage()
    {
        final ArrayList<Object> jsObj = new ArrayList<>();

        jsObj.add(action.getSubject());
        jsObj.add(correlation);
        jsObj.add(information);

        String jsonAsString = pojoToJsonString(jsObj);

        return REQUEST_PREPENDER + jsonAsString;
    }

    public RequestAction getAction()
    {
        return action;
    }

    public RequestCorrelation getCorrelation()
    {
        return correlation;
    }

    public T getInformation()
    {
        return information;
    }
}
