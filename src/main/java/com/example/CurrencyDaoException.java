package com.example;

public class CurrencyDaoException extends RuntimeException {

    public CurrencyDaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyDaoException(Throwable cause) {
        super(cause);
    }
}
