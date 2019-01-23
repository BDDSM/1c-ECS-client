/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.apidata;

import static com._1c.g5.commons.utils.G5Preconditions.checkIsNotNullOrEmpty;

/**
 * Класс-структура для хранения данных о ключах информационной базы.
 * <br><br>
 * Содержит поля:<br>
 * publicKey    - открытый ключ информационной базы<br>
 * privateKey   - закрытый ключ информационной базы<br>
 */
public final class IbKeys
{
    private final String publicKey;
    private final String privateKey;

    public IbKeys(String publicKey, String privateKey)
    {
        checkIsNotNullOrEmpty(publicKey, "'publicKey' should not be null or empty.");
        checkIsNotNullOrEmpty(privateKey, "'privateKey' should not be null or empty.");

        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Открытый ключ информационной базы
     */
    public String getPublicKey()
    {
        return publicKey;
    }

    /**
     * Закрытый ключ информационной базы
     */
    public String getPrivateKey()
    {
        return privateKey;
    }
}
