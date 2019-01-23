/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.apidata;

import java.util.UUID;

import static com._1c.g5.commons.utils.G5Preconditions.checkIsNotNullOrEmpty;

/**
 * Класс-структура для хранения данных о приложении в системе взаимодействия.
 * <br><br>
 * Содержит поля:<br>
 * applicationId    - идентификатор приложения в системе взаимодействия<br>
 * ecsPublicKey     - публичный ключ приложения в системе взаимодействия<br>
 */
public final class AppParams
{
    private final UUID applicationId;
    private final String ecsPublicKey;

    public AppParams(UUID applicationId, String ecsPublicKey)
    {
        checkIsNotNullOrEmpty(ecsPublicKey, "'ecsPublicKey' should not be null or empty.");

        this.applicationId = applicationId;
        this.ecsPublicKey = ecsPublicKey;
    }

    @Override
    public String toString()
    {
        return "AppParams{" +
                "applicationId='" + applicationId + '\'' +
                ", ecsPublicKey='" + ecsPublicKey + '\'' +
                '}';
    }

    /**
     * Идентификатор приложения в системе взаимодействия
     */
    public UUID getApplicationId()
    {
        return applicationId;
    }

    /**
     * Публичный ключ приложения в системе взаимодействия
     */
    public String getEcsPublicKey()
    {
        return ecsPublicKey;
    }
}
