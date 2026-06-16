package org.timur.roadmap.currencyexchange.exception;

public class ExchangeRateAlreadyExistsException extends RuntimeException {

    public static final String MESSAGE = "Валютная пара c таким кодом уже существует";

    public ExchangeRateAlreadyExistsException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
