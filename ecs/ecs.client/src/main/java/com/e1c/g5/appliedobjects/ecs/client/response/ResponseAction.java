/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.response;

/**
 * action -- первая составная часть пакета протокола СВ - строка "действие" (см. {@link EcsResponseBase})
 */
public final class ResponseAction
{
    public static final String ERROR = "error";
    public static final String PING = "ping";
    public static final String MAGIC40 = "40";
    public static final String CREATE_OR_UPDATE_APPLICATION_OK = "createOrUpdateApplicationOk";
    public static final String ENABLE_APPLICATION_OK = "enableApplicationOk";
    public static final String AUTHENTICATE_OK = "authenticateOk";
    public static final String CREATE_CONVERSATION_OK = "createConversationOk";
    public static final String UPDATE_CONVERSATION_OK = "updateConversationOk";
    public static final String GET_CONVERSATION_OK = "getConversationOk";
    public static final String GET_UNREAD_CONVERSATIONS_OK = "getUnreadConversationsOk";
    public static final String CREATE_CONVERSATION_MESSAGE_OK = "createConversationMessageOk";
    public static final String GET_UNREAD_CONVERSATION_MESSAGES_OK = "getUnreadConversationMessagesOk";
    public static final String SET_LAST_READ_MESSAGE_ID_OK = "setLastReadMessageIdOk";

    private String subject = "";

    public ResponseAction()
    {
    }

    public ResponseAction(String subject)
    {
        this.subject = subject;
    }

    @Override
    public String toString()
    {
        return "ResponseAction{" +
                "subject='" + subject + '\'' +
                '}';
    }

    public String getSubject()
    {
        return subject;
    }
}
