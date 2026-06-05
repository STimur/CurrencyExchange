package org.timur.roadmap.currencyexchange.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotFoundException;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.utility.Database;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExchangeRateServiceTest {

    private ExchangeRateService exchangeRateService;

    @BeforeAll
    static void init() {
        Database.init("C:\\Users\\Timur\\IdeaProjects\\CurrencyExchange\\src\\data\\app.db");
    }

    @BeforeEach
    void setup() {
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
        exchangeRateService = new ExchangeRateService(exchangeRateDao);
    }

    @Test
    public void findAllThrowExceptionWhenDatabaseIsUnavailable() {
        ExchangeRateDao exchangeRateDaoMock = mock(ExchangeRateDao.class);
        when(exchangeRateDaoMock.findAll()).thenThrow(new ExchangeRateDaoException("База данных недоступна", new SQLException()));

        ExchangeRateService exchangeRateServiceWithMock = new ExchangeRateService(exchangeRateDaoMock);

        ExchangeRateDaoException exception = assertThrows(
                ExchangeRateDaoException.class,
                exchangeRateServiceWithMock::findAll
        );
        assertEquals("База данных недоступна", exception.getMessage());
    }

    @Test
    public void shouldReturnListOfExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateService.findAll();

        assertEquals(6, exchangeRates.size());
        assertEquals("USD", exchangeRates.getFirst().baseCurrency().code());
        assertEquals("EUR", exchangeRates.getFirst().targetCurrency().code());
        assertEquals(new BigDecimal("0.87"), exchangeRates.getFirst().rate());
        assertEquals("JPY", exchangeRates.getLast().baseCurrency().code());
        assertEquals("EUR", exchangeRates.getLast().targetCurrency().code());
        assertEquals(new BigDecimal("1.176471"), exchangeRates.getLast().rate());
    }

    @Test
    public void findByCodePairThrowExceptionWhenExchangeRateNotFound() {
        ExchangeRateNotFoundException exception = assertThrows(
                ExchangeRateNotFoundException.class,
                () -> exchangeRateService.findByCodePair("USD", "NEX")
        );

        assertEquals("Обменный курс для пары (USD, NEX) не найден", exception.getMessage());
    }

    @Test
    public void findByCodePairThrowsExceptionWhenDatabaseIsUnavailable() {
        ExchangeRateDao exchangeRateDaoMock = mock(ExchangeRateDao.class);
        when(exchangeRateDaoMock.findByCodePair(any(), any()))
                .thenThrow(new ExchangeRateDaoException("Some DB Error", new SQLException()));

        ExchangeRateService exchangeRateServiceWithMock = new ExchangeRateService(exchangeRateDaoMock);

        ExchangeRateDaoException exception = assertThrows(
                ExchangeRateDaoException.class,
                () -> exchangeRateServiceWithMock.findByCodePair("USD", "EUR")
        );

        assertEquals("Some DB Error", exception.getMessage());
    }

    @Test
    public void shouldReturnExchangeRateByCodePair() {
        ExchangeRate exchangeRate = exchangeRateService.findByCodePair("USD", "EUR");

        assertEquals(1, exchangeRate.id());
        assertEquals("USD", exchangeRate.baseCurrency().code());
        assertEquals("USD", exchangeRate.baseCurrency().code());
        assertEquals("EUR", exchangeRate.targetCurrency().code());
        assertEquals("EUR", exchangeRate.targetCurrency().code());
        assertEquals(new BigDecimal("0.87"), exchangeRate.rate());
    }
}
