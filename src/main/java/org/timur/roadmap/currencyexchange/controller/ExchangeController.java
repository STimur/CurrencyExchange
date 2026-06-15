package org.timur.roadmap.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.model.Conversion;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeController extends HttpServlet {
    private final ExchangeRateService exchangeRateService;
    private final ObjectMapper mapper;

    public ExchangeController() {
        this.exchangeRateService = new ExchangeRateService(new ExchangeRateDao(), new CurrencyDao());
        this.mapper = new ObjectMapper();
    }

    public ExchangeController(ExchangeRateService exchangeRateService, ObjectMapper mapper) {
        this.exchangeRateService = exchangeRateService;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amount = req.getParameter("amount");

        Conversion conversion =
                exchangeRateService.convert(baseCurrencyCode, targetCurrencyCode, new BigDecimal(amount));

        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), conversion);
    }
}
