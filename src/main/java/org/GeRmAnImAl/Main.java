package org.GeRmAnImAl;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QRGeneratorUI qrGeneratorUI = new QRGeneratorUI();
            qrGeneratorUI.setVisible(true);
        });
    }

}