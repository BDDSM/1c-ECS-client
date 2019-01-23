/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.exceptions;

/**
 * Исключение, связанное с протоколом
 * (например, неожиданный тип ответа, или ответ, означающий ошибку)
 */
public final class EcsApiProtocolException extends EcsApiExceptionBase
{
    public EcsApiProtocolException(Throwable cause)
    {
        super(cause);
    }
}
