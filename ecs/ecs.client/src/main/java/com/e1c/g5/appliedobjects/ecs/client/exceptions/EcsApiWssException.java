/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.exceptions;

/**
 * Исключение, связанное с WebSocket
 * (ошибки в работе соединения)
 */
public final class EcsApiWssException extends EcsApiExceptionBase
{
    public EcsApiWssException(Throwable cause)
    {
        super(cause);
    }
}
