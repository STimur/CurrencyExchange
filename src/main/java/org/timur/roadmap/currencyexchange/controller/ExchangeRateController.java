package org.timur.roadmap.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.dao.ExchangeRateDao;
import org.timur.roadmap.currencyexchange.dto.ErrorResponse;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateNotFoundException;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateController extends HttpServlet {

    private final ExchangeRateService exchangeRateService;
    private final ObjectMapper mapper;

    public ExchangeRateController() {
        this.exchangeRateService = new ExchangeRateService(new ExchangeRateDao(), new CurrencyDao());
        this.mapper = new ObjectMapper();
    }

    public ExchangeRateController(ExchangeRateService exchangeRateService, ObjectMapper mapper) {
        this.exchangeRateService = exchangeRateService;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), new ErrorResponse("Коды валют пары отсутствуют в адресе"));
            return;
        }

        String baseCurrencyCode = path.substring(1, 4);
        String targetCurrencyCode = path.substring(4, 7);
        try {
            ExchangeRate exchangeRate = exchangeRateService.findByCodePair(baseCurrencyCode, targetCurrencyCode);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), exchangeRate);

        } catch (ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), new ErrorResponse("Обменный курс для пары не найден"));
        }
        catch (ExchangeRateDaoException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), new ErrorResponse(e.getMessage()));
        }
    }
}
