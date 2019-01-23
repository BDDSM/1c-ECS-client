/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.request;

/**
 * action -- первая составная часть пакета протокола СВ - строка "действие" (см. {@link EcsRequestBase})
 */
public final class RequestAction
{
    public static final String CREATE_OR_UPDATE_APPLICATION = "createOrUpdateApplication";
    public static final String ENABLE_APPLICATION = "enableApplication";
    public static final String AUTHENTICATE = "authenticate";
    public static final String CREATE_CONVERSATION = "createConversation";
    public static final String UPDATE_CONVERSATION = "updateConversation";
    public static final String GET_CONVERSATION = "getConversation";
    public static final String CREATE_CONVERSATION_MESSAGE = "createConversationMessage";
    public static final String GET_UNREAD_CONVERSATIONS = "getUnreadConversations";
    public static final String GET_UNREAD_CONVERSATION_MESSAGES = "getUnreadConversationMessages";
    public static final String SET_LAST_READ_MESSAGE_ID = "setLastReadMessageId";

    private String subject;

    public RequestAction(String subject)
    {
        this.subject = subject;
    }

    public String getSubject()
    {
        return subject;
    }

}
