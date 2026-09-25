package com.example.crud.infra;

public class CepInvalidoException extends RuntimeException{
    public CepInvalidoException (String mensagem) {
        super (mensagem);
    }
}
