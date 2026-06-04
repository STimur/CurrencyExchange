package org.timur.roadmap.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.exception.CurrencyAlreadyExistsException;
import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.exception.CurrencyDaoException;
import org.timur.roadmap.currencyexchange.service.CurrencyService;
import org.timur.roadmap.currencyexchange.dto.ErrorResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesController extends HttpServlet {
    private final CurrencyService currencyService;
    private final ObjectMapper mapper;

    public CurrenciesController() {
        this.currencyService = new CurrencyService(new CurrencyDao());
        this.mapper = new ObjectMapper();
    }

    public CurrenciesController(CurrencyService currencyService, ObjectMapper mapper) {
        this.currencyService = currencyService;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Currency> currencies = currencyService.findAll();

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), currencies);

        } catch (CurrencyDaoException e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), new ErrorResponse(e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        if (code == null || code.isBlank() || name == null || name.isBlank() || sign == null || sign.isBlank()) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new ErrorResponse("Отсутствует нужное поле формы"));
            return;
        }

        try {
            Currency currency = currencyService.create(code, name, sign);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), currency);
        }
        catch (CurrencyAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), new ErrorResponse("Валюта с таким кодом уже существует"));
        }
        catch (CurrencyDaoException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            mapper.writeValue(resp.getWriter(), new ErrorResponse(e.getMessage()));
        }
    }
}
