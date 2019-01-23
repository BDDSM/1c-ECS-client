/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.request.requests;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.e1c.g5.appliedobjects.ecs.client.request.EcsRequestBase;
import com.e1c.g5.appliedobjects.ecs.client.request.RequestAction;
import com.e1c.g5.appliedobjects.ecs.client.request.RequestCorrelation;
import com.e1c.g5.appliedobjects.ecs.client.request.RequestCorrelationRecoveryData;

import static com._1c.g5.commons.utils.G5Preconditions.checkIsNotNullOrEmpty;

/**
 * authenticate {} -- запрос на СВ, фаза 2 (до-аутентификация)
 */
public final class AuthenticateRequestPhase2 extends EcsRequestBase<AuthenticateRequestPhase2.Information>
{
    /**
     * @param applicationId UUID приложения
     * @param recoveryData  зашифрованный подписаннный пакет с информацией от ИБ к СВ
     */
    public AuthenticateRequestPhase2(UUID applicationId, String recoveryData)
    {
        checkIsNotNullOrEmpty(recoveryData, "'recoveryData' should not be null or empty.");

        this.action = new RequestAction(RequestAction.AUTHENTICATE);
        this.correlation = new RequestCorrelationRecoveryData(RequestCorrelation.CORRELATION_ID_B, recoveryData);
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
        private UUID applicationId = EcsRequestBase.ZERO_UUID;
        private String recoveryData;

        public Builder setApplicationId(UUID applicationId)
        {
            this.applicationId = applicationId;
            return this;
        }

        public Builder setRecoveryData(String recoveryData)
        {
            this.recoveryData = recoveryData;
            return this;
        }

        /**
         * Сборка объекта {@link AuthenticateRequestPhase2}
         */
        public AuthenticateRequestPhase2 build()
        {
            return new AuthenticateRequestPhase2(applicationId, recoveryData);
        }
    }
}
