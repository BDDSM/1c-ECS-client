/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.e1c.g5.appliedobjects.ecs.crypto.util.AESResult;
import com.e1c.g5.appliedobjects.ecs.crypto.util.AESUtil;
import com.e1c.g5.appliedobjects.ecs.crypto.util.RSAUtil;

public final class Crypto
{
    private static final ObjectMapper ENCRYPTION_OBJECT_MAPPER = new ObjectMapper();

    static {
        ENCRYPTION_OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static String encrypt(Object object, PublicKey publicKey, PrivateKey privateKey)
    {
        try
        {
            byte[] plainBytes = ENCRYPTION_OBJECT_MAPPER.writeValueAsBytes(object);

// ...
            return base64(resultArray);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Unable to encrypt object = " + object);
        }
    }

    private static byte[] concatWithLength(byte[]... objects)
    {
//...
        return buffer.array();
    }

    private static String base64(byte[] array)
    {
        byte[] encoded = Base64.getEncoder().encode(array);
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static <T> T decrypt(PrivateKey privateKey, PublicKey publicKey, String base64Token, Class<T> valueType)
    {
        try
        {
//...
            if (!RSAUtil.verifySignature(body, sign, publicKey))
            {
                throw new IllegalArgumentException("invalid.sign");
            }

            return ENCRYPTION_OBJECT_MAPPER.readValue(body, valueType);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Unable to decrypt encrypted token\n" + base64Token, e);
        }
    }
//...
}
