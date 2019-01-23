/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.exceptions;

/**
 * Базовый класс для иерархии исключений Ecs
 */
public abstract class EcsApiExceptionBase extends RuntimeException
{
    public EcsApiExceptionBase(Throwable cause)
    {
        super(cause);
    }
}
