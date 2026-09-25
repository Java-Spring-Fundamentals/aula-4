package com.example.crud.infra.exceptions;

// CEP com formato errado (ex.: "123", "abcde-fgh"). Vira HTTP 400.
public class InvalidCepException extends RuntimeException {
    public InvalidCepException(String cep) {
        super("CEP inválido: '" + cep + "'. Informe 8 dígitos, com ou sem hífen (ex.: 08773-380).");
    }
}
