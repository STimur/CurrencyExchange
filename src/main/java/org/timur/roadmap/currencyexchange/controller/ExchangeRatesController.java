package org.timur.roadmap.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.dto.ErrorResponse;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesController extends HttpServlet {
    private final ExchangeRateService exchangeRateService;
    private final ObjectMapper mapper;

    public ExchangeRatesController() {
        this.exchangeRateService = new ExchangeRateService(new ExchangeRateDao());
        this.mapper = new ObjectMapper();
    }

    public ExchangeRatesController(ExchangeRateService exchangeRateService, ObjectMapper mapper) {
        this.exchangeRateService = exchangeRateService;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRate> exchangeRates = exchangeRateService.findAll();

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), exchangeRates);

        } catch (ExchangeRateDaoException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), new ErrorResponse(e.getMessage()));
        }
    }
}
