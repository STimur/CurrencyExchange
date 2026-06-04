package org.timur.roadmap.currencyexchange.exception;

public class CurrencyAlreadyExistsException extends RuntimeException {

    public CurrencyAlreadyExistsException(String code) {
        super(String.format("Currency with %s already exists", code));
    }
}
