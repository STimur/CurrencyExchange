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

    private final CurrencyService currencyService = new CurrencyService(new CurrencyDao());
    private final CurrencyDao repo = new CurrencyDao();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            getAllCurrencies(resp);
        } else {
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
                    repo.insert(
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
        resp.setContentType("application/json");
        try {
            Currency currency = currencyService.findByCode(code);

            if (currency == null) {

                resp.setStatus(
                        HttpServletResponse.SC_NOT_FOUND);

                resp.getWriter().write("""
                    { "error": "Currency not found" }
                    """);

                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), currency);

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            String errorJson = """
            {
              "error": "DB_ERROR",
              "message": "%s"
            }
            """.formatted(e.getMessage());

            resp.getWriter().write(errorJson);
        }
    }

    private void getAllCurrencies(HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            List<Currency> currencies = repo.findAll();

            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), currencies);

        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            String errorJson = """
            {
              "error": "DB_ERROR",
              "message": "%s"
            }
            """.formatted(e.getMessage());

            resp.getWriter().write(errorJson);
        }
    }
}
