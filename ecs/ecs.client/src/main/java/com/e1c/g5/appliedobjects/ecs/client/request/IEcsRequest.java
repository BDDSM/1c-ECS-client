/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.request;

/**
 * Иерархия "запрос на СВ"
 */
public interface IEcsRequest
{
    /**
     * @return строка запроса в формате протокола СВ
     */
    String asMessage();

    /**
     * @return объект action
     */
    RequestAction getAction();

    /**
     * @return объект correlation
     */
    RequestCorrelation getCorrelation();
}
