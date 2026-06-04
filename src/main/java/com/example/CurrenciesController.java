package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {
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
}
