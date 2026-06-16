package org.timur.roadmap.currencyexchange.exception;

public class ExchangeRateCurrencyNotExistsException extends RuntimeException {

    public static final String MESSAGE = "Одна (или обе) валюта из валютной пары не существует в БД";

    public ExchangeRateCurrencyNotExistsException() {
        super(MESSAGE);
    }
}
