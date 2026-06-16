package org.timur.roadmap.currencyexchange.exception;

public class CurrencyAlreadyExistsException extends RuntimeException {

    private static final String MESSAGE = "Валюта с таким кодом уже существует";

    public CurrencyAlreadyExistsException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
