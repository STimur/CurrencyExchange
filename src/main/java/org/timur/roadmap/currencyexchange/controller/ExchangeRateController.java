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
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

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
            mapper.writeValue(resp.getWriter(), new ErrorResponse("Коды валют пары отсутствуют в адресе"));
            return;
        }

        String baseCurrencyCode = path.substring(1, 4);
        String targetCurrencyCode = path.substring(4, 7);

        ExchangeRate exchangeRate = exchangeRateService.findByCodePair(baseCurrencyCode, targetCurrencyCode);

        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), exchangeRate);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            throw new BadRequestException("Коды валют пары отсутствуют в адресе");
        }

        String rate = extractRate(req);
        String baseCurrencyCode = path.substring(1, 4);
        String targetCurrencyCode = path.substring(4, 7);

        ExchangeRate exchangeRate =
                exchangeRateService.update(baseCurrencyCode, targetCurrencyCode, new BigDecimal(rate));

        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), exchangeRate);
    }

    private String extractRate(HttpServletRequest req) throws IOException {
        final String exceptionMessage = "Отсутствует нужное поле формы";

        String body = req.getReader().readLine();
        if (body == null || body.isBlank()) {
            throw new BadRequestException(exceptionMessage);
        }

        String[] parts = body.split("=", 2);
        if (parts.length != 2) {
            throw new BadRequestException(exceptionMessage);
        }

        String decodedRate = URLDecoder.decode(parts[1], UTF_8);
        if (decodedRate.isBlank()) {
            throw new BadRequestException(exceptionMessage);
        }

        return decodedRate;
    }
}
