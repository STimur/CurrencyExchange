package org.timur.roadmap.currencyexchange.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.dao.CurrencyDao;
import org.timur.roadmap.currencyexchange.exception.CurrencyDaoException;
import org.timur.roadmap.currencyexchange.exception.CurrencyNotFoundException;
import org.timur.roadmap.currencyexchange.service.CurrencyService;
import org.timur.roadmap.currencyexchange.dto.ErrorResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyController extends HttpServlet {

    private final CurrencyService currencyService;
    private final ObjectMapper mapper;

    public CurrencyController() {
        this.currencyService = new CurrencyService(new CurrencyDao());
        this.mapper = new ObjectMapper();
    }

    public CurrencyController(CurrencyService currencyService, ObjectMapper mapper) {
        this.currencyService = currencyService;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            mapper.writeValue(resp.getWriter(), new ErrorResponse("Код валюты отсутствует в адресе"));
            return;
        }

        String code = path.substring(1);
        try {
            Currency currency = currencyService.findByCode(code);
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), currency);

        } catch (CurrencyNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getWriter(), new ErrorResponse("Валюта не найдена"));
        }
    }
}
