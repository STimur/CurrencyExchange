package org.timur.roadmap.currencyexchange.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.timur.roadmap.currencyexchange.dto.ErrorResponse;
import org.timur.roadmap.currencyexchange.exception.CurrencyAlreadyExistsException;
import org.timur.roadmap.currencyexchange.exception.CurrencyDaoException;
import org.timur.roadmap.currencyexchange.exception.CurrencyNotFoundException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateCurrencyNotExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestProcessingFilterTest {

    @Mock
    private ServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private PrintWriter writer;

    @InjectMocks
    private RequestProcessingFilter requestProcessingFilter;

    @Test
    void shouldSetEncodingAndContentType() throws IOException, ServletException {

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(request).setCharacterEncoding(StandardCharsets.UTF_8);
        verify(response).setCharacterEncoding(StandardCharsets.UTF_8);
        verify(response).setContentType("application/json");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturn500WhenCurrencyDaoException() throws IOException, ServletException {
        CurrencyDaoException e = new CurrencyDaoException(null);
        doThrow(e).when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(writer);

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(mapper).writeValue(writer, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldReturn500WhenExchangeRateDaoException() throws IOException, ServletException {
        ExchangeRateDaoException e = new ExchangeRateDaoException(null);
        doThrow(e).when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(writer);

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(mapper).writeValue(writer, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldReturn500WhenRuntimeException() throws IOException, ServletException {
        RuntimeException e = new RuntimeException("Сервер не смог обработать запрос");
        doThrow(e).when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(writer);

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(mapper).writeValue(writer, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldReturn409WhenCurrencyAlreadyExistsException() throws IOException, ServletException {
        CurrencyAlreadyExistsException e = new CurrencyAlreadyExistsException(null);
        doThrow(e).when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(writer);

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(mapper).writeValue(writer, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldReturn409WhenExchangeRateAlreadyExistsException() throws IOException, ServletException {
        ExchangeRateAlreadyExistsException e = new ExchangeRateAlreadyExistsException(null);
        doThrow(e).when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(writer);

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(mapper).writeValue(writer, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldReturn404WhenCurrencyNotFoundException() throws IOException, ServletException {
        CurrencyNotFoundException e = new CurrencyNotFoundException();
        doThrow(e).when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(writer);

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mapper).writeValue(writer, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldReturn404WhenExchangeRateNotFoundException() throws IOException, ServletException {
        ExchangeRateNotFoundException e = new ExchangeRateNotFoundException();
        doThrow(e).when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(writer);

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mapper).writeValue(writer, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldReturn404WhenExchangeRateNotExistsException() throws IOException, ServletException {
        ExchangeRateNotExistsException e = new ExchangeRateNotExistsException();
        doThrow(e).when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(writer);

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mapper).writeValue(writer, new ErrorResponse(e.getMessage()));
    }

    @Test
    void shouldReturn404WhenExchangeRateCurrencyNotExistsException() throws IOException, ServletException {
        ExchangeRateCurrencyNotExistsException e = new ExchangeRateCurrencyNotExistsException();
        doThrow(e).when(filterChain).doFilter(request, response);
        when(response.getWriter()).thenReturn(writer);

        requestProcessingFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(mapper).writeValue(writer, new ErrorResponse(e.getMessage()));
    }
}