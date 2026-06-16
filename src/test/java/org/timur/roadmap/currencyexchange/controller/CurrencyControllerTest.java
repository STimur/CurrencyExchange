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
import org.timur.roadmap.currencyexchange.exception.CurrencyNotFoundException;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.service.CurrencyService;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyServiceMock;
    @Mock
    private ObjectMapper mapperMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private PrintWriter writerMock;

    @InjectMocks
    private CurrencyController currencyController;

    @BeforeEach
    void setup() throws IOException {
        when(responseMock.getWriter()).thenReturn(writerMock);
    }

    @Test
    void shouldReturn400WhenCurrencyCodeIsNull() throws IOException {
        when(requestMock.getPathInfo()).thenReturn(null);

        currencyController.doGet(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Код валюты отсутствует в адресе"));
    }

    @Test
    void shouldReturn400WhenCurrencyCodeIsEmpty() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/");

        currencyController.doGet(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Код валюты отсутствует в адресе"));
    }

    @Test
    void shouldReturn404WhenCurrencyNotFound() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/X");
        CurrencyNotFoundException e = new CurrencyNotFoundException("X");
        when(currencyServiceMock.findByCode("X")).thenThrow(e);

        currencyController.doGet(requestMock, responseMock);

        verify(currencyServiceMock).findByCode("X");
        verify(responseMock).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Валюта не найдена"));
    }

    @Test
    void shouldReturnCurrency() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/USD");
        Currency currency = new Currency(1, "", "", "");
        when(currencyServiceMock.findByCode("USD")).thenReturn(currency);

        currencyController.doGet(requestMock, responseMock);

        verify(currencyServiceMock).findByCode("USD");
        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(mapperMock).writeValue(writerMock, currency);
    }
}
