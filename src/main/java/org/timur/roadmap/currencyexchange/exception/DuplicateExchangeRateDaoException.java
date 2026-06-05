package org.timur.roadmap.currencyexchange.exception;

public class DuplicateExchangeRateDaoException extends ExchangeRateDaoException {

    public DuplicateExchangeRateDaoException(Throwable cause) {
        super(cause);
    }

    public DuplicateExchangeRateDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
