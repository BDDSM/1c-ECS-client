/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.protodata;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ApplicationResponse
{
    private Date expirationTime;

    @JsonProperty("cookie")
    private UUID sessionId;

    public Date getExpirationTime()
    {
        return expirationTime != null ? new Date(expirationTime.getTime()) : null;
    }

    public void setExpirationTime(Date expirationTime)
    {
        this.expirationTime = expirationTime != null ? new Date(expirationTime.getTime()) : null;
    }

    public UUID getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(UUID sessionId)
    {
        this.sessionId = sessionId;
    }

    @Override
    public String toString()
    {
        return "ApplicationResponse{" +
                "expirationTime=" + expirationTime +
                ", sessionId=" + sessionId +
                '}';
    }
}