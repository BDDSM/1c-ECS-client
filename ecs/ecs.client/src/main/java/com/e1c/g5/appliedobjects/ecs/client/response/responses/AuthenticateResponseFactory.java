/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.response.responses;

import com.e1c.g5.appliedobjects.ecs.client.response.EcsResponseFactory;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseAction;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseCorrelation;

public final class AuthenticateResponseFactory extends EcsResponseFactory
{
    @Override
    public AuthenticateResponse createResponse(String json)
    {
        parseMessageToResponseSubobjects(json,
                ResponseAction.class,
                ResponseCorrelation.class,
                AuthenticateResponse.Information.class,
                null);

        return new AuthenticateResponse(action, correlation, (AuthenticateResponse.Information) information);
    }
}
