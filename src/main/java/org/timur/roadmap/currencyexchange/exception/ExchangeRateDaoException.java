package org.timur.roadmap.currencyexchange.exception;

public class ExchangeRateDaoException extends RuntimeException {

    public ExchangeRateDaoException(Throwable cause) {
        super(cause);
    }

    public ExchangeRateDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
