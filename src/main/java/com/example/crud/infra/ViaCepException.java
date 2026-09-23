package com.example.crud.infra;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ViaCepException extends RuntimeException {

    private final HttpStatus status;

    public ViaCepException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}