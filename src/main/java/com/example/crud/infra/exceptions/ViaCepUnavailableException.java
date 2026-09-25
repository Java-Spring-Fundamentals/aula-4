package com.example.crud.infra.exceptions;

public class ViaCepUnavailableException extends RuntimeException {
    public ViaCepUnavailableException(Throwable cause) {
        super("Serviço ViaCEP indisponível no momento. Tente novamente mais tarde", cause);
    }
}
