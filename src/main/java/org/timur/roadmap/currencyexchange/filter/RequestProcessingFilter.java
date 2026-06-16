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
import org.timur.roadmap.currencyexchange.exception.CurrencyDaoException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")
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
        } catch (CurrencyDaoException | ExchangeRateDaoException e) {
            httpResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(response.getWriter(), new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            httpResp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(response.getWriter(), new ErrorResponse("Сервер не смог обработать запрос"));
        }
    }
}
