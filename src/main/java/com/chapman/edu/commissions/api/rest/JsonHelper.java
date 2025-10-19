package com.chapman.edu.commissions.api.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Helper class for JSON serialization and deserialization.
 *
 * This class demonstrates the Single Responsibility Principle (SRP) by handling
 * only JSON conversion logic. It uses the Gson library to convert between Java
 * objects and JSON strings.
 *
 * Concepts demonstrated:
 * - Custom type adapters for LocalDate and LocalDateTime
 * - Singleton pattern for Gson instance
 * - Null-safe serialization
 */
public class JsonHelper {

    // Singleton Gson instance configured with custom type adapters
    // This ensures consistent JSON formatting across the entire application
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()  // Makes JSON output human-readable
            .serializeNulls()     // Include null fields in JSON output
            // Custom serializer for LocalDate (converts to ISO-8601 format: yyyy-MM-dd)
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
                (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            // Custom deserializer for LocalDate (parses from ISO-8601 format)
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                (json, typeOfT, context) -> LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            // Custom serializer for LocalDateTime (converts to ISO-8601 format with time)
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            // Custom deserializer for LocalDateTime (parses from ISO-8601 format with time)
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();

    /**
     * Convert a Java object to JSON string.
     *
     * @param object The object to serialize
     * @return JSON string representation
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    /**
     * Convert a JSON string to a Java object of the specified type.
     *
     * @param json The JSON string to deserialize
     * @param classOfT The class of the target object
     * @param <T> The type of the target object
     * @return The deserialized object
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * Get the configured Gson instance.
     * Useful for advanced serialization scenarios.
     *
     * @return The Gson instance
     */
    public static Gson getGson() {
        return GSON;
    }
}