package com.example;

public class CurrencyAlreadyExistsException extends RuntimeException {

    public CurrencyAlreadyExistsException(String code) {
        super(String.format("Currency with %s already exists", code));
    }
}
