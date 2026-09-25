package com.example.crud.infra;

import com.example.crud.infra.exceptions.CepNotFoundException;
import com.example.crud.infra.exceptions.InvalidCepException;
import com.example.crud.infra.exceptions.ViaCepUnavailableException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// Transforma as exceções lançadas em qualquer controller em respostas HTTP
// com status correto e uma mensagem clara, em vez de um erro 500 genérico.
@RestControllerAdvice
public class RequestsExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threat404(){
        return build(HttpStatus.NOT_FOUND, "Data not found with provided ID");
    }

    // Erros de @Valid (ex.: nome em branco, distributionCenter inválido)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> threatValidation(MethodArgumentNotValidException exception){
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message);
    }

    // Parâmetro obrigatório faltando na URL (ex.: chamar /availability/p1 sem ?cep=)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionDTO> threatMissingParam(MissingServletRequestParameterException exception){
        return build(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório ausente: " + exception.getParameterName());
    }

    @ExceptionHandler(InvalidCepException.class)
    public ResponseEntity<ExceptionDTO> threatInvalidCep(InvalidCepException exception){
        return build(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ExceptionDTO> threatCepNotFound(CepNotFoundException exception){
        return build(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ViaCepUnavailableException.class)
    public ResponseEntity<ExceptionDTO> threatViaCepUnavailable(ViaCepUnavailableException exception){
        return build(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    private ResponseEntity<ExceptionDTO> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ExceptionDTO(message, status.value()));
    }
}
