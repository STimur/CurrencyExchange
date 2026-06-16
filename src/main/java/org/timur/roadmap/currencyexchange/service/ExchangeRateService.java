package org.timur.roadmap.currencyexchange.service;

import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.exception.DuplicateExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateCurrencyNotExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotFoundException;
import org.timur.roadmap.currencyexchange.model.Conversion;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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
                .orElseThrow(ExchangeRateNotFoundException::new);
    }

    public ExchangeRate create(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        try {
            Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                    .orElseThrow(ExchangeRateCurrencyNotExistsException::new);
            Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                    .orElseThrow(ExchangeRateCurrencyNotExistsException::new);

            int id = exchangeRateDao.insert(baseCurrencyCode, targetCurrencyCode, rate);

            return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
        } catch (DuplicateExchangeRateDaoException e) {
            throw new ExchangeRateAlreadyExistsException(e);
        }
    }

    public ExchangeRate update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        return exchangeRateDao.update(baseCurrencyCode, targetCurrencyCode, rate)
                .orElseThrow(ExchangeRateNotExistsException::new);
    }

    public Conversion convert(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        Optional<ExchangeRate> directRate = exchangeRateDao.findByCodePair(baseCurrencyCode, targetCurrencyCode);

        if (directRate.isPresent()) {
            ExchangeRate rate = directRate.get();

            return new Conversion(
                    rate.baseCurrency(),
                    rate.targetCurrency(),
                    rate.rate(),
                    amount,
                    amount.multiply(rate.rate()).setScale(2, RoundingMode.HALF_UP)
            );
        }

        Optional<ExchangeRate> reverseRate = exchangeRateDao.findByCodePair(targetCurrencyCode, baseCurrencyCode);

        if (reverseRate.isPresent()) {
            ExchangeRate rate = reverseRate.get();
            BigDecimal exchangeRate = BigDecimal.ONE.divide(rate.rate(), 6, RoundingMode.HALF_UP);

            return new Conversion(
                    rate.targetCurrency(),
                    rate.baseCurrency(),
                    exchangeRate,
                    amount,
                    amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP)
            );
        }

        ExchangeRate USDtoBaseRate = exchangeRateDao.findByCodePair("USD", baseCurrencyCode).get();
        BigDecimal baseToUSDRate = BigDecimal.ONE.divide(USDtoBaseRate.rate(), 6, RoundingMode.HALF_UP);
        ExchangeRate USDtoTargetRate = exchangeRateDao.findByCodePair("USD", targetCurrencyCode).get();
        BigDecimal rate = baseToUSDRate.multiply(USDtoTargetRate.rate());

        return new Conversion(
                USDtoBaseRate.targetCurrency(),
                USDtoTargetRate.targetCurrency(),
                rate,
                amount,
                amount.multiply(rate).setScale(2, RoundingMode.HALF_UP)
        );
    }
}
