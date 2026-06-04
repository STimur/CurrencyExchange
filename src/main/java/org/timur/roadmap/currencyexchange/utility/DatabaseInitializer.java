package org.timur.roadmap.currencyexchange.utility;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;

@WebListener
public class DatabaseInitializer
        implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        String dbPath = System.getProperty("db.path");
        if (dbPath == null) {
            dbPath = System.getenv("DB_PATH");
        }
        if (dbPath == null) {
            throw new IllegalStateException("DB path is not configured");
        }

        Database.init(dbPath);


        try (Connection conn = Database.getConnection()) {

            SqlRunner.runSqlFile(conn, "db/schema.sql");
            SqlRunner.runSqlFile(conn, "db/data.sql");

            System.out.println("Database initialized");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}