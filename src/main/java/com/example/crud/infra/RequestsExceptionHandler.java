package com.example.crud.infra;

import com.example.crud.infra.exceptions.CepNotFoundException;
import com.example.crud.infra.exceptions.InvalidCepException;
import com.example.crud.infra.exceptions.ViaCepUnavailableException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestsExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404(){
        return build(HttpStatus.NOT_FOUND, "Data not found with provided ID");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> threatValidation(MethodArgumentNotValidException e){
        var fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null
                ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                : "Invalid request body";
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ExceptionDTO> threatMissingParameter(Exception e){
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // ---- Integração ViaCEP ----

    @ExceptionHandler(InvalidCepException.class)
    public ResponseEntity<ExceptionDTO> threatInvalidCep(InvalidCepException e){
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threatCepNotFound(CepNotFoundException e){
        return build(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ViaCepUnavailableException.class)
    public ResponseEntity<ExceptionDTO> threatViaCepUnavailable(ViaCepUnavailableException e){
        return build(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    private ResponseEntity<ExceptionDTO> build(HttpStatus status, String message){
        return ResponseEntity.status(status).body(new ExceptionDTO(message, status.value()));
    }
}
