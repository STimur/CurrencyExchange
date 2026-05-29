package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CurrencyRepository {

    public List<Currency> findAll() {

        String sql = "SELECT * FROM Сurrencies";

        List<Currency> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(new Currency(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("full_name"),
                        rs.getString("sign")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public Currency findByCode(String code) {

        String sql = "SELECT * FROM Сurrencies WHERE code = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    return new Currency(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("full_name"),
                            rs.getString("sign")
                    );
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Currency save(String code,
                         String fullName,
                         String sign) {

        String sql = """
                INSERT INTO Сurrencies(code, full_name, sign)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, code);
            stmt.setString(2, fullName);
            stmt.setString(3, sign);

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException(
                            "Failed to obtain generated id");
                }

                int id = keys.getInt(1);

                return new Currency(
                        id,
                        code,
                        fullName,
                        sign
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
