/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Базовый класс "ответ от СВ"
 * <br><br>
 * Протокол представляет собой JSON объект "массив" из 3-х (или 2 из 3-х) разных подобъектов (см. {@link EcsResponseParser}).
 * <br><br>
 * В результате разбора формируется объект ответа, содержащий 3 подобъекта, из которых состоит сообщение протокола СВ:<br>
 * - подобъект "действие"  ({@link ResponseAction}),<br>
 * - подобъект "связка" ({@link ResponseCorrelation}),<br>
 * - подобъект "информация" ({@link ResponseInformationBase}) с содержимым разного формата - строка, объект, массив; может ОТСУТСТВОВАТЬ.
 */
public abstract class EcsResponseBase implements IEcsResponse
{
    /**
     * третий элемент протокола - "информация"
     */
    protected ResponseAction action;

    /**
     * второй элемент протокола - "связка"
     */
    protected ResponseCorrelation correlation;

    /**
     * первый элемент протокола - "действие"
     */
    protected ResponseInformationBase information;

    // маппер для создания ридера; используется в некоторых типах ответов
    protected final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // "полностью нулевой" UUID для использования в качестве инциализатора
    public static final UUID ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public EcsResponseBase(ResponseAction action, ResponseCorrelation correlation, ResponseInformationBase information)
    {
        this.action = action;
        this.correlation = correlation;
        this.information = information;
    }

    public ResponseAction getAction()
    {
        return action;
    }

    public ResponseCorrelation getCorrelation()
    {
        return correlation;
    }

    public ResponseInformationBase getInformation()
    {
        return information;
    }
}
