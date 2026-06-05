package org.timur.roadmap.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.dto.ErrorResponse;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateAlreadyExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateCurrencyNotExistsException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesController extends HttpServlet {
    private final ExchangeRateService exchangeRateService;
    private final ObjectMapper mapper;

    public ExchangeRatesController() {
        this.exchangeRateService = new ExchangeRateService(new ExchangeRateDao(), new CurrencyDao());
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if (baseCurrencyCode == null || baseCurrencyCode.isBlank()
                || targetCurrencyCode == null || targetCurrencyCode.isBlank()
                || rate == null || rate.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), new ErrorResponse("Отсутствует нужное поле формы"));
            return;
        }

        try {
            ExchangeRate exchangeRate =
                    exchangeRateService.create(baseCurrencyCode, targetCurrencyCode, new BigDecimal(rate));

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), exchangeRate);
        }
        catch (ExchangeRateAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), new ErrorResponse(e.getMessage()));
        }
        catch (ExchangeRateCurrencyNotExistsException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(),
                    new ErrorResponse("Одна (или обе) валюта из валютной пары не существует в БД"));
        }
        catch (ExchangeRateDaoException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), new ErrorResponse(e.getMessage()));
        }
    }
}
