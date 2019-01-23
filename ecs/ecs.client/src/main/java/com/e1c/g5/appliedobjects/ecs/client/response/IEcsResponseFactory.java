package com.e1c.g5.appliedobjects.ecs.client.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface IEcsResponseFactory
{
    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    default <T> T json2object(JsonNode json, Class<T> classType)
    {
        return OBJECT_MAPPER.convertValue(json, classType);
    }
}
