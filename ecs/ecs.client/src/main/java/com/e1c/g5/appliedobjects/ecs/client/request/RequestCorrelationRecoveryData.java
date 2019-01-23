/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.request;

/**
 * correlation c доп. полем recoveryData
 */
public final class RequestCorrelationRecoveryData extends RequestCorrelation
{
    private String recoveryData;

    public RequestCorrelationRecoveryData(String correlationId, String recoveryData)
    {
        super(correlationId);
        this.recoveryData = recoveryData;
    }

    @Override
    public String toString()
    {
        return "RequestCorrelationRecoveryData{" +
                "recoveryData='" + recoveryData + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }

    public String getRecoveryData()
    {
        return recoveryData;
    }
}
