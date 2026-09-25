package com.example.crud.infra.exceptions;

public class CepNotFoundException extends RuntimeException {
    public CepNotFoundException(String cep) {
        super("CEP não encontrado na ViaCEP: " + cep);
    }
}
