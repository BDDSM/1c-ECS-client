/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.response.responses;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;

import com.e1c.g5.appliedobjects.ecs.client.response.EcsResponseBase;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseAction;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseCorrelation;
import com.e1c.g5.appliedobjects.ecs.client.response.ResponseInformationBase;

/**
 * getUnreadConversationMessagesOk {} -- ответ от СВ
 */
public final class GetUnreadConversationMessagesResponse extends EcsResponseBase
{
    public static final ObjectReader MESSAGES_READER = OBJECT_MAPPER.reader().forType(
            new TypeReference<List<Information.Message>>()
            {
            });

    public GetUnreadConversationMessagesResponse(ResponseAction action, ResponseCorrelation correlation, Information information)
    {
        super(action, correlation, information);
    }

    public static final class Information extends ResponseInformationBase
    {
        @JsonIgnoreProperties(ignoreUnknown = true)
        /**
         * Должен быть STATIC'ом, иначе его невозможно создать при десериализации (Jackson вывалится с ошибкой)
         */
        public static final class Message
        {
            private static final String TYPE_TEXT = "TEXT";

            private UUID id = EcsResponseBase.ZERO_UUID;
            private UUID conversationId = EcsResponseBase.ZERO_UUID;
            private Date createdAt = new Date();
            private Date updatedAt = new Date();
            private UUID authorId = EcsResponseBase.ZERO_UUID;
            private String type = TYPE_TEXT;
            private String text = "";
            private List<UUID> recipients = new ArrayList<>();
            // ...и ещё есть всякое
            // поэтому БЕЗ конструктора, иначе будет ошибка

            @Override
            public String toString()
            {
                return "Message{" +
                        "id=" + id +
                        ", conversationId=" + conversationId +
                        ", createdAt=" + createdAt +
                        ", updatedAt=" + updatedAt +
                        ", authorId=" + authorId +
                        ", type='" + type + '\'' +
                        ", text='" + text + '\'' +
                        ", recipients=" + recipients +
                        '}';
            }

            public UUID getId()
            {
                return id;
            }

            public void setId(UUID id)
            {
                this.id = id;
            }

            public UUID getConversationId()
            {
                return conversationId;
            }

            public void setConversationId(UUID conversationId)
            {
                this.conversationId = conversationId;
            }

            public Date getCreatedAt()
            {
                return createdAt != null ? new Date(createdAt.getTime()) : null;
            }

            public void setCreatedAt(Date createdAt)
            {
                this.createdAt = createdAt != null ? new Date(createdAt.getTime()) : null;
            }

            public Date getUpdatedAt()
            {
                return updatedAt != null ? new Date(updatedAt.getTime()) : null;
            }

            public void setUpdatedAt(Date updatedAt)
            {
                this.updatedAt = updatedAt != null ? new Date(updatedAt.getTime()) : null;
            }

            public UUID getAuthorId()
            {
                return authorId;
            }

            public void setAuthorId(UUID authorId)
            {
                this.authorId = authorId;
            }

            public String getType()
            {
                return type;
            }

            public void setType(String type)
            {
                this.type = type;
            }

            public String getText()
            {
                return text;
            }

            public void setText(String text)
            {
                this.text = text;
            }

            public List<UUID> getRecipients()
            {
                return recipients;
            }

            public void setRecipients(List<UUID> recipients)
            {
                this.recipients = recipients;
            }
        }

        private List<Message> messages = new ArrayList<>();

        public Information(List<Message> messages)
        {
            this.messages = messages;
        }

        @Override
        public String toString()
        {
            return "Information{" +
                    "messages=" + messages +
                    '}';
        }

        public List<Message> getMessages()
        {
            return messages;
        }

        public void setMessages(List<Message> messages)
        {
            this.messages = messages;
        }
    }

    @Override
    public String toString()
    {
        return "GetUnreadConversationMessagesResponse{" +
                "action=" + action +
                ", correlation=" + correlation +
                ", information=" + information +
                '}';
    }

    public Information getInformation()
    {
        return (Information) information;
    }
}
