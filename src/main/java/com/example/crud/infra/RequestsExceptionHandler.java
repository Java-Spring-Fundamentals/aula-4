package com.example.crud.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class RequestsExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDTO> produtoNaoEncontrado(
            EntityNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDTO(exception.getMessage(), 404));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionDTO> erroDaRequisicao(
            ResponseStatusException exception) {

        int status = exception.getStatusCode().value();

        return ResponseEntity.status(status)
                .body(new ExceptionDTO(exception.getReason(), status));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> dadosInvalidos(
            MethodArgumentNotValidException exception) {

        var erro = exception.getBindingResult().getFieldError();

        String mensagem = erro == null
                ? "Dados invalidos."
                : erro.getField() + ": " + erro.getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(new ExceptionDTO(mensagem, 400));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDTO> corpoInvalido() {

        return ResponseEntity.badRequest()
                .body(new ExceptionDTO(
                        "Envie um corpo JSON valido com os tipos corretos.",
                        400));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionDTO> parametroAusente(
            MissingServletRequestParameterException exception) {

        return ResponseEntity.badRequest()
                .body(new ExceptionDTO(
                        "Informe o parametro: " + exception.getParameterName(),
                        400));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ExceptionDTO> cabecalhoAusente(
            MissingRequestHeaderException exception) {

        return ResponseEntity.badRequest()
                .body(new ExceptionDTO(
                        "Informe o cabecalho: " + exception.getHeaderName(),
                        400));
    }
}