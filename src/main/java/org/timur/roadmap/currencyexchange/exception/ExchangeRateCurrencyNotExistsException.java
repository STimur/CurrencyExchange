package org.timur.roadmap.currencyexchange.exception;

public class ExchangeRateCurrencyNotExistsException extends RuntimeException {

    public ExchangeRateCurrencyNotExistsException(String message) {
        super(message);
    }
}
