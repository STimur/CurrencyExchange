package org.timur.roadmap.currencyexchange.exception;

public class CurrencyNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Валюта не найдена";

    public CurrencyNotFoundException() {
        super(MESSAGE);
    }
}
