package com.mfm.user.access_service.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class JsonUtil {

    private static ObjectMapper MAPPER;

    private JsonUtil() {
    }

    @PostConstruct
    public static void init() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(module);
        setMapper(mapper);
    }

    @SneakyThrows
    public static <T> T toObject(InputStream input, Class<T> type) {
        return MAPPER.readValue(input, type);
    }

    @SneakyThrows
    public static <T> T toObject(String json, Class<T> type) {
        return MAPPER.readValue(json, type);
    }

    private static void setMapper(ObjectMapper mapper) {
        MAPPER = mapper;
    }

    private static ObjectMapper getMapper() {
        if (MAPPER == null) {
            init();
        }
        return MAPPER;
    }
}
