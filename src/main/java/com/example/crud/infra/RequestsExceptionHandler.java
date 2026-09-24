package com.example.crud.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class RequestsExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404(){
        ExceptionDTO response = new ExceptionDTO("Data not found with provided ID", 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> threatValidation(MethodArgumentNotValidException exception){
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ExceptionDTO response = new ExceptionDTO(message, 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDTO> threatUnreadableBody(HttpMessageNotReadableException exception){
        String message = "Invalid request body";
        if (exception.getMostSpecificCause() instanceof IllegalArgumentException invalid) {
            message = invalid.getMessage();
        }
        ExceptionDTO response = new ExceptionDTO(message, 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionDTO> threatInvalidArgument(MethodArgumentTypeMismatchException exception){
        String message = "Invalid value '" + exception.getValue() + "' for parameter '" + exception.getName() + "'";
        ExceptionDTO response = new ExceptionDTO(message, 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDTO> threatIllegalArgument(IllegalArgumentException exception){
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionDTO> threatMissingParameter(MissingServletRequestParameterException exception){
        String message = "Missing required parameter '" + exception.getParameterName() + "'";
        ExceptionDTO response = new ExceptionDTO(message, 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidCepException.class)
    public ResponseEntity<ExceptionDTO> threatInvalidCep(InvalidCepException exception){
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threatCepNotFound(CepNotFoundException exception){
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ExceptionDTO> threatExternalService(ExternalServiceException exception){
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), 503);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}