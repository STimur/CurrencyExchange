package org.timur.roadmap.currencyexchange.dao;

import org.timur.roadmap.currencyexchange.exception.DuplicateExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.utility.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao {

    public static final String UPDATE_SQL = """
            UPDATE ExchangeRates
            SET rate = ?
            WHERE base_currency_id = (
                SELECT id
                FROM Currencies
                WHERE code = ?
            )
            AND target_currency_id = (
                SELECT id
                FROM Currencies
                WHERE code = ?
            );
            """;

    public static final String SELECT_BY_CODE_PAIR_SQL = """
            SELECT
                er.id,
            
                bc.id AS bc_id,
                bc.full_name AS bc_full_name,
                bc.code AS bc_code,
                bc.sign AS bc_sign,
            
                tc.id AS tc_id,
                tc.full_name AS tc_full_name,
                tc.code AS tc_code,
                tc.sign AS tc_sign,
            
                er.rate
            FROM ExchangeRates er
            JOIN Currencies bc ON er.base_currency_id = bc.id
            JOIN Currencies tc ON er.target_currency_id = tc.id
            WHERE bc.code = ? AND tc.code = ?;
            """;

    public static final String SELECT_ALL_SQL = """
            SELECT
                er.id,
            
                bc.id AS bc_id,
                bc.full_name AS bc_full_name,
                bc.code AS bc_code,
                bc.sign AS bc_sign,
            
                tc.id AS tc_id,
                tc.full_name AS tc_full_name,
                tc.code AS tc_code,
                tc.sign AS tc_sign,
            
                er.rate
            FROM ExchangeRates er
            JOIN Currencies bc ON er.base_currency_id = bc.id
            JOIN Currencies tc ON er.target_currency_id = tc.id;
            """;

    public static final String INSERT_SQL = """
            INSERT INTO ExchangeRates (base_currency_id, target_currency_id, rate)
            SELECT bc.id, tc.id, ?
            FROM Currencies bc JOIN Currencies tc
            WHERE bc.code = ? AND tc.code = ?;
            """;

    public List<ExchangeRate> findAll() {

        List<ExchangeRate> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                result.add(map(rs));
            }

        } catch (SQLException e) {
            throw new ExchangeRateDaoException(e);
        }

        return result;
    }

    public Optional<ExchangeRate> findByCodePair(String baseCurrencyCode, String targetCurrencyCode) {

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(SELECT_BY_CODE_PAIR_SQL)) {

            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new ExchangeRateDaoException(e);
        }
    }

    public int insert(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(
                             INSERT_SQL,
                             Statement.RETURN_GENERATED_KEYS)) {

            stmt.setBigDecimal(1, rate);
            stmt.setString(2, baseCurrencyCode);
            stmt.setString(3, targetCurrencyCode);

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException("Failed to obtain generated id");
                }

                return keys.getInt(1);
            }

        } catch (SQLException e) {
            if (isUniqueConstraintViolation(e)) {
                throw new DuplicateExchangeRateDaoException(e);
            }

            throw new ExchangeRateDaoException(e);
        }
    }

    private boolean isUniqueConstraintViolation(SQLException e) {
        return e.getErrorCode() == 19
                && e.getMessage().contains("UNIQUE");
    }

    public Optional<ExchangeRate> update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {

        try (Connection conn = Database.getConnection()) {

            int updated;

            try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
                ps.setBigDecimal(1, rate);
                ps.setString(2, baseCurrencyCode);
                ps.setString(3, targetCurrencyCode);

                updated = ps.executeUpdate();
            }

            if (updated == 0) {
                return Optional.empty();
            }

            try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_CODE_PAIR_SQL)) {
                ps.setString(1, baseCurrencyCode);
                ps.setString(2, targetCurrencyCode);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(map(rs));
                    }
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new ExchangeRateDaoException(e);
        }
    }

    private static ExchangeRate map(ResultSet rs) throws SQLException {
        return new ExchangeRate(
                rs.getInt("id"),
                new Currency(
                        rs.getInt("bc_id"),
                        rs.getString("bc_code"),
                        rs.getString("bc_full_name"),
                        rs.getString("bc_sign")
                ),
                new Currency(
                        rs.getInt("tc_id"),
                        rs.getString("tc_code"),
                        rs.getString("tc_full_name"),
                        rs.getString("tc_sign")
                ),
                rs.getBigDecimal("rate")
        );
    }
}
