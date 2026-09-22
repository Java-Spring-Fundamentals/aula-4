package com.example.crud.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestsExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404() {
        ExceptionDTO response = new ExceptionDTO(
                "Data not found with provided ID",
                404
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDTO> handleBadRequest(
            IllegalArgumentException exception
    ) {
        ExceptionDTO response = new ExceptionDTO(
                exception.getMessage(),
                400
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionDTO> handleServiceUnavailable(
            IllegalStateException exception
    ) {
        ExceptionDTO response = new ExceptionDTO(
                exception.getMessage(),
                503
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}