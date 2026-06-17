package org.timur.roadmap.currencyexchange.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
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
import java.nio.charset.StandardCharsets;

@WebFilter({"/currencies", "/currency/*", "/exchangeRates", "/exchangeRate/*", "/exchange"})
public class RequestProcessingFilter implements Filter {

    private final ObjectMapper mapper;

    public RequestProcessingFilter() {
        this.mapper = new ObjectMapper();
    }

    public RequestProcessingFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletResponse httpResp = (HttpServletResponse) response;

        request.setCharacterEncoding(StandardCharsets.UTF_8);
        httpResp.setCharacterEncoding(StandardCharsets.UTF_8);
        httpResp.setContentType("application/json");

        try {
            chain.doFilter(request, httpResp);
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException |
                 ExchangeRateNotExistsException | ExchangeRateCurrencyNotExistsException e) {
            writeErrorResponse(httpResp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (CurrencyAlreadyExistsException | ExchangeRateAlreadyExistsException e) {
            writeErrorResponse(httpResp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (CurrencyDaoException | ExchangeRateDaoException e) {
            writeErrorResponse(httpResp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (RuntimeException e) {
            writeErrorResponse(httpResp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Сервер не смог обработать запрос");
        }
    }

    private void writeErrorResponse(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        mapper.writeValue(resp.getWriter(), new ErrorResponse(msg));
    }
}
