package com.example.crud.domain.viacep;

public class CepNotFoundException extends RuntimeException {

    public CepNotFoundException() {
        super("CEP not found");
    }
}
