/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.test.requests;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.e1c.g5.appliedobjects.ecs.client.request.requests.AuthenticateRequestPhase1;

class AuthenticateRequestPhase1Test
{
    // Создание объекта
    @Test
    void test_CreateInstance_success()
    {
        AuthenticateRequestPhase1 requestAuthenticate = new AuthenticateRequestPhase1.Builder()
                .setApplicationId(UUID.randomUUID())
                .build();
    }
}
