package org.timur.roadmap.currencyexchange.service;

import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;

import java.util.List;

public class ExchangeRateService {

    private final ExchangeRateDao exchangeRateDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public List<ExchangeRate> findAll() {
        return exchangeRateDao.findAll();
    }

    public ExchangeRate findByCodePair(String baseCurrencyCode, String targetCurrencyCode) {
        return null;
    }
}
