/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.test;

import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiUnexpectedException;
import com.e1c.g5.appliedobjects.ecs.client.exceptions.EcsApiUriException;
import com.e1c.g5.appliedobjects.ecs.client.wss.EcsWssClient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class EcsWssClientTests
{
    public static final String DEV_SERVER_URI = "wss://develop.ecs.dept07:9094?transport=websocket&version=4";
    public static final String UNCONNECTABLE_SERVER_URI = "wss://FAIL.TO.CONNECT";
    public static final String WRONG_URI = "ошибка://";

    private static final String KEY_STORE_FILE_PATH = "C:\\keystore.jks";
    private static final String KEY_STORE_PASSWD = "passpass";
    private static final String KEY_PASSWD = "passpass";

    private EcsWssClient ecsWssClient;// = new EcsWssClient(System.out::println);
    private EcsWssClient ecsWssClient_spy;// = Mockito.spy(ecsWssClient);

    @BeforeEach
    void setUp()
    {
        ecsWssClient = new EcsWssClient(System.out::println, Executors.newScheduledThreadPool(1));
        ecsWssClient_spy = Mockito.spy(ecsWssClient);

        doNothing().when(ecsWssClient_spy).connect(anyString(), anyString(), anyString(), anyString(), anyLong());
    }

    // Создание объекта
    @Test
    void test_CreateInstance_success()
    {
        // должны попасть сюда имея успешно созданные объекты
        ecsWssClient_spy.connect("", KEY_STORE_FILE_PATH, KEY_STORE_PASSWD, KEY_PASSWD, 0);
        verify(ecsWssClient_spy).connect(anyString(), anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    void GIVEN__Disconnected__WHEN__isOpened__THEN__false()
    {
        assertFalse(ecsWssClient.isOpen());
    }

    @Test
    void GIVEN__Disconnected__WHEN__isClosed__THEN__true()
    {
        assertTrue(ecsWssClient.isClose());
    }

    @Test
    void GIVEN__Disconnected__WHEN__onClose_called__THEN__ok()
    {
        ecsWssClient.onClose();
        assertFalse(ecsWssClient.isOpen());
        assertTrue(ecsWssClient.isClose());
        // (пере)проверяем повторный вызов disconnect() на уже-closed объекте
        ecsWssClient.onClose();
        assertFalse(ecsWssClient.isOpen());
        assertTrue(ecsWssClient.isClose());
    }

    @Test
    void GIVEN__Disconnected__WHEN__onOpen_called__THEN__ok()
    {
        ecsWssClient.onOpen();
    }

    @Test
    void GIVEN__Disconnected__WHEN__onError_called__THEN__throws_EcsApiUnexpectedException_and_isOpen_false()
    {
        assertThrows(EcsApiUnexpectedException.class, () -> ecsWssClient.onError(new IllegalStateException("")));
        assertFalse(ecsWssClient.isOpen());
    }

//    @Test
//    void GIVEN__Disconnected__WHEN__connect_to_dev_uri__THEN__success_and_isOpen_true()
//    {
//        ecsWssClient.connect(DEV_SERVER_URI, 10);
//        assertTrue(ecsWssClient.isOpen());
//    }

    @Test
    void GIVEN__Disconnected__WHEN__connect_to_wrong_uri__THEN__throws_exception_EcsApiUriException()
    {
        assertThrows(EcsApiUriException.class,
                () -> ecsWssClient.connect(WRONG_URI, KEY_STORE_FILE_PATH, KEY_STORE_PASSWD, KEY_PASSWD, 10));
    }

//    @Test
//    void onMessage()
//    {
//    }

    @AfterEach
    void tearDown()
    {
        ecsWssClient.disconnect();
    }
}
