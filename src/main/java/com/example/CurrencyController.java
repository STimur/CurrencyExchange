package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet({"/currencies", "/currency/*"})
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
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        if (req.getServletPath().equals("/currencies")) {
            getAllCurrencies(resp);
        } else {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(resp.getWriter(), new ErrorResponse("Код валюты отсутствует в адресе"));
                return;
            }
            String code = path.substring(1);
            getCurrencyByCode(code, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws IOException {

        String code = req.getParameter("code");
        String fullName = req.getParameter("fullName");
        String sign = req.getParameter("sign");

        if (code == null || code.isBlank()
                || fullName == null || fullName.isBlank()
                || sign == null || sign.isBlank()) {

            resp.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST);

            resp.setContentType("application/json");

            resp.getWriter().write("""
                    {
                      "message":"Missing required fields"
                    }
                    """);

            return;
        }

        try {

            Currency currency =
                    currencyService.create(
                            code,
                            fullName,
                            sign);

            resp.setStatus(
                    HttpServletResponse.SC_CREATED);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            mapper.writeValue(
                    resp.getWriter(),
                    currency
            );

        } catch (Exception e) {

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            resp.setContentType("application/json");

            resp.getWriter().write("""
                    {
                      "message":"Database error"
                    }
                    """);
        }
    }

    private void getCurrencyByCode(String code, HttpServletResponse resp) throws IOException {
        try {
            Currency currency = currencyService.findByCode(code);

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), currency);

        } catch (CurrencyNotFoundException e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(resp.getWriter(), new ErrorResponse("Валюта не найдена"));

        } catch (CurrencyDaoException e) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(resp.getWriter(), new ErrorResponse(e.getMessage()));
        }
    }

    private void getAllCurrencies(HttpServletResponse resp) throws IOException {
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
}
