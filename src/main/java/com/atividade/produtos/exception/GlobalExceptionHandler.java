package com.atividade.produtos.exception;

import com.atividade.produtos.dto.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleProductNotFound(ProductNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Dados inválidos", details);
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiError> handleMissingInput(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Corpo da requisição ausente ou com JSON inválido", List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Rota não encontrada: " + ex.getResourcePath(), List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage(), List.of());
    }

    @ExceptionHandler(InvalidCepException.class)
    public ResponseEntity<ApiError> handleInvalidCep(InvalidCepException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ApiError> handleCepNotFound(CepNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(ViaCepUnavailableException.class)
    public ResponseEntity<ApiError> handleViaCepUnavailable(ViaCepUnavailableException ex) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        log.error("Erro inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente mais tarde.", List.of());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, List<String> details) {
        ApiError body = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, details);
        return ResponseEntity.status(status).body(body);
    }
}