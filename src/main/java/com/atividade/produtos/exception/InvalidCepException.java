package com.atividade.produtos.exception;

public class InvalidCepException extends RuntimeException {

    public InvalidCepException(String cep) {
        super("CEP inválido: '" + cep + "'. Informe 8 dígitos, com ou sem hífen (ex.: 08773-380)");
    }
}