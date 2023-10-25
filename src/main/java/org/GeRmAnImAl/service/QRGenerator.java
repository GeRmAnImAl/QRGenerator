package org.GeRmAnImAl.service;

import java.awt.image.BufferedImage;

public interface QRGenerator {
    public void generateQRCode();

    public void deleteSelectedQRCode();

    public void populateQRCodeList();

    public byte[] convertBufferedImageToByteArray(BufferedImage image);

    public void displayQRCode(String text);
}
