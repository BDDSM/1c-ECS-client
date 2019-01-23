/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.response.responses;

import com.e1c.g5.appliedobjects.ecs.client.response.EcsResponseFactory;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseAction;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseCorrelation;

public final class GetUnreadConversationMessagesResponseFactory extends EcsResponseFactory
{
    @Override
    public GetUnreadConversationMessagesResponse createResponse(String json)
    {
        parseMessageToResponseSubobjects(json,
                ResponseAction.class,
                ResponseCorrelation.class,
                GetUnreadConversationMessagesResponse.Information.class,
                GetUnreadConversationMessagesResponse.MESSAGES_READER);

        return new GetUnreadConversationMessagesResponse(action, correlation,
                (GetUnreadConversationMessagesResponse.Information) information);
    }
}
