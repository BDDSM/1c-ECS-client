/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.protodata;

import java.util.UUID;

public final class IbAuthData
{
    private String login;
    private UUID clientId;
    private String name;
    private String fullName;
    private boolean privileged;

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public UUID getClientId()
    {
        return clientId;
    }

    public void setClientId(UUID clientId)
    {
        this.clientId = clientId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public boolean isPrivileged()
    {
        return privileged;
    }

    public void setPrivileged(boolean privileged)
    {
        this.privileged = privileged;
    }

    @Override
    public String toString()
    {
        return "IbAuthData{" +
                "login='" + login + '\'' +
                ", clientId=" + clientId +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", privileged=" + privileged +
                '}';
    }
}
