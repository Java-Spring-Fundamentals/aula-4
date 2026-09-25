package com.example.crud.infra;

public class ViaCepIndisponivelException extends RuntimeException {
    public ViaCepIndisponivelException(String message) {
        super(message);
    }
}
