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
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRatesControllerTest {

    @Mock
    private ExchangeRateService exchangeRateService;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;

    @InjectMocks
    private ExchangeRatesController exchangeRatesController;

    @BeforeEach
    void setup() throws IOException {
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void getShouldReturnAllExchangeRates() throws IOException {
        List<ExchangeRate> exchangeRates = List.of();
        when(exchangeRateService.findAll()).thenReturn(exchangeRates);

        exchangeRatesController.doGet(request, response);

        verify(exchangeRateService).findAll();
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(mapper).writeValue(writer, exchangeRates);
    }

    @Test
    void postShouldReturn400WhenInvalidInput() throws IOException {
        when(request.getParameter("baseCurrencyCode")).thenReturn(null);

        exchangeRatesController.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mapper).writeValue(writer, new ErrorResponse("Отсутствует нужное поле формы"));
    }

    @Test
    void postShouldReturn409WhenExchangeRateAlreadyExists() throws IOException {
        String baseCurrency = "USD";
        String targetCurrency = "EUR";
        String rate = "0.8";
        when(request.getParameter("baseCurrencyCode")).thenReturn(baseCurrency);
        when(request.getParameter("targetCurrencyCode")).thenReturn(targetCurrency);
        when(request.getParameter("rate")).thenReturn(rate);
        when(exchangeRateService.create(baseCurrency, targetCurrency, new BigDecimal(rate)))
                .thenThrow(new ExchangeRateAlreadyExistsException(baseCurrency, targetCurrency));

        exchangeRatesController.doPost(request, response);

        verify(exchangeRateService).create(baseCurrency, targetCurrency, new BigDecimal(rate));
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(mapper).writeValue(writer, new ErrorResponse("Валютная пара (USD, EUR) уже существует"));
    }

    @Test
    void postShouldReturn404WhenAtLeastOneCurrencyFromPairNotExistsInDatabase() throws IOException {
        String baseCurrency = "USD";
        String targetCurrency = "NEX";
        String rate = "0.8";
        when(request.getParameter("baseCurrencyCode")).thenReturn(baseCurrency);
        when(request.getParameter("targetCurrencyCode")).thenReturn(targetCurrency);
        when(request.getParameter("rate")).thenReturn(rate);
        when(exchangeRateService.create(baseCurrency, targetCurrency, new BigDecimal(rate)))
                .thenThrow(new ExchangeRateCurrencyNotExistsException(
                        "Одна (или обе) валюта из валютной пары не существует в БД"
                ));

        exchangeRatesController.doPost(request, response);

        verify(exchangeRateService).create(baseCurrency, targetCurrency, new BigDecimal(rate));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mapper).writeValue(writer,
                new ErrorResponse("Одна (или обе) валюта из валютной пары не существует в БД"));
    }

    @Test
    void postShouldCreateNewExchangeRate() throws IOException {
        String baseCurrency = "USD";
        String targetCurrency = "NEX";
        String rate = "0.8";
        ExchangeRate exchangeRate = new ExchangeRate(0, null, null, null);
        when(request.getParameter("baseCurrencyCode")).thenReturn(baseCurrency);
        when(request.getParameter("targetCurrencyCode")).thenReturn(targetCurrency);
        when(request.getParameter("rate")).thenReturn(rate);
        when(exchangeRateService.create(baseCurrency, targetCurrency, new BigDecimal(rate)))
                .thenReturn(exchangeRate);

        exchangeRatesController.doPost(request, response);

        verify(exchangeRateService).create(baseCurrency, targetCurrency, new BigDecimal(rate));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(mapper).writeValue(writer, exchangeRate);
    }
}
