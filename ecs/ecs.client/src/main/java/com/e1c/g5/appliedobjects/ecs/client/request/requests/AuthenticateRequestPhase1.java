/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.request.requests;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.e1c.g5.appliedobjects.ecs.client.request.EcsRequestBase;
import com.e1c.g5.appliedobjects.ecs.client.request.RequestAction;
import com.e1c.g5.appliedobjects.ecs.client.request.RequestCorrelation;

/**
 * authenticate {} -- запрос на СВ, фаза 1 (начало)
 */
public final class AuthenticateRequestPhase1 extends EcsRequestBase<AuthenticateRequestPhase1.Information>
{
    /**
     * @param applicationId UUID приложения
     */
    public AuthenticateRequestPhase1(UUID applicationId)
    {
        this.action = new RequestAction(RequestAction.AUTHENTICATE);
        this.correlation = new RequestCorrelation(RequestCorrelation.CORRELATION_ID_A);
        this.information = new Information(applicationId);
    }

    public static final class Information
    {
        @JsonProperty("clientId")
        private UUID applicationId;

        Information(UUID applicationId)
        {
            this.applicationId = applicationId;
        }

        public UUID getApplicationId()
        {
            return applicationId;
        }
    }

    public static final class Builder
    {
        private UUID applicationId = UUID.randomUUID();

        public Builder setApplicationId(UUID applicationId)
        {
            this.applicationId = applicationId;
            return this;
        }

        /**
         * Сборка объекта {@link AuthenticateRequestPhase1}
         */
        public AuthenticateRequestPhase1 build()
        {
            return new AuthenticateRequestPhase1(applicationId);
        }
    }
}
