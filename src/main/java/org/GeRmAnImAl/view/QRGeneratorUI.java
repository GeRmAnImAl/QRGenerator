package org.GeRmAnImAl.view;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.GeRmAnImAl.repository.DatabaseManager;
import org.GeRmAnImAl.repository.QRCodeDAO;
import org.GeRmAnImAl.service.QRGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.awt.image.BufferedImage;

public class QRGeneratorUI extends JFrame implements QRGenerator {
    private JTextField textField;
    private JLabel qrCodeLabel;
    private JLabel listQRCodes;
    private QRCodeDAO qrCodeDAO;
    private JList<String> qrCodeList;
    private DefaultListModel<String> listModel;
    private DatabaseManager databaseManager;

    public QRGeneratorUI(QRCodeDAO qrCodeDAO, DatabaseManager databaseManager) {
        this.qrCodeDAO = qrCodeDAO;
        this.databaseManager = databaseManager;
        setTitle("QR Code Generator");
        setPreferredSize(new Dimension(452, 466));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textField = new JTextField();
        textField.setText("Enter the URL Here");
        JButton generateButton = new JButton("Generate QR Code");
        generateButton.setPreferredSize(new Dimension(452, 50));
        generateButton.setEnabled(false);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTextField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTextField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkTextField();
            }

            private void checkTextField() {
                generateButton.setEnabled(!"Enter the URL Here".equals(textField.getText().trim()));
            }
        });

        generateButton.addActionListener(e -> generateQRCode());

        qrCodeLabel = new JLabel();
        listQRCodes = new JLabel("Existing QR Codes: ");

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(textField, BorderLayout.NORTH);
        textPanel.add(qrCodeLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(generateButton, BorderLayout.CENTER);

        add(textPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        qrCodeList = new JList<>(listModel);

        qrCodeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {  // Only handle event once when selection is final
                String selectedText = qrCodeList.getSelectedValue();
                if (selectedText != null) {
                    displayQRCode(selectedText);
                }
            }
        });

        JPanel listPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(qrCodeList);
        scrollPane.setMaximumSize(new Dimension(452, 300));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        listPanel.add(listQRCodes, BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        add(listPanel, BorderLayout.SOUTH);

        populateQRCodeList();
    }

    public void generateQRCode() {
        String text = textField.getText();
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIcon icon = new ImageIcon(qrImage);

            byte[] qrCodeData = convertBufferedImageToByteArray(qrImage);

            boolean insertSuccessful = qrCodeDAO.insertQRCode(text, qrCodeData);
            if(!insertSuccessful){
                JOptionPane.showMessageDialog(this, "A QR Code Already Exists For This URL.",
                        "Duplicate URL", JOptionPane.WARNING_MESSAGE);
                return;
            }

            qrCodeLabel.setIcon(icon);
            populateQRCodeList();
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

    public byte[] convertBufferedImageToByteArray(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error converting image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return baos.toByteArray();
    }

    private void displayQRCode(String text) {
        byte[] qrCodeData = qrCodeDAO.queryQRCode(text);
        if (qrCodeData != null) {
            ImageIcon icon = new ImageIcon(qrCodeData);
            qrCodeLabel.setIcon(icon);
        } else {
            JOptionPane.showMessageDialog(this, "QR code data not found for: " + text, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            databaseManager.closeConnection();
        }
    }
}
