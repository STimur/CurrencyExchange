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
import org.timur.roadmap.currencyexchange.model.Conversion;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeControllerTest {

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
    private ExchangeController exchangeController;

    @BeforeEach
    void setup() throws IOException {
        when(responseMock.getWriter()).thenReturn(writerMock);
    }

    @Test
    void shouldReturnConversion() throws IOException {
        Conversion conversion = new Conversion(null, null, null, null, null);
        when(requestMock.getParameter("from")).thenReturn("USD");
        when(requestMock.getParameter("to")).thenReturn("AUD");
        when(requestMock.getParameter("amount")).thenReturn("10");
        when(exchangeRateServiceMock.convert("USD", "AUD", new BigDecimal("10"))).thenReturn(conversion);

        exchangeController.doGet(requestMock, responseMock);

        verify(exchangeRateServiceMock).convert("USD", "AUD", new BigDecimal("10"));
        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(mapperMock).writeValue(writerMock, conversion);
    }
}
