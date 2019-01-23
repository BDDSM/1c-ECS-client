/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.exceptions;

/**
 * Исключение, означающее, что произошел таймаут
 */
public final class EcsApiTimeoutException extends EcsApiExceptionBase
{
    public EcsApiTimeoutException(Throwable cause)
    {
        super(cause);
    }
}
