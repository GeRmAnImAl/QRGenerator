package org.GeRmAnImAl.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Represents a QR code with associated URL.
 */
public class QRCode {
    private String url;
    private byte[] qrCodeData;

    /**
     * Constructs a QRCode object with the specified URL and QR code data.
     * @param url the URL to be encoded in the QR code
     * @param qrCodeData the byte array representation of the QR code image
     */
    public QRCode(String url, byte[] qrCodeData) {
        this.url = url;
        this.qrCodeData = qrCodeData;
    }

    /**
     * Gets the URL associated with this QR code.
     * @return the URL of this QR code
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL to be associated with this QR code.
     * @param url the new URL for this QR code
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the QR code data.
     * @return the byte array representation of the QR code image
     */
    public byte[] getQrCodeData() {
        return qrCodeData;
    }

    /**
     * Sets the QR code data.
     * @param qrCodeData the new byte array representation of the QR code image
     */
    public void setQrCodeData(byte[] qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public static byte[] convertBufferedImageToByteArray(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception accordingly
        }
        return baos.toByteArray();
    }
}
