package com.example.crud.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestsExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404(){
        ExceptionDTO response = new ExceptionDTO("Data not found with provided ID", 404);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDTO> cepErrado(IllegalArgumentException ex){
        ExceptionDTO response = new ExceptionDTO(ex.getMessage(), 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionDTO> faltaParametro(MissingServletRequestParameterException ex){
        ExceptionDTO response = new ExceptionDTO("Faltou o parâmetro: " + ex.getParameterName(), 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ViaCepIndisponivelException.class)
    public ResponseEntity<ExceptionDTO> servicoFora(ViaCepIndisponivelException ex){
        ExceptionDTO response = new ExceptionDTO(ex.getMessage(), 503);
        return ResponseEntity.status(503).body(response);
    }
}
