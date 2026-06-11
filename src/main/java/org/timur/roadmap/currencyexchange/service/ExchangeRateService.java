package org.timur.roadmap.currencyexchange.service;

import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.exception.DuplicateExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateCurrencyNotExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotFoundException;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRateService {

    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyDao currencyDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao, CurrencyDao currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public List<ExchangeRate> findAll() {
        return exchangeRateDao.findAll();
    }

    public ExchangeRate findByCodePair(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateDao.findByCodePair(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException(baseCurrencyCode, targetCurrencyCode));
    }

    public ExchangeRate create(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        try {
            Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                    .orElseThrow(() -> new ExchangeRateCurrencyNotExistsException(
                            "Одна (или обе) валюта из валютной пары не существует в БД"));
            Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                    .orElseThrow(() -> new ExchangeRateCurrencyNotExistsException(
                            "Одна (или обе) валюта из валютной пары не существует в БД"));

            int id = exchangeRateDao.insert(baseCurrencyCode, targetCurrencyCode, rate);

            return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
        } catch (DuplicateExchangeRateDaoException e) {
            throw new ExchangeRateAlreadyExistsException("Валютная пара с таким кодом уже существует", e);
        }
    }

    public ExchangeRate update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        return exchangeRateDao.update(baseCurrencyCode, targetCurrencyCode, rate)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Валютная пара отсутствует в базе данных"));
    }
}
