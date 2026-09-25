package com.example.crud.infra;

import java.time.Instant;
import java.util.Map;

public record ExceptionDTO(
        Instant timestamp,
        int status,
        String message,
        String path,
        Map<String, String> fields
) {
    public ExceptionDTO(int status, String message, String path) {
        this(Instant.now(), status, message, path, Map.of());
    }
}
