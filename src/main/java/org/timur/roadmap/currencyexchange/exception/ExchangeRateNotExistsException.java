package org.timur.roadmap.currencyexchange.exception;

public class ExchangeRateNotExistsException extends RuntimeException {

    public static final String MESSAGE = "Валютная пара отсутствует в базе данных";

    public ExchangeRateNotExistsException() {
        super(MESSAGE);
    }
}
