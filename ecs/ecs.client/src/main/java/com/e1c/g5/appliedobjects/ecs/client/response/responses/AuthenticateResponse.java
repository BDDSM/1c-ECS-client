/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.response.responses;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.e1c.g5.appliedobjects.ecs.client.response.EcsResponseBase;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseAction;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseCorrelation;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseInformationBase;

/**
 * authenticateOk {} -- ответ от СВ
 */
public final class AuthenticateResponse extends EcsResponseBase
{
    public AuthenticateResponse(ResponseAction action, ResponseCorrelation correlation, Information information)
    {
        super(action, correlation, information);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Information extends ResponseInformationBase
    {
        /**
         * В этом объекте-ответе инфа от СВ как-то мутно "обозвана"
         */
        @JsonProperty("id")
        private UUID ecsLoginid = EcsResponseBase.ZERO_UUID;
        @JsonProperty("clientId")
        private UUID applicationId = EcsResponseBase.ZERO_UUID;
        @JsonProperty("applicationId")
        private UUID someUnknownId = EcsResponseBase.ZERO_UUID;
        private UUID subscriberId = EcsResponseBase.ZERO_UUID;
        private UUID sessionId = EcsResponseBase.ZERO_UUID;
        // ...и ещё есть всякое
        // поэтому БЕЗ конструктора, иначе будет ошибка

        @Override
        public String toString()
        {
            return "Information{" +
                    "ecsLoginid=" + ecsLoginid +
                    ", applicationId=" + applicationId +
                    ", someUnknownId=" + someUnknownId +
                    ", subscriberId=" + subscriberId +
                    ", sessionId=" + sessionId +
                    '}';
        }

        public UUID getEcsLoginid()
        {
            return ecsLoginid;
        }

        public UUID getApplicationId()
        {
            return applicationId;
        }

        public UUID getSomeUnknownId()
        {
            return someUnknownId;
        }

        public UUID getSubscriberId()
        {
            return subscriberId;
        }

        public UUID getSessionId()
        {
            return sessionId;
        }

    }

    @Override
    public String toString()
    {
        return "AuthenticateResponse{" +
                "action=" + action +
                ", correlation=" + correlation +
                ", information=" + information +
                '}';
    }

    public Information getInformation()
    {
        return (Information) information;
    }
}
