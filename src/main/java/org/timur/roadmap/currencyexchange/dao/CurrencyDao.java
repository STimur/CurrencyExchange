package org.timur.roadmap.currencyexchange.dao;

import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.exception.CurrencyDaoException;
import org.timur.roadmap.currencyexchange.utility.Database;
import org.timur.roadmap.currencyexchange.exception.DuplicateCurrencyDaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {

    public List<Currency> findAll() {

        String sql = "SELECT * FROM Currencies";

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
            throw new CurrencyDaoException("Some DB Error", e);
        }

        return result;
    }

    public Optional<Currency> findByCode(String code) {

        String sql = "SELECT * FROM Currencies WHERE code = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    return Optional.of(new Currency(
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("full_name"),
                            rs.getString("sign")
                    ));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new CurrencyDaoException("Some DB Error", e);
        }
    }

    public Currency insert(String code,
                           String fullName,
                           String sign) {

        String sql = """
                INSERT INTO Currencies(code, full_name, sign)
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
                    throw new SQLException("Failed to obtain generated id");
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
            if (isUniqueConstraintViolation(e)) {
                throw new DuplicateCurrencyDaoException(e);
            }

            throw new CurrencyDaoException(e);
        }
    }

    private boolean isUniqueConstraintViolation(SQLException e) {
        return e.getErrorCode() == 19
                && e.getMessage().contains("UNIQUE");
    }
}
