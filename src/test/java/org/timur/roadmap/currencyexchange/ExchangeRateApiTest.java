package org.timur.roadmap.currencyexchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.utility.Database;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExchangeRateApiTest {

    private final HttpClient client =
            HttpClient.newHttpClient();

    @BeforeAll
    static void init() {
        Database.init("C:\\Users\\Timur\\IdeaProjects\\CurrencyExchange\\src\\data\\app.db");
    }

    @Test
    void shouldCreateExchangeRate() throws Exception {
        String body = "baseCurrencyCode=USD&targetCurrencyCode=RUB&rate=0.87";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(new URI(
                                "http://localhost:8080/" +
                                        "CurrencyExchange_war/exchangeRates"))
                        .header(
                                "Content-Type",
                                "application/x-www-form-urlencoded")
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(body))
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        ObjectMapper mapper = new ObjectMapper();

        ExchangeRate exchangeRate =
                mapper.readValue(
                        response.body(),
                        ExchangeRate.class);

        assertTrue(exchangeRate.id() > 0);
        assertEquals("USD", exchangeRate.baseCurrency().code());
        assertEquals("RUB", exchangeRate.targetCurrency().code());
        assertEquals(new BigDecimal("0.87"), exchangeRate.rate());

        deleteExchangeRate(exchangeRate.id());
    }

    private void deleteExchangeRate(int id) {

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(
                             "DELETE FROM ExchangeRates WHERE id = ?")) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
