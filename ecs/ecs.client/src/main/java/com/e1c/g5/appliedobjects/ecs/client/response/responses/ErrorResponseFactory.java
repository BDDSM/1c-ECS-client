/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.response.responses;

import com.e1c.g5.appliedobjects.ecs.client.response.EcsResponseFactory;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseAction;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseCorrelation;

public final class ErrorResponseFactory extends EcsResponseFactory
{
    @Override
    public ErrorResponse createResponse(String json)
    {
        parseMessageToResponseSubobjects(json,
                ResponseAction.class,
                ResponseCorrelation.class,
                ErrorResponse.Information.class,
                null);

        return new ErrorResponse(action, correlation, (ErrorResponse.Information) information);
    }
}
