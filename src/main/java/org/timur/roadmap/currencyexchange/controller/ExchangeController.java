package org.timur.roadmap.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.dto.ErrorResponse;
import org.timur.roadmap.currencyexchange.exception.BadRequestException;
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
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");

        String errorMessage = "Отсутствует нужное поле формы";
        if (from == null || from.isBlank() || to == null || to.isBlank() || amount == null || amount.isBlank()) {
            throw new BadRequestException(errorMessage);
        }

        BigDecimal amountValue;
        try {
            amountValue = new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new BadRequestException(errorMessage);
        }

        Conversion conversion =
                exchangeRateService.convert(from, to, amountValue);

        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), conversion);
    }
}
