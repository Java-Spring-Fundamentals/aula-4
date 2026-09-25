package com.example.crud.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ExceptionDTO> threatInvalidCep(){
        ExceptionDTO response = new ExceptionDTO("Invalid CEP format, it must have 8 digits", 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threatCepNotFound(){
        ExceptionDTO response = new ExceptionDTO("CEP not found", 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ViaCepUnavailableException.class)
    public ResponseEntity<ExceptionDTO> threatViaCepUnavailable(){
        ExceptionDTO response = new ExceptionDTO("ViaCEP service is unavailable", 503);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
