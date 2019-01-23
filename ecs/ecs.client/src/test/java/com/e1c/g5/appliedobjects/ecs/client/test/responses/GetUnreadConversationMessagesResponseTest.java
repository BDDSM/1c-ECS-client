/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.test.responses;

import org.junit.jupiter.api.Test;

import com.e1c.g5.appliedobjects.ecs.client.response.responses.GetUnreadConversationMessagesResponse;
import com.e1c.g5.appliedobjects.ecs.client.response.responses.GetUnreadConversationMessagesResponseFactory;

class GetUnreadConversationMessagesResponseTest
{
    // arrange
    public static final String getUnreadConversationMessagesOk = "[\"getUnreadConversationMessagesOk\",{\"correlationId\":\"45f2f0d0-72e4-4a43-b132-aaac69de74db\"},[{\"id\":\"fcf14c39-6463-451a-bc76-9df80b0d568d\",\"conversationId\":\"f21c9726-df2b-4d01-9b73-cf401912604f\",\"createdAt\":\"2018-11-21T09:46:24.776Z\",\"updatedAt\":\"2018-11-21T09:46:24.776Z\",\"authorId\":\"d9d54a68-7211-4cbe-a86e-1d72c7383e0d\",\"type\":\"SERVICE\",\"code\":\"conversation.changed\",\"codeParams\":{\"title\":\"ОбсуждениеG5new\",\"key\":\"converkey_8b405b6a-8453-4ca4-99f6-2fc32eee9c38\",\"members\":{\"deleted\":[],\"created\":[],\"empty\":true},\"empty\":false}},{\"id\":\"c2d60ab2-f6d8-47e8-90a6-af5ccdfa2caa\",\"conversationId\":\"f21c9726-df2b-4d01-9b73-cf401912604f\",\"createdAt\":\"2018-11-21T09:46:24.811Z\",\"updatedAt\":\"2018-11-21T09:46:24.811Z\",\"authorId\":\"7361f39b-4ede-4efe-adb5-7a7022cb61c0\",\"type\":\"TEXT\",\"text\":\"Gimmemoar!!!\",\"recipients\":[\"d9d54a68-7211-4cbe-a86e-1d72c7383e0d\"],\"codeParams\":{\"members\":{\"deleted\":[],\"created\":[],\"empty\":true},\"empty\":true}}]]";

    // Создание объекта
    @Test
    void test_CreateInstance_success()
    {
        GetUnreadConversationMessagesResponseFactory factory = new GetUnreadConversationMessagesResponseFactory();

        // act
        // assert
        GetUnreadConversationMessagesResponse response = factory.createResponse(getUnreadConversationMessagesOk);
    }
}