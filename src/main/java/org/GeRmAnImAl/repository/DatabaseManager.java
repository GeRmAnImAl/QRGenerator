package org.GeRmAnImAl.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private Connection connection;

    public DatabaseManager() {
        connect();
    }

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

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:QRGenerator.db");
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }
}

