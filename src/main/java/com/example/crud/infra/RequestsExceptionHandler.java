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
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<ExceptionDTO> cepInvalido(CepInvalidoException e) {
        return ResponseEntity.status(400).body(new ExceptionDTO(e.getMessage(),400));
    }

    @ExceptionHandler(CepNaoEncontradoException.class)
    public ResponseEntity<ExceptionDTO> cepNaoEncontrado(CepNaoEncontradoException e) {
        return ResponseEntity.status(404).body(new ExceptionDTO(e.getMessage(), 404));
    }

    @ExceptionHandler(ViaCepIndisponivelException.class)
    public ResponseEntity<ExceptionDTO> viaCepIndisponivel(ViaCepIndisponivelException e) {
        return ResponseEntity.status(503).body(new ExceptionDTO(e.getMessage(), 503));
    }
}
