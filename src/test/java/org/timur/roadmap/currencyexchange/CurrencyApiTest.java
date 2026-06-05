package org.timur.roadmap.currencyexchange;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.utility.Database;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyApiTest {

    private final HttpClient client =
            HttpClient.newHttpClient();

    @BeforeAll
    static void init() {
        Database.init("C:\\Users\\Timur\\IdeaProjects\\CurrencyExchange\\src\\data\\app.db");
    }

    @Test
    void shouldReturnCurrencyByCode() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(
                        "http://localhost:8080/" +
                                "CurrencyExchange_war/currency/EUR"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        assertEquals(200, response.statusCode());

        ObjectMapper mapper = new ObjectMapper();

        Currency currency = mapper.readValue(response.body(), Currency.class);
        assertEquals(2, currency.id());
        assertEquals("EUR", currency.code());
        assertEquals("Euro", currency.fullName());
        assertEquals("€", currency.sign());
    }

    @Test
    void shouldReturnListOfAllCurrencies() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(
                        "http://localhost:8080/" +
                                "CurrencyExchange_war/currencies"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        assertEquals(200, response.statusCode());

        ObjectMapper mapper = new ObjectMapper();

        List<Currency> currencies = mapper.readValue(response.body(), new TypeReference<>() {
        });

        assertEquals(4, currencies.size());
        assertEquals("USD", currencies.getFirst().code());
        assertEquals("RUB", currencies.getLast().code());
    }

    @Test
    void shouldCreateCurrency() throws Exception {

        try {
            String body = "name=TEST&code=TEST&sign=TEST";

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(new URI(
                                    "http://localhost:8080/" +
                                            "CurrencyExchange_war/currencies"))
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

            Currency currency =
                    mapper.readValue(
                            response.body(),
                            Currency.class);

            assertTrue(currency.id() > 0);
            assertEquals("TEST", currency.code());
            assertEquals("TEST", currency.fullName());
            assertEquals("TEST", currency.sign());
        } finally {
            deleteCurrency("TEST");
        }
    }

    private void deleteCurrency(String code) {

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(
                             "DELETE FROM Currencies WHERE code = ?")) {

            stmt.setString(1, code);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
