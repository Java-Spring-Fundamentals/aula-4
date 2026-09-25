package com.atividade.produtos.exception;

public class ViaCepUnavailableException extends RuntimeException {

    public ViaCepUnavailableException(Throwable cause) {
        super("Serviço de CEP indisponível no momento. Tente novamente em instantes.", cause);
    }
}