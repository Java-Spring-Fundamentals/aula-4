package com.example.crud.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class RequestsExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404(){
        ExceptionDTO response = new ExceptionDTO("Data not found with provided ID", 404);
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionDTO> handleResponseStatus(
            ResponseStatusException exception
    ) {
        int status = exception.getStatusCode().value();

        ExceptionDTO response = new ExceptionDTO(
                exception.getReason(),
                status
        );

        return ResponseEntity.status(status).body(response);
    }
}
