package org.timur.roadmap.currencyexchange.exception;

public class ExchangeImpossibleException extends RuntimeException {

    public static final String MESSAGE = "Не возможно вычислить обменный курс";

    public ExchangeImpossibleException() {
        super(MESSAGE);
    }
}
