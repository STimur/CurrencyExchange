package org.timur.roadmap.currencyexchange.exception;

public class ExchangeRateAlreadyExistsException extends RuntimeException {

    public ExchangeRateAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeRateAlreadyExistsException(String baseCurrencyCode, String targetCurrencyCode) {
        super(String.format("Валютная пара (%s, %s) уже существует", baseCurrencyCode, targetCurrencyCode));
    }
}
