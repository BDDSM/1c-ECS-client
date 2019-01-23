/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.exceptions;

/**
 * Исключение, которое не относится остальным явно выделенным спецификам
 * (например, исключения исполнения потоков)
 */
public final class EcsApiUnexpectedException extends EcsApiExceptionBase
{
    public EcsApiUnexpectedException(Throwable cause)
    {
        super(cause);
    }
}
