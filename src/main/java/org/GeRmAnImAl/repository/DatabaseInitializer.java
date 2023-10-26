package org.GeRmAnImAl.repository;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * This class handles the initialization of the database,
 * specifically the creation of necessary tables.
 */
public class DatabaseInitializer {

    private DatabaseManager databaseManager;

    /**
     * Constructs a DatabaseInitializer with a specified DatabaseManager.
     * @param databaseManager the DatabaseManager to be used for database operations
     */
    public DatabaseInitializer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Creates a new table named 'qr_codes' in the database if it doesn't already exist.
     * The table has columns for id, text, and qr_code_data.
     */
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

    /**
     * Gets the current DatabaseManager associated with this DatabaseInitializer.
     * @return the current DatabaseManager
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * Sets a new DatabaseManager for this DatabaseInitializer.
     * @param databaseManager the new DatabaseManager to be used for database operations
     */
    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}

