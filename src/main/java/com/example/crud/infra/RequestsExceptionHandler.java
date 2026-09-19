package com.example.crud.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestsExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404(){
        ExceptionDTO response = new ExceptionDTO("Data not found with provided ID", 404);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidCepException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidCep(InvalidCepException ex) {
        ExceptionDTO response = new ExceptionDTO(ex.getMessage(), 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleCepNotFound(CepNotFoundException ex) {
        ExceptionDTO response = new ExceptionDTO(ex.getMessage(), 404);
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(ViaCepUnavailableException.class)
    public ResponseEntity<ExceptionDTO> handleViaCepUnavailable(ViaCepUnavailableException ex) {
        ExceptionDTO response = new ExceptionDTO(ex.getMessage(), 503);
        return ResponseEntity.status(503).body(response);
    }
}
