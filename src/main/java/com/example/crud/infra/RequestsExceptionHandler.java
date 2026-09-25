package com.example.crud.infra;

import com.example.crud.service.exceptions.CepNotFoundException;
import com.example.crud.service.exceptions.InvalidCepException;
import com.example.crud.service.exceptions.ViaCepUnavailableException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestsExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404(){
        ExceptionDTO response = new ExceptionDTO("Data not found with provided ID", 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionDTO> handleMissingParameter(MissingServletRequestParameterException e){
        ExceptionDTO response = new ExceptionDTO("Required parameter '" + e.getParameterName() + "' is missing", 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidCepException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidCep(InvalidCepException e){
        ExceptionDTO response = new ExceptionDTO(e.getMessage(), 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleCepNotFound(CepNotFoundException e){
        ExceptionDTO response = new ExceptionDTO(e.getMessage(), 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ViaCepUnavailableException.class)
    public ResponseEntity<ExceptionDTO> handleViaCepUnavailable(ViaCepUnavailableException e){
        ExceptionDTO response = new ExceptionDTO(e.getMessage(), 503);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
