package com.atividade.produtos.exception;

public class CepNotFoundException extends RuntimeException {

    public CepNotFoundException(String cep) {
        super("CEP não encontrado na ViaCEP: " + cep);
    }
}