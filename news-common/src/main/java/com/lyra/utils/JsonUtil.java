package com.lyra.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String objectForJson(Object data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T>T jsonToObject(String jsonData, Class<T> valueType) {
        try {
            T t = objectMapper.readValue(jsonData, valueType);
            return t;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
