package org.GeRmAnImAl.repository;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    private DatabaseManager databaseManager;

    public DatabaseInitializer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS qr_codes (\n"
                + "    id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "    text text NOT NULL,\n"
                + "    qr_code_data blob NOT NULL\n"
                + ");";

        try (Connection conn = databaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}

