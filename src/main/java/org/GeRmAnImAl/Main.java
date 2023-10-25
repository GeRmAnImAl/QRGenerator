package org.GeRmAnImAl;

import org.GeRmAnImAl.repository.DatabaseInitializer;
import org.GeRmAnImAl.repository.DatabaseManager;
import org.GeRmAnImAl.repository.QRCodeDAO;
import org.GeRmAnImAl.view.QRGeneratorUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();
        DatabaseInitializer databaseInitializer = new DatabaseInitializer(databaseManager);
        databaseInitializer.createNewTable();
        QRCodeDAO qrCodeDAO = new QRCodeDAO(databaseManager);

        SwingUtilities.invokeLater(() -> {
            QRGeneratorUI qrGeneratorUI = new QRGeneratorUI(qrCodeDAO, databaseManager);
            qrGeneratorUI.setVisible(true);
        });
    }

}