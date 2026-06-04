package org.timur.roadmap.currencyexchange.service;

import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.exception.CurrencyAlreadyExistsException;
import org.timur.roadmap.currencyexchange.exception.CurrencyNotFoundException;
import org.timur.roadmap.currencyexchange.exception.DuplicateCurrencyDaoException;
import org.timur.roadmap.currencyexchange.dao.CurrencyDao;

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
