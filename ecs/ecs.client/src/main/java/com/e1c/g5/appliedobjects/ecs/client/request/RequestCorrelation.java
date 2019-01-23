/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.request;

import java.util.UUID;

/**
 * correlation -- вторая составная часть пакета протокола СВ - объект "{ correlationId ... }" (см. {@link EcsRequestBase})
 */
public class RequestCorrelation
{
    public static final String CORRELATION_ID_A = "A";
    public static final String CORRELATION_ID_B = "B";

    protected String correlationId;

    public RequestCorrelation(String correlationId)
    {
        this.correlationId = correlationId;
    }

    public RequestCorrelation(UUID correlationId)
    {
        this.correlationId = correlationId.toString();
    }

    @Override
    public String toString()
    {
        return "RequestCorrelation{" +
                "correlationId='" + correlationId + '\'' +
                '}';
    }

    public String getCorrelationId()
    {
        return correlationId;
    }
}
