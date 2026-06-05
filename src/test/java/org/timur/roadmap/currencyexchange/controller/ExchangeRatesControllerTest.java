package org.timur.roadmap.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.timur.roadmap.currencyexchange.dto.ErrorResponse;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateCurrencyNotExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRatesControllerTest {

    @Mock
    private ExchangeRateService exchangeRateServiceMock;
    @Mock
    private ObjectMapper mapperMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private PrintWriter writerMock;

    @InjectMocks
    private ExchangeRatesController exchangeRatesController;

    @BeforeEach
    void setup() throws IOException {
        when(responseMock.getWriter()).thenReturn(writerMock);
    }

    @Test
    void getShouldReturn500WhenErrorOnServer() throws IOException {
        ExchangeRateDaoException e = new ExchangeRateDaoException("База данных недоступна", new SQLException());
        when(exchangeRateServiceMock.findAll()).thenThrow(e);

        exchangeRatesController.doGet(requestMock, responseMock);

        verify(exchangeRateServiceMock).findAll();
        verify(responseMock).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseMock).setContentType("application/json");
        verify(mapperMock).writeValue(writerMock, new ErrorResponse(e.getMessage()));
    }

    @Test
    void getShouldReturnAllExchangeRates() throws IOException {
        List<ExchangeRate> exchangeRates = List.of();
        when(exchangeRateServiceMock.findAll()).thenReturn(exchangeRates);

        exchangeRatesController.doGet(requestMock, responseMock);

        verify(exchangeRateServiceMock).findAll();
        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(responseMock).setContentType("application/json");
        verify(mapperMock).writeValue(writerMock, exchangeRates);
    }

    @Test
    void postShouldReturn400WhenInvalidInput() throws IOException {
        when(requestMock.getParameter("baseCurrencyCode")).thenReturn(null);

        exchangeRatesController.doPost(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(responseMock).setContentType("application/json");
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Отсутствует нужное поле формы"));
    }

    @Test
    void postShouldReturn409WhenExchangeRateAlreadyExists() throws IOException {
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        String rate = "0.8";
        when(requestMock.getParameter("baseCurrencyCode")).thenReturn(baseCurrency);
        when(requestMock.getParameter("targetCurrencyCode")).thenReturn(targetCurrency);
        when(requestMock.getParameter("rate")).thenReturn(rate);
        when(exchangeRateServiceMock.create(baseCurrency, targetCurrency, new BigDecimal(rate)))
                .thenThrow(new ExchangeRateAlreadyExistsException(baseCurrency, targetCurrency));

        exchangeRatesController.doPost(requestMock, responseMock);

        verify(exchangeRateServiceMock).create(baseCurrency, targetCurrency, new BigDecimal(rate));
        verify(responseMock).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(responseMock).setContentType("application/json");
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Валютная пара (USD, EUR) уже существует"));
    }

    @Test
    void postShouldReturn404WhenAtLeastOneCurrencyFromPairNotExistsInDatabase() throws IOException {
        String baseCurrency = "USD";
        String targetCurrency = "NEX";
        String rate = "0.8";
        when(requestMock.getParameter("baseCurrencyCode")).thenReturn(baseCurrency);
        when(requestMock.getParameter("targetCurrencyCode")).thenReturn(targetCurrency);
        when(requestMock.getParameter("rate")).thenReturn(rate);
        when(exchangeRateServiceMock.create(baseCurrency, targetCurrency, new BigDecimal(rate)))
                .thenThrow(new ExchangeRateCurrencyNotExistsException());

        exchangeRatesController.doPost(requestMock, responseMock);

        verify(exchangeRateServiceMock).create(baseCurrency, targetCurrency, new BigDecimal(rate));
        verify(responseMock).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(responseMock).setContentType("application/json");
        verify(mapperMock).writeValue(writerMock,
                new ErrorResponse("Одна (или обе) валюта из валютной пары не существует в БД"));
    }

    @Test
    void postShouldReturn500WhenDatabaseIsUnavailable() throws IOException {
        String baseCurrency = "USD";
        String targetCurrency = "NEX";
        String rate = "0.8";
        when(requestMock.getParameter("baseCurrencyCode")).thenReturn(baseCurrency);
        when(requestMock.getParameter("targetCurrencyCode")).thenReturn(targetCurrency);
        when(requestMock.getParameter("rate")).thenReturn(rate);
        when(exchangeRateServiceMock.create(baseCurrency, targetCurrency, new BigDecimal(rate)))
                .thenThrow(new ExchangeRateDaoException("База данных недоступна", new SQLException()));

        exchangeRatesController.doPost(requestMock, responseMock);

        verify(exchangeRateServiceMock).create(baseCurrency, targetCurrency, new BigDecimal(rate));
        verify(responseMock).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(responseMock).setContentType("application/json");
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("База данных недоступна"));
    }

    @Test
    void postShouldCreateNewExchangeRate() throws IOException {
        String baseCurrency = "USD";
        String targetCurrency = "NEX";
        String rate = "0.8";
        ExchangeRate exchangeRate = new ExchangeRate(0, null, null, null);
        when(requestMock.getParameter("baseCurrencyCode")).thenReturn(baseCurrency);
        when(requestMock.getParameter("targetCurrencyCode")).thenReturn(targetCurrency);
        when(requestMock.getParameter("rate")).thenReturn(rate);
        when(exchangeRateServiceMock.create(baseCurrency, targetCurrency, new BigDecimal(rate)))
                .thenReturn(exchangeRate);

        exchangeRatesController.doPost(requestMock, responseMock);

        verify(exchangeRateServiceMock).create(baseCurrency, targetCurrency, new BigDecimal(rate));
        verify(responseMock).setStatus(HttpServletResponse.SC_CREATED);
        verify(responseMock).setContentType("application/json");
        verify(mapperMock).writeValue(writerMock, exchangeRate);
    }
}
