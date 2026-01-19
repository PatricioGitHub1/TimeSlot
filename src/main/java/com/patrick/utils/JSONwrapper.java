package com.patrick.utils;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

import java.util.List;
import java.util.Map;

public class JSONwrapper {

    public static <T> T convertToObject(Class<T> clazz, String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return (T) mapper.readValue(jsonString, clazz);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonNode getJsonNode(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(jsonString);
        } catch(JacksonException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> convertToList(Class<T> clazz, JsonNode node, String key) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectReader listReader = mapper.readerForListOf(clazz);
            return listReader.readValue(node.get(key));

        } catch(JacksonException e) {
            e.printStackTrace();
            return null;
        }
    }
}
