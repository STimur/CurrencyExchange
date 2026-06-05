package org.timur.roadmap.currencyexchange.exception;

public class ExchangeRateNotFoundException extends RuntimeException {

    public ExchangeRateNotFoundException(String message) {
        super(message);
    }

    public ExchangeRateNotFoundException(String baseCurrencyCode, String targetCurrencyCode) {
        super(String.format("Обменный курс для пары (%s, %s) не найден", baseCurrencyCode, targetCurrencyCode));
    }
}
