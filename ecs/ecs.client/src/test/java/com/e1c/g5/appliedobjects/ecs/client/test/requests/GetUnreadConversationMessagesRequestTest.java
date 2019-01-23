/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.test.requests;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.e1c.g5.appliedobjects.ecs.client.request.requests.GetUnreadConversationMessagesRequest;

class GetUnreadConversationMessagesRequestTest
{
    // Создание объекта
    @Test
    void test_CreateInstance_success()
    {
        GetUnreadConversationMessagesRequest requestGetUnreadConversationMessages = new GetUnreadConversationMessagesRequest.Builder()
                .setConversationId(UUID.randomUUID())
                .setFromId(UUID.randomUUID())
                .setLimit(100)
                .build();
    }
}
