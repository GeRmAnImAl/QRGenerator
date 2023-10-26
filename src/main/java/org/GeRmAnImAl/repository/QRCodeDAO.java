package org.GeRmAnImAl.repository;

import org.GeRmAnImAl.model.QRCode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing QRCode data in a database.
 */
public class QRCodeDAO {

    private DatabaseManager databaseManager;

    /**
     * Constructs a QRCodeDAO with the specified DatabaseManager.
     * @param databaseManager the database manager for managing database connections
     */
    public QRCodeDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Inserts a new QR code into the database.
     * @param qrCode the QRCode object containing the data to be inserted
     * @return true if the insertion is successful, false otherwise
     */
    public boolean insertQRCode(QRCode qrCode) {
        try (Connection conn = this.databaseManager.getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Connection is closed");
                return false;
            }

            String checkSql = "SELECT COUNT(*) FROM qr_codes WHERE text = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, qrCode.getUrl());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // QR code with specified text already exists
                    return false;
                }
            }

            String sql = "INSERT INTO qr_codes(text, qr_code_data) VALUES(?,?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, qrCode.getUrl());
                pstmt.setBytes(2, qrCode.getQrCodeData());
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Queries the database for the QR code data of a specified text.
     * @param text the text for which the QR code data is to be queried
     * @return the QR code data as a byte array, or null if not found
     */
    public byte[] queryQRCode(String text) {
        try (Connection conn = this.databaseManager.getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Connection is closed");
                return null;
            }
            String sql = "SELECT qr_code_data FROM qr_codes WHERE text = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, text);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getBytes("qr_code_data");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Updates the QR code data and text of an existing QR code in the database.
     * @param id the ID of the QR code to be updated
     * @param newText the new text for the QR code
     * @param newQRCodeData the new QR code data
     */
    public void updateQRCode(int id, String newText, byte[] newQRCodeData) {
        try (Connection conn = this.databaseManager.getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Connection is closed");
                return;
            }
            String sql = "UPDATE qr_codes SET text = ?, qr_code_data = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newText);
                pstmt.setBytes(2, newQRCodeData);
                pstmt.setInt(3, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Deletes a QR code from the database based on the specified text.
     * @param text the text of the QR code to be deleted
     * @return true if the deletion is successful, false otherwise
     */
    public boolean deleteQRCode(String text) {
        try (Connection conn = this.databaseManager.getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Connection is closed");
                return false;
            }
            String sql = "DELETE FROM qr_codes WHERE text = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, text);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all QR codes from the database.
     * @return a List of String representing the texts of all QR codes in the database
     */
    public List<String> getAllQRCodes() {
        List<String> qrCodes = new ArrayList<>();
        try (Connection conn = this.databaseManager.getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Connection is closed");
                return qrCodes;  // Return empty list
            }
            String sql = "SELECT text FROM qr_codes";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    qrCodes.add(rs.getString("text"));
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return qrCodes;
    }
}

