package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        try {
            return objectMapper.readValue(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize JSON", e);
        }
    }

    public static void writeJson(HttpServletResponse response, Object obj) {
        response.setContentType("application/json; charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(toJson(obj));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON response", e);
        }
    }
}
