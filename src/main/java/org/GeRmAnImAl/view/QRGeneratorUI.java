package org.GeRmAnImAl.view;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.GeRmAnImAl.repository.QRCodeDAO;
import org.GeRmAnImAl.service.QRGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;

public class QRGeneratorUI extends JFrame implements QRGenerator {
    private JTextField textField;
    private JLabel qrCodeLabel;
    private QRCodeDAO qrCodeDAO;
    private JList<String> qrCodeList;
    private DefaultListModel<String> listModel;

    public QRGeneratorUI(QRCodeDAO qrCodeDAO) {
        this.qrCodeDAO = qrCodeDAO;
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

        listModel = new DefaultListModel<>();
        qrCodeList = new JList<>(listModel);
        add(new JScrollPane(qrCodeList), BorderLayout.EAST);
        populateQRCodeList();
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

    public void populateQRCodeList(){
        List<String> qrCodes = qrCodeDAO.getAllQRCodes();
        listModel.clear();
        for (String qrCode : qrCodes) {
            listModel.addElement(qrCode);
        }
    }
}
