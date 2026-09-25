package com.example.crud.infra.exceptions;

// A ViaCEP não respondeu (fora do ar, lenta demais, sem internet, resposta inesperada).
// Vira HTTP 503 (Service Unavailable): o problema é temporário e não é culpa de quem chamou.
public class ViaCepUnavailableException extends RuntimeException {
    public ViaCepUnavailableException(Throwable cause) {
        super("O serviço de consulta de CEP (ViaCEP) está indisponível no momento. Tente novamente em instantes.", cause);
    }
}
