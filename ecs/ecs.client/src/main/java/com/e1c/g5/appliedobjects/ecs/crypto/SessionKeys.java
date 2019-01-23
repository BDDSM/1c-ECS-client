/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.crypto;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import com.e1c.g5.appliedobjects.ecs.crypto.util.RSAUtil;

/**
 * Класс для хранения набора ключей для сессии связи с системой взаимодействия:<br>
 * - открытый и закрытый ключи информационной базы;<br>
 * - открытый ключ системы взаимодействия.<br>
 */
public class SessionKeys
{
    // Ключи ИБ
    private PublicKey ibPublicKey;
    private PrivateKey ibPrivateKey;

    // Ключ СВ
    private PublicKey ecsPublicKey;

    public static PublicKey toPublicKey(String source)
    {
        return RSAUtil.bytesToPublicKey(Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8)));
    }

    public static PrivateKey toPrivateKey(String source)
    {
        return RSAUtil.bytesToPrivateKey(Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8)));
    }

    public void setIbPublicKey(String keyAsBase64)
    {
        ibPublicKey = toPublicKey(keyAsBase64);
    }

    public PublicKey getIbPublicKey()
    {
        return ibPublicKey;
    }

    public void setIbPrivateKey(String keyAsBase64)
    {
        ibPrivateKey = toPrivateKey(keyAsBase64);
    }

    public PrivateKey getIbPrivateKey()
    {
        return ibPrivateKey;
    }

    public void setEcsPublicKey(String keyAsBase64)
    {
        ecsPublicKey = toPublicKey(keyAsBase64);
    }

    public PublicKey getEcsPublicKey()
    {
        return ecsPublicKey;
    }
}
