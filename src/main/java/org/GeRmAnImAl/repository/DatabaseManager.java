package org.GeRmAnImAl.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    public Connection connect() {
        Connection conn = null;
        try {
            String URL = "jdbc:sqlite:QRGenerator.db";
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}

