package org.timur.roadmap.currencyexchange.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.exception.CurrencyAlreadyExistsException;
import org.timur.roadmap.currencyexchange.exception.CurrencyDaoException;
import org.timur.roadmap.currencyexchange.exception.CurrencyNotFoundException;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.utility.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CurrencyServiceTest {

    private CurrencyService currencyService;

    @BeforeAll
    static void init() {
        Database.init("C:\\Users\\Timur\\IdeaProjects\\CurrencyExchange\\src\\data\\app.db");
    }

    @BeforeEach
    void setup() {
        CurrencyDao currencyDAO = new CurrencyDao();
        currencyService = new CurrencyService(currencyDAO);
    }

    @Test
    public void findAllThrowExceptionWhenDatabaseIsUnavailable() {
        CurrencyDao currencyDaoMock = mock(CurrencyDao.class);
        when(currencyDaoMock.findAll())
                .thenThrow(new CurrencyDaoException("Some DB Error", new SQLException()));

        CurrencyService currencyServiceWithMock = new CurrencyService(currencyDaoMock);

        RuntimeException exception = assertThrows(
                CurrencyDaoException.class,
                currencyServiceWithMock::findAll
        );

        assertEquals("Some DB Error", exception.getMessage());
    }

    @Test
    public void shouldReturnListOfCurrencies() {
        List<Currency> currencies = currencyService.findAll();

        assertEquals(5, currencies.size());
        assertEquals("USD", currencies.getFirst().code());
        assertEquals("RUB", currencies.getLast().code());
    }

    @Test
    public void findByCodeThrowExceptionWhenCurrencyNotFound() {
        CurrencyNotFoundException exception = assertThrows(
                CurrencyNotFoundException.class,
                () -> currencyService.findByCode("XXX")
        );

        assertEquals("Currency not found: XXX", exception.getMessage());
    }

    @Test
    public void findByCodeThrowsExceptionWhenDatabaseIsUnavailable() {
        CurrencyDao currencyDaoMock = mock(CurrencyDao.class);
        when(currencyDaoMock.findByCode(any()))
                .thenThrow(new CurrencyDaoException("Some DB Error", new SQLException()));

        CurrencyService currencyServiceWithMock = new CurrencyService(currencyDaoMock);

        CurrencyDaoException exception = assertThrows(
                CurrencyDaoException.class,
                () -> currencyServiceWithMock.findByCode("XXX")
        );

        assertEquals("Some DB Error", exception.getMessage());
    }

    @Test
    public void shouldReturnCurrencyByCode() {
        Currency currency = currencyService.findByCode("USD");

        assertEquals("USD", currency.code());
    }

    @Test
    public void createThrowExceptionWhenCurrencyAlreadyExists() {
        CurrencyAlreadyExistsException exception = assertThrows(
                CurrencyAlreadyExistsException.class,
                () -> currencyService.create("USD", "", "")
        );

        assertEquals("Currency with USD already exists", exception.getMessage());
    }

    @Test
    public void createThrowExceptionWhenDatabaseIsUnavailable() {
        CurrencyDao currencyDaoMock = mock(CurrencyDao.class);
        when(currencyDaoMock.insert(any(), any(), any()))
                .thenThrow(new CurrencyDaoException("Some DB Error", new SQLException()));

        CurrencyService currencyServiceWithMock = new CurrencyService(currencyDaoMock);

        CurrencyDaoException exception = assertThrows(
                CurrencyDaoException.class,
                () -> currencyServiceWithMock.create("", "", "")
        );

        assertEquals("Some DB Error", exception.getMessage());
    }

    @Test
    public void shouldCreateNewCurrency() {
        try {
            Currency currency = currencyService.create("TEST", "TEST", "TEST");

            assertTrue(currency.id() > 0);
            assertEquals("TEST", currency.code());
            assertEquals("TEST", currency.name());
            assertEquals("TEST", currency.sign());
        } finally {
            deleteCurrency("TEST");
        }
    }

    private void deleteCurrency(String code) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(
                             "DELETE FROM Currencies WHERE code = ?")) {

            stmt.setString(1, code);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
