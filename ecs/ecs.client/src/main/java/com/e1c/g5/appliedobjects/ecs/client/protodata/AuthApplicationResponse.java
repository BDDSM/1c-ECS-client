/*
 * Copyright (C) 2018, 1C
 */
package com.e1c.g5.appliedobjects.ecs.client.protodata;

public final class AuthApplicationResponse extends ApplicationResponse
{
    private IbAuthData data;

    public IbAuthData getData()
    {
        return data;
    }

    public void setData(IbAuthData data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "AuthApplicationResponse{" +
                super.toString() +
                " data=" + data +
                '}';
    }
}