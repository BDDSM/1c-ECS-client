/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.test.responses;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.e1c.g5.appliedobjects.ecs.client.response.responses.AuthenticateResponse;
import com.e1c.g5.appliedobjects.ecs.client.response.responses.AuthenticateResponseFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticateResponseTest
{
    // arrange
    public static final String authenticateOk = "[\"authenticateOk\",{\"correlationId\":\"B\"},{\"id\":\"7361f39b-4ede-4efe-adb5-7a7022cb61c0\",\"clientId\":\"40e84f9b-c4b9-4fa4-8def-91b943fc3e88\",\"applicationId\":\"6f60b48b-1f83-4baa-bcb3-5c569d610ea2\",\"subscriberId\":\"ce493ee3-a6b2-419e-87c2-083b94ab0515\",\"subscriberBucketId\":43,\"version\":4,\"privileged\":false,\"sessionId\":\"269390ad-b14d-40fa-9e9c-d5dc2821f0de\",\"features\":{\"videoEnabled\":true,\"conferenceMemberLimit\":10,\"storageEnabled\":true},\"locale\":\"en_US\",\"authType\":\"FULL\"}]";

    // Создание объекта
    @Test
    void test_CreateInstance_success()
    {
        AuthenticateResponseFactory factory = new AuthenticateResponseFactory();

        // act
        // assert
        AuthenticateResponse response = factory.createResponse(authenticateOk);
    }

    @Test
    void GIVEN__authenticateOk__WHEN__ID_isSet__THEN__ID_isParsed()
    {
        // arrange
        UUID ecsLoginId = UUID.fromString("7361f39b-4ede-4efe-adb5-7a7022cb61c0");

        StringBuilder responseAuthenticateOk = new StringBuilder();
        responseAuthenticateOk.append("[\"responseAuthenticateOk\",{\"correlationId\":\"B\"},{\"id\":\"");
        responseAuthenticateOk.append(ecsLoginId.toString());
        responseAuthenticateOk.append("\",\"clientId\":\"40e84f9b-c4b9-4fa4-8def-91b943fc3e88\",\"applicationId\":\"6f60b48b-1f83-4baa-bcb3-5c569d610ea2\",\"subscriberId\":\"ce493ee3-a6b2-419e-87c2-083b94ab0515\",\"subscriberBucketId\":43,\"version\":4,\"privileged\":false,\"sessionId\":\"269390ad-b14d-40fa-9e9c-d5dc2821f0de\",\"features\":{\"videoEnabled\":true,\"conferenceMemberLimit\":10,\"storageEnabled\":true},\"locale\":\"en_US\",\"authType\":\"FULL\"}]");

        AuthenticateResponseFactory factory = new AuthenticateResponseFactory();

        // act
        AuthenticateResponse response = factory.createResponse(responseAuthenticateOk.toString());
        UUID getEcsLoginid = response.getInformation().getEcsLoginid();

        // assert
        assertEquals(ecsLoginId, getEcsLoginid);
    }
}
