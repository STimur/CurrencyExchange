package org.timur.roadmap.currencyexchange.exception;

public class CurrencyDaoException extends RuntimeException {

    public CurrencyDaoException() {
        super();
    }

    public CurrencyDaoException(Throwable cause) {
        super(cause);
    }

    public CurrencyDaoException(String message) {
        super(message);
    }

    public CurrencyDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
