package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrenciesControllerTest {

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
    private CurrenciesController currenciesController;

    @BeforeEach
    void setup() throws IOException {
        when(responseMock.getWriter()).thenReturn(writerMock);
    }

    @Test
    void getCurrenciesReturn500WhenErrorOnServer() throws IOException {
        CurrencyDaoException e = new CurrencyDaoException();
        when(currencyServiceMock.findAll()).thenThrow(e);

        currenciesController.doGet(requestMock, responseMock);

        verify(currencyServiceMock).findAll();
        verify(responseMock).setContentType("application/json");
        verify(responseMock).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldReturnAllCurrencies() throws IOException {
        List<Currency> currencies = List.of();
        when(currencyServiceMock.findAll()).thenReturn(currencies);

        currenciesController.doGet(requestMock, responseMock);

        verify(currencyServiceMock).findAll();
        verify(responseMock).setContentType("application/json");
        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(mapperMock).writeValue(writerMock, currencies);
    }
}
