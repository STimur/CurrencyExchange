package org.timur.roadmap.currencyexchange.exception;

public class ExchangeRateNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Обменный курс для пары не найден";

    public ExchangeRateNotFoundException() {
        super(MESSAGE);
    }
}
