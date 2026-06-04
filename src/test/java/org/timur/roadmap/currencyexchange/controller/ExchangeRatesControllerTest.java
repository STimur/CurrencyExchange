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
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
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
    void getShouldReturnAllCurrencies() throws IOException {
        List<ExchangeRate> exchangeRates = List.of();
        when(exchangeRateServiceMock.findAll()).thenReturn(exchangeRates);

        exchangeRatesController.doGet(requestMock, responseMock);

        verify(exchangeRateServiceMock).findAll();
        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(responseMock).setContentType("application/json");
        verify(mapperMock).writeValue(writerMock, exchangeRates);
    }

//    @Test
//    void postShouldReturn400WhenInvalidInput() throws IOException {
//        when(requestMock.getParameter("code")).thenReturn(null);
//
//        exchangeRatesController.doPost(requestMock, responseMock);
//
//        verify(responseMock).setContentType("application/json");
//        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Отсутствует нужное поле формы"));
//    }

//    @Test
//    void postShouldReturn409WhenCurrencyAlreadyExists() throws IOException {
//        String name = "dollar";
//        String code = "USD";
//        String sign = "$";
//        when(requestMock.getParameter("name")).thenReturn(name);
//        when(requestMock.getParameter("code")).thenReturn(code);
//        when(requestMock.getParameter("sign")).thenReturn(sign);
//        when(exchangeRateServiceMock.create(code, name, sign)).thenThrow(new CurrencyAlreadyExistsException(code));
//
//        exchangeRatesController.doPost(requestMock, responseMock);
//
//        verify(exchangeRateServiceMock).create(code, name, sign);
//        verify(responseMock).setContentType("application/json");
//        verify(responseMock).setStatus(HttpServletResponse.SC_CONFLICT);
//        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Валюта с таким кодом уже существует"));
//    }
//
//    @Test
//    void postShouldReturn500WhenDatabaseIsUnavailable() throws IOException {
//        String name = "dollar";
//        String code = "USD";
//        String sign = "$";
//        CurrencyDaoException e = new CurrencyDaoException();
//        when(requestMock.getParameter("name")).thenReturn(name);
//        when(requestMock.getParameter("code")).thenReturn(code);
//        when(requestMock.getParameter("sign")).thenReturn(sign);
//        when(exchangeRateServiceMock.create(code, name, sign)).thenThrow(e);
//
//        exchangeRatesController.doPost(requestMock, responseMock);
//
//        verify(exchangeRateServiceMock).create(code, name, sign);
//        verify(responseMock).setContentType("application/json");
//        verify(responseMock).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        verify(mapperMock).writeValue(writerMock, new ErrorResponse(e.getMessage()));
//    }
//
//    @Test
//    void postShouldCreateNewCurrency() throws IOException {
//        String name = "dollar";
//        String code = "USD";
//        String sign = "$";
//        Currency currency = new Currency(0, code, name, sign);
//        when(requestMock.getParameter("name")).thenReturn(name);
//        when(requestMock.getParameter("code")).thenReturn(code);
//        when(requestMock.getParameter("sign")).thenReturn(sign);
//        when(exchangeRateServiceMock.create(code, name, sign)).thenReturn(currency);
//
//        exchangeRatesController.doPost(requestMock, responseMock);
//
//        verify(exchangeRateServiceMock).create(code, name, sign);
//        verify(responseMock).setContentType("application/json");
//        verify(responseMock).setStatus(HttpServletResponse.SC_CREATED);
//        verify(mapperMock).writeValue(writerMock, currency);
//    }
}
