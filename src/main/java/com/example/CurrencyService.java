package com.example;

import java.util.List;

public class CurrencyService {

    CurrencyDao currencyDAO;

    public CurrencyService(CurrencyDao currencyDAO) {
        this.currencyDAO = currencyDAO;
    }

    public List<Currency> findAll() {
        return currencyDAO.findAll();
    }

    public Currency findByCode(String code) {
        return currencyDAO.findByCode(code).orElseThrow(() -> new CurrencyNotFoundException(code));
    }

    public Currency create(String code, String fullName, String sign) {
        try {
            return currencyDAO.insert(code, fullName, sign);
        } catch (DuplicateCurrencyDaoException e) {
            throw new CurrencyAlreadyExistsException(code);
        }
    }
}
