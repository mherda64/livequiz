package com.example.livequiz.request;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import java.io.IOException;

public final class Mapper
{
    private static ObjectMapper mapper;

    public static ObjectMapper get()
    {
        if (mapper == null)
        {
            mapper = new ObjectMapper();
            mapper.registerModule(new JSR310Module());
        }

        return mapper;
    }

    public static String string(Object data)
    {
        try
        {
            return get().writeValueAsString(data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T objectOrThrow(String data, Class<T> type) throws JsonParseException, JsonMappingException, IOException
    {
        return get().readValue(data, type);
    }

    public static <T> T object(String data, Class<T> type)
    {
        try
        {
            return objectOrThrow(data, type);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}