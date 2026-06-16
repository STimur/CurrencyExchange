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
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotFoundException;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.SQLException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateControllerTest {

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
    private ExchangeRateController exchangeRateController;

    @BeforeEach
    void setup() throws IOException {
        when(responseMock.getWriter()).thenReturn(writerMock);
    }

    @Test
    void shouldReturn400WhenCurrencyPairIsNull() throws IOException {
        when(requestMock.getPathInfo()).thenReturn(null);

        exchangeRateController.doGet(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Коды валют пары отсутствуют в адресе"));
    }

    @Test
    void shouldReturn400WhenCurrencyPairIsEmpty() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/");

        exchangeRateController.doGet(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Коды валют пары отсутствуют в адресе"));
    }

    @Test
    void shouldReturn404WhenExchangeRateNotFound() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/USDNEX");
        ExchangeRateNotFoundException e = new ExchangeRateNotFoundException("Обменный курс для пары не найден");
        when(exchangeRateServiceMock.findByCodePair("USD", "NEX")).thenThrow(e);

        exchangeRateController.doGet(requestMock, responseMock);

        verify(exchangeRateServiceMock).findByCodePair("USD", "NEX");
        verify(responseMock).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Обменный курс для пары не найден"));
    }

    @Test
    void shouldReturnExchangeRate() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/USDEUR");
        ExchangeRate exchangeRate = new ExchangeRate(0, null, null, null);
        when(exchangeRateServiceMock.findByCodePair("USD", "EUR")).thenReturn(exchangeRate);

        exchangeRateController.doGet(requestMock, responseMock);

        verify(exchangeRateServiceMock).findByCodePair("USD", "EUR");
        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(mapperMock).writeValue(writerMock, exchangeRate);
    }

    @Test
    void patchShouldReturn400WhenEmptyOrNullPath() throws IOException {
        when(requestMock.getPathInfo()).thenReturn("/");

        exchangeRateController.doPatch(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Коды валют пары отсутствуют в адресе"));
    }

    @Test
    void patchShouldReturn400WhenInvalidInput() throws IOException {
        BufferedReader br = new BufferedReader(new StringReader("\n"));
        when(requestMock.getPathInfo()).thenReturn("/USDEUR");
        when(requestMock.getReader()).thenReturn(br);

        exchangeRateController.doPatch(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse("Отсутствует нужное поле формы"));
    }

    @Test
    void patchShouldReturn404WhenExchangeRateNotFound() throws IOException {
        String rate = "0.8";
        BufferedReader br = new BufferedReader(new StringReader("rate=" + rate));
        when(requestMock.getPathInfo()).thenReturn("/USDNEX");
        when(requestMock.getReader()).thenReturn(br);
        ExchangeRateNotFoundException e = new ExchangeRateNotFoundException("Валютная пара отсутствует в базе данных");
        when(exchangeRateServiceMock.update("USD", "NEX", new BigDecimal(rate))).thenThrow(e);

        exchangeRateController.doPatch(requestMock, responseMock);

        verify(exchangeRateServiceMock).update("USD", "NEX", new BigDecimal(rate));
        verify(responseMock).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mapperMock).writeValue(writerMock, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldUpdateExchangeRate() throws IOException {
        String rate = "0.8";
        BufferedReader br = new BufferedReader(new StringReader("rate=" + rate));
        when(requestMock.getPathInfo()).thenReturn("/USDEUR");
        when(requestMock.getReader()).thenReturn(br);
        ExchangeRate exchangeRate = new ExchangeRate(0, null, null, null);
        when(exchangeRateServiceMock.update("USD", "EUR", new BigDecimal(rate))).thenReturn(exchangeRate);

        exchangeRateController.doPatch(requestMock, responseMock);

        verify(exchangeRateServiceMock).update("USD", "EUR", new BigDecimal(rate));
        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(mapperMock).writeValue(writerMock, exchangeRate);
    }
}
