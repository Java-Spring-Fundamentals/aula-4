package com.example.crud.infra.exceptions;

// CEP com formato certo, mas que não existe na base da ViaCEP. Vira HTTP 404.
public class CepNotFoundException extends RuntimeException {
    public CepNotFoundException(String cep) {
        super("CEP não encontrado: " + cep);
    }
}
