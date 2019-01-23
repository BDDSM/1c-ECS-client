/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.test.requests;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.e1c.g5.appliedobjects.ecs.client.request.requests.AuthenticateRequestPhase2;

class AuthenticateRequestPhase2Test
{
    // Создание объекта
    @Test
    void test_CreateInstance_success()
    {
        AuthenticateRequestPhase2 requestAuthenticatePhase2 = new AuthenticateRequestPhase2.Builder()
                .setApplicationId(UUID.randomUUID())
                .setRecoveryData(UUID.randomUUID().toString())
                .build();
    }
}
