package com.Airtribe.Chronos.job_service.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Converter
public class LocalDateTimeMapConverter implements AttributeConverter<Map<String, LocalDateTime>, String> {

    private final ObjectMapper objectMapper;

    public LocalDateTimeMapConverter() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register support for Java 8 time types
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional, writes ISO format
    }

    @Override
    public String convertToDatabaseColumn(Map<String, LocalDateTime> attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Map<String, LocalDateTime> to JSON", e);
        }
    }

    @Override
    public Map<String, LocalDateTime> convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return objectMapper.readValue(dbData, new TypeReference<HashMap<String, LocalDateTime>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading JSON into Map<String, LocalDateTime>", e);
        }
    }
}