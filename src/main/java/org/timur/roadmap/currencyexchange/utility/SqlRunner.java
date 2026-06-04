package org.timur.roadmap.currencyexchange.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class SqlRunner {

    public static void runSqlFile(Connection conn,
                                  String resourcePath)
            throws Exception {

        InputStream is = SqlRunner.class
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (is == null) {
            throw new RuntimeException(
                    "SQL file not found: " + resourcePath);
        }

        String sql;

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     is,
                                     StandardCharsets.UTF_8))) {

            sql = reader.lines()
                    .collect(Collectors.joining("\n"));
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}