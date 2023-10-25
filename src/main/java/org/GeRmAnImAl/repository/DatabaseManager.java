package org.GeRmAnImAl.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private Connection connection;

    public DatabaseManager() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:QRGenerator.db");
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

