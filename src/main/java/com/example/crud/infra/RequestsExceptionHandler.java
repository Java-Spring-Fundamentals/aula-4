package com.example.crud.infra;

import com.example.crud.domain.product.ProductNotFoundException;
import com.example.crud.domain.viacep.CepNotFoundException;
import com.example.crud.domain.viacep.InvalidCepException;
import com.example.crud.domain.viacep.ViaCepException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestsExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleProductNotFound(ProductNotFoundException exception) {
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidationError(MethodArgumentNotValidException exception) {
        String message = "Invalid product data";

        if (exception.getFieldError() != null) {
            message = exception.getFieldError().getField() + ": "
                    + exception.getFieldError().getDefaultMessage();
        }

        ExceptionDTO response = new ExceptionDTO(message, 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidCepException.class)
    public ResponseEntity<ExceptionDTO> handleInvalidCep(InvalidCepException exception) {
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), 400);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleCepNotFound(CepNotFoundException exception) {
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), 404);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ViaCepException.class)
    public ResponseEntity<ExceptionDTO> handleViaCepError(ViaCepException exception) {
        ExceptionDTO response = new ExceptionDTO(exception.getMessage(), 503);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
