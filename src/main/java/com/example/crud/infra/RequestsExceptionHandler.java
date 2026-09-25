package com.example.crud.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class RequestsExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404(){
        ExceptionDTO response = new ExceptionDTO("Data not found with provided ID", 404);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    public ResponseEntity<ExceptionDTO> threatCepNaoEncontrado(CepNaoEncontradoException ex){
        ExceptionDTO response = new ExceptionDTO(ex.getMessage(), 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ExceptionDTO> threatCepInvalido(){
        ExceptionDTO response = new ExceptionDTO("CEP em formato inválido", 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ExceptionDTO> threatServicoIndisponivel(){
        ExceptionDTO response = new ExceptionDTO("Serviço de CEP indisponível no momento, tente novamente mais tarde", 503);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}