package org.timur.roadmap.currencyexchange.exception;

public class CurrencyDaoException extends RuntimeException {

    private static final String MESSAGE = "База данных не смогла обработать запрос";

    public CurrencyDaoException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
