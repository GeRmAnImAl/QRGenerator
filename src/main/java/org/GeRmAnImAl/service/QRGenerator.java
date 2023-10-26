package org.GeRmAnImAl.service;

import java.awt.image.BufferedImage;

public interface QRGenerator {
    public void generateQRCode();

    public void deleteSelectedQRCode();

    public void populateQRCodeList();

    public void displayQRCode(String text);
}
