package com.example.crud.domain.viacep;

public class ViaCepException extends RuntimeException {

    public ViaCepException() {
        super("Unable to communicate with ViaCEP");
    }
}
