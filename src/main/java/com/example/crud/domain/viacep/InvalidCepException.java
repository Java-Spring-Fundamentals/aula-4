package com.example.crud.domain.viacep;

public class InvalidCepException extends RuntimeException {

    public InvalidCepException() {
        super("CEP must contain exactly 8 numbers");
    }
}
