package org.GeRmAnImAl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class QRGeneratorUI extends JFrame implements QRGenerator{
    private JTextField textField;
    private JLabel qrCodeLabel;

    public QRGeneratorUI() {
        setTitle("QR Code Generator");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textField = new JTextField();
        JButton generateButton = new JButton("Generate QR Code");
        qrCodeLabel = new JLabel();

        generateButton.addActionListener(e -> generateQRCode());

        setLayout(new BorderLayout());
        add(textField, BorderLayout.NORTH);
        add(generateButton, BorderLayout.SOUTH);
        add(qrCodeLabel, BorderLayout.CENTER);
    }

    public void generateQRCode() {
        String text = textField.getText();
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIcon icon = new ImageIcon(qrImage);
            qrCodeLabel.setIcon(icon);
        } catch (WriterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating QR code: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
