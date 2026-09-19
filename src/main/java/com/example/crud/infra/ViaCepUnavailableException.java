package com.example.crud.infra;

public class ViaCepUnavailableException extends RuntimeException {

    public ViaCepUnavailableException(String message) {
        super(message);
    }
}
