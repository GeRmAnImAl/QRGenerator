package org.GeRmAnImAl.repository;

import java.sql.*;

public class QRCodeDAO {

    private DatabaseManager databaseManager;

    public QRCodeDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void insertQRCode(String text, byte[] qrCodeData) {
        String sql = "INSERT INTO qr_codes(text, qr_code_data) VALUES(?,?)";

        try (Connection conn = this.databaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, text);
            pstmt.setBytes(2, qrCodeData);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public byte[] queryQRCode(int id) {
        String sql = "SELECT qr_code_data FROM qr_codes WHERE id = ?";

        try (Connection conn = this.databaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBytes("qr_code_data");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public void updateQRCode(int id, String newText, byte[] newQRCodeData) {
        String sql = "UPDATE qr_codes SET text = ?, qr_code_data = ? WHERE id = ?";

        try (Connection conn = this.databaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newText);
            pstmt.setBytes(2, newQRCodeData);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}

