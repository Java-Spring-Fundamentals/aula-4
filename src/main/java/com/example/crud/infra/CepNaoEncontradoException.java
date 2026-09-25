package com.example.crud.infra;

public class CepNaoEncontradoException extends RuntimeException {
    public CepNaoEncontradoException(String message) {
        super(message);
    }
}