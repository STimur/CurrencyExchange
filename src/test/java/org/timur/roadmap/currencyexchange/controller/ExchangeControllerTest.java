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
import org.timur.roadmap.currencyexchange.exception.BadRequestException;
import org.timur.roadmap.currencyexchange.model.Conversion;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeControllerTest {

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
    private ExchangeController exchangeController;

    @BeforeEach
    void setup() throws IOException {
        lenient().when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromIsNull() throws IOException {
        when(request.getParameter("from")).thenReturn(null);

        BadRequestException e = assertThrows(
                BadRequestException.class,
                () -> exchangeController.doGet(request, response)
        );

        assertEquals("Отсутствует нужное поле формы", e.getMessage());
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromIsEmpty() throws IOException {
        when(request.getParameter("from")).thenReturn("");

        BadRequestException e = assertThrows(
                BadRequestException.class,
                () -> exchangeController.doGet(request, response)
        );

        assertEquals("Отсутствует нужное поле формы", e.getMessage());
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenToIsNull() throws IOException {
        when(request.getParameter("from")).thenReturn("test");
        when(request.getParameter("to")).thenReturn(null);

        BadRequestException e = assertThrows(
                BadRequestException.class,
                () -> exchangeController.doGet(request, response)
        );

        assertEquals("Отсутствует нужное поле формы", e.getMessage());
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenToIsEmpty() throws IOException {
        when(request.getParameter("from")).thenReturn("test");
        when(request.getParameter("to")).thenReturn("");

        BadRequestException e = assertThrows(
                BadRequestException.class,
                () -> exchangeController.doGet(request, response)
        );

        assertEquals("Отсутствует нужное поле формы", e.getMessage());
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenAmountIsNull() throws IOException {
        when(request.getParameter("from")).thenReturn("test");
        when(request.getParameter("to")).thenReturn("test");
        when(request.getParameter("amount")).thenReturn(null);

        BadRequestException e = assertThrows(
                BadRequestException.class,
                () -> exchangeController.doGet(request, response)
        );

        assertEquals("Отсутствует нужное поле формы", e.getMessage());
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenAmountIsEmpty() throws IOException {
        when(request.getParameter("from")).thenReturn("test");
        when(request.getParameter("to")).thenReturn("test");
        when(request.getParameter("amount")).thenReturn("");

        BadRequestException e = assertThrows(
                BadRequestException.class,
                () -> exchangeController.doGet(request, response)
        );

        assertEquals("Отсутствует нужное поле формы", e.getMessage());
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenAmountIsNotNumber() throws IOException {
        when(request.getParameter("from")).thenReturn("test");
        when(request.getParameter("to")).thenReturn("test");
        when(request.getParameter("amount")).thenReturn("lol");

        BadRequestException e = assertThrows(
                BadRequestException.class,
                () -> exchangeController.doGet(request, response)
        );

        assertEquals("Отсутствует нужное поле формы", e.getMessage());
        verifyNoInteractions(exchangeRateService);
    }

    @Test
    void shouldReturnConversion() throws IOException {
        Conversion conversion = new Conversion(null, null, null, null, null);
        when(request.getParameter("from")).thenReturn("USD");
        when(request.getParameter("to")).thenReturn("AUD");
        when(request.getParameter("amount")).thenReturn("10");
        when(exchangeRateService.convert("USD", "AUD", new BigDecimal("10"))).thenReturn(conversion);

        exchangeController.doGet(request, response);

        verify(exchangeRateService).convert("USD", "AUD", new BigDecimal("10"));
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(mapper).writeValue(writer, conversion);
    }
}
