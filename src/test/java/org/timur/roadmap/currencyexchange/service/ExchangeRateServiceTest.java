package org.timur.roadmap.currencyexchange.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateCurrencyNotExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotFoundException;
import org.timur.roadmap.currencyexchange.model.Conversion;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.utility.Database;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        CurrencyDao currencyDao = new CurrencyDao();
        exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyDao);
    }

    @Test
    public void findAllThrowExceptionWhenDatabaseIsUnavailable() {
        ExchangeRateDao exchangeRateDaoMock = mock(ExchangeRateDao.class);
        ExchangeRateDaoException e = new ExchangeRateDaoException(new SQLException());
        when(exchangeRateDaoMock.findAll()).thenThrow(e);

        ExchangeRateService exchangeRateServiceWithMock = new ExchangeRateService(exchangeRateDaoMock, null);

        ExchangeRateDaoException exception = assertThrows(
                ExchangeRateDaoException.class,
                exchangeRateServiceWithMock::findAll
        );
        assertEquals(e.getMessage(), exception.getMessage());
    }

    @Test
    public void shouldReturnListOfExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateService.findAll();

        assertEquals(7, exchangeRates.size());
        assertEquals("USD", exchangeRates.getFirst().baseCurrency().code());
        assertEquals("EUR", exchangeRates.getFirst().targetCurrency().code());
        assertEquals(new BigDecimal("0.87"), exchangeRates.getFirst().rate());
        assertEquals("USD", exchangeRates.getLast().baseCurrency().code());
        assertEquals("AUD", exchangeRates.getLast().targetCurrency().code());
        assertEquals(new BigDecimal("1.45"), exchangeRates.getLast().rate());
    }

    @Test
    public void findByCodePairThrowExceptionWhenExchangeRateNotFound() {
        ExchangeRateNotFoundException exception = assertThrows(
                ExchangeRateNotFoundException.class,
                () -> exchangeRateService.findByCodePair("USD", "NEX")
        );

        assertEquals("Обменный курс для пары не найден", exception.getMessage());
    }

    @Test
    public void findByCodePairThrowsExceptionWhenDatabaseIsUnavailable() {
        ExchangeRateDao exchangeRateDaoMock = mock(ExchangeRateDao.class);
        ExchangeRateDaoException e = new ExchangeRateDaoException(new SQLException());
        when(exchangeRateDaoMock.findByCodePair(any(), any())).thenThrow(e);

        ExchangeRateService exchangeRateServiceWithMock = new ExchangeRateService(exchangeRateDaoMock, null);

        ExchangeRateDaoException exception = assertThrows(
                ExchangeRateDaoException.class,
                () -> exchangeRateServiceWithMock.findByCodePair("USD", "EUR")
        );

        assertEquals(e.getMessage(), exception.getMessage());
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

    @Test
    public void createThrowExceptionWhenExchangeRateAlreadyExists() {
        ExchangeRateAlreadyExistsException exception = assertThrows(
                ExchangeRateAlreadyExistsException.class,
                () -> exchangeRateService.create("USD", "EUR", new BigDecimal("0.8"))
        );

        assertEquals("Валютная пара c таким кодом уже существует", exception.getMessage());
    }

    @Test
    public void createThrowExceptionWhenAtLeastOneOfCurrenciesNotExists() {
        ExchangeRateCurrencyNotExistsException exception = assertThrows(
                ExchangeRateCurrencyNotExistsException.class,
                () -> exchangeRateService.create("USD", "NEX", new BigDecimal("0.8"))
        );

        assertEquals("Одна (или обе) валюта из валютной пары не существует в БД", exception.getMessage());
    }

    @Test
    public void createThrowExceptionWhenDatabaseIsUnavailable() {
        CurrencyDao currencyDaoMock = mock(CurrencyDao.class);
        ExchangeRateDao exchangeRateDaoMock = mock(ExchangeRateDao.class);
        ExchangeRateDaoException e = new ExchangeRateDaoException(new SQLException());
        when(currencyDaoMock.findByCode(any())).thenReturn(Optional.of(new Currency(0, "", "", "")));
        when(exchangeRateDaoMock.insert(any(), any(), any())).thenThrow(e);

        ExchangeRateService exchangeRateServiceWithMock = new ExchangeRateService(exchangeRateDaoMock, currencyDaoMock);

        ExchangeRateDaoException exception = assertThrows(
                ExchangeRateDaoException.class,
                () -> exchangeRateServiceWithMock.create("USD", "RUB", new BigDecimal("75.4"))
        );

        assertEquals(e.getMessage(), exception.getMessage());
    }

    @Test
    public void shouldCreateNewExchangeRate() {
        ExchangeRate exchangeRate = exchangeRateService.create("USD", "RUB", new BigDecimal("74.3"));

        assertTrue(exchangeRate.id() > 0);
        assertEquals("USD", exchangeRate.baseCurrency().code());
        assertEquals("RUB", exchangeRate.targetCurrency().code());
        assertEquals(new BigDecimal("74.3"), exchangeRate.rate());

        deleteExchangeRate(exchangeRate.id());
    }

    @Test
    public void updateThrowExceptionWhenCurrencyPairNotExists() {
        ExchangeRateNotExistsException exception = assertThrows(
                ExchangeRateNotExistsException.class,
                () -> exchangeRateService.update("USD", "NEX", new BigDecimal("0.8"))
        );

        assertEquals("Валютная пара отсутствует в базе данных", exception.getMessage());
    }

    @Test
    public void updateThrowExceptionWhenDatabaseIsUnavailable() {
        ExchangeRateDao exchangeRateDaoMock = mock(ExchangeRateDao.class);
        ExchangeRateDaoException e = new ExchangeRateDaoException(new SQLException());
        when(exchangeRateDaoMock.update("USD", "NEX", new BigDecimal("0.8"))).thenThrow(e);

        ExchangeRateService exchangeRateServiceWithMock = new ExchangeRateService(exchangeRateDaoMock, null);

        ExchangeRateDaoException exception = assertThrows(
                ExchangeRateDaoException.class,
                () -> exchangeRateServiceWithMock.update("USD", "NEX", new BigDecimal("0.8"))
        );

        verify(exchangeRateDaoMock).update("USD", "NEX", new BigDecimal("0.8"));
        assertEquals(e.getMessage(), exception.getMessage());
    }

    @Test
    public void shouldUpdateExchangeRate() {
        ExchangeRate exchangeRate = exchangeRateService.update("USD", "EUR", new BigDecimal("0.8"));

        assertEquals(1, exchangeRate.id());
        assertEquals("USD", exchangeRate.baseCurrency().code());
        assertEquals("EUR", exchangeRate.targetCurrency().code());
        assertEquals(new BigDecimal("0.8"), exchangeRate.rate());

        // update to initial value
        exchangeRateService.update("USD", "EUR", new BigDecimal("0.87"));
    }

    @Test
    public void shouldConvertIfDirectRateExists() {
        Conversion conversion = exchangeRateService.convert("USD", "AUD", new BigDecimal("10.5"));

        assertEquals("USD", conversion.baseCurrency().code());
        assertEquals("AUD", conversion.targetCurrency().code());
        assertEquals(new BigDecimal("1.45"), conversion.rate());
        assertEquals(new BigDecimal("10.5"), conversion.amount());
        assertEquals(new BigDecimal("15.23"), conversion.convertedAmount());
        assertEquals(2, conversion.convertedAmount().scale());
    }

    @Test
    public void shouldConvertIfReverseRateExists() {
        BigDecimal expectedConversionRate = BigDecimal.ONE.divide(new BigDecimal("1.45"), 6, RoundingMode.HALF_UP);
        BigDecimal amount = new BigDecimal("10.5");

        Conversion conversion = exchangeRateService.convert("AUD", "USD", amount);

        assertEquals("AUD", conversion.baseCurrency().code());
        assertEquals("USD", conversion.targetCurrency().code());
        assertEquals(expectedConversionRate, conversion.rate());
        assertEquals(amount, conversion.amount());
        assertEquals(
                amount.multiply(expectedConversionRate).setScale(2, RoundingMode.HALF_UP),
                conversion.convertedAmount()
        );
        assertEquals(2, conversion.convertedAmount().scale());
    }

    @Test
    public void shouldConvertIfCrossUSDRateExists() {
        BigDecimal EURtoUSDreverseRate = BigDecimal.ONE.divide(new BigDecimal("0.87"), 6, RoundingMode.HALF_UP);
        BigDecimal USDtoAUDrate = new BigDecimal("1.45");
        BigDecimal expectedConversionRate = EURtoUSDreverseRate.multiply(USDtoAUDrate);
        BigDecimal amount = new BigDecimal("10.5");

        Conversion conversion = exchangeRateService.convert("EUR", "AUD", amount);

        assertEquals("EUR", conversion.baseCurrency().code());
        assertEquals("AUD", conversion.targetCurrency().code());
        assertEquals(expectedConversionRate, conversion.rate());
        assertEquals(amount, conversion.amount());
        assertEquals(
                amount.multiply(expectedConversionRate).setScale(2, RoundingMode.HALF_UP),
                conversion.convertedAmount()
        );
        assertEquals(2, conversion.convertedAmount().scale());
    }

    private void deleteExchangeRate(int id) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(
                             "DELETE FROM ExchangeRates WHERE id = ?")) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
