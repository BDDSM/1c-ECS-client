/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.response;

/**
 * correlation -- вторая составная часть пакета протокола СВ - объект "{ correlationId ... }" (см. {@link EcsResponseBase})
 */
public final class ResponseCorrelation
{
    protected String correlationId = "";

    public ResponseCorrelation()
    {
    }

    public ResponseCorrelation(String correlationId)
    {
        this.correlationId = correlationId;
    }

    @Override
    public String toString()
    {
        return "ResponseCorrelation{" +
                "correlationId='" + correlationId + '\'' +
                '}';
    }

    public String getCorrelationId()
    {
        return correlationId;
    }

    public void setCorrelationId(String correlationId)
    {
        this.correlationId = correlationId;
    }
}
