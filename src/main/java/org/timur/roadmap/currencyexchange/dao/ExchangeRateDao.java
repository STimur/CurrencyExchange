package org.timur.roadmap.currencyexchange.dao;

import org.timur.roadmap.currencyexchange.exception.ExchangeRateDaoException;
import org.timur.roadmap.currencyexchange.model.Currency;
import org.timur.roadmap.currencyexchange.model.ExchangeRate;
import org.timur.roadmap.currencyexchange.utility.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {

    public List<ExchangeRate> findAll() {
        String sql = """
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

        List<ExchangeRate> result = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                result.add(new ExchangeRate(
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
                ));
            }

        } catch (SQLException e) {
            throw new ExchangeRateDaoException("База данных недоступна", e);
        }

        return result;
    }
}
