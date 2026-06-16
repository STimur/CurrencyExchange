package org.timur.roadmap.currencyexchange.exception;

public class ExchangeRateDaoException extends RuntimeException {

    private static final String MESSAGE = "База данных не смогла обработать запрос";

    public ExchangeRateDaoException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
