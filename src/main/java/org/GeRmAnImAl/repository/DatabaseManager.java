package org.GeRmAnImAl.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connection and disconnection operations.
 */
public class DatabaseManager {

    private Connection connection;

    /**
     * Constructor for DatabaseManager. Initiates a database connection.
     */
    public DatabaseManager() {
        connect();
    }

    /**
     * Retrieves the current database connection, establishing a new connection if necessary.
     * @return the current database Connection object
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return connection;
    }

    /**
     * Establishes a new database connection.
     */
    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:QRGenerator.db");
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Closes the current database connection if it is open.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;  // Optionally set connection to null after closing
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}

