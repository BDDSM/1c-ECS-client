/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.request.requests;

import java.util.UUID;

import com.e1c.g5.appliedobjects.ecs.client.request.EcsRequestBase;
import com.e1c.g5.appliedobjects.ecs.client.request.RequestAction;
import com.e1c.g5.appliedobjects.ecs.client.request.RequestCorrelation;

/**
 * getUnreadConversationMessages {} - запрос на СВ
 */
public final class GetUnreadConversationMessagesRequest extends EcsRequestBase<GetUnreadConversationMessagesRequest.Information>
{
    /**
     * @param correlationId  ИД запрос-ответа
     * @param conversationId ИД обсуждения
     * @param fromId         начиная с какого сообщения
     * @param limit          ограничить количество сообщений
     */
    private GetUnreadConversationMessagesRequest(UUID correlationId, UUID conversationId, UUID fromId, int limit)
    {
        this.action = new RequestAction(RequestAction.GET_UNREAD_CONVERSATION_MESSAGES);
        this.correlation = new RequestCorrelation(correlationId);
        this.information = new Information(conversationId, fromId, limit);
    }

    public static final class Information
    {
        private UUID conversationId;
        private UUID fromId;
        private int limit;

        public Information(UUID conversationId, UUID fromId, int limit)
        {
            this.conversationId = conversationId;
            this.fromId = fromId;
            this.limit = limit;
        }

        public UUID getConversationId()
        {
            return conversationId;
        }

        public UUID getFromId()
        {
            return fromId;
        }

        public int getLimit()
        {
            return limit;
        }
    }

    public static final class Builder
    {
        private static final UUID FROM_ID_NOT_SET = null;

        // для запроса генерируется случайный идентификатор
        private UUID correlationId = UUID.randomUUID();
        private UUID conversationId = EcsRequestBase.ZERO_UUID;
        private UUID fromId = FROM_ID_NOT_SET;
        private int limit = 0;

        public Builder setConversationId(UUID conversationId)
        {
            this.conversationId = conversationId;
            return this;
        }

        public Builder setFromId(UUID fromId)
        {
            this.fromId = fromId;
            return this;
        }

        public Builder setLimit(int limit)
        {
            this.limit = limit;
            return this;
        }

        /**
         * Сборка объекта {@link GetUnreadConversationMessagesRequest}
         */
        public GetUnreadConversationMessagesRequest build()
        {
            return new GetUnreadConversationMessagesRequest(
                    correlationId,
                    conversationId,
                    fromId,
                    limit);
        }
    }
}
