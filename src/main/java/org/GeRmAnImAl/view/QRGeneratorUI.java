package org.GeRmAnImAl.view;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.GeRmAnImAl.model.QRCode;
import org.GeRmAnImAl.repository.DatabaseManager;
import org.GeRmAnImAl.repository.QRCodeDAO;
import org.GeRmAnImAl.service.QRGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.awt.image.BufferedImage;

/**
 * QRGeneratorUI class represents the User Interface for generating, displaying,
 * and managing QR Codes. It extends JFrame and implements the QRGenerator interface.
 */
public class QRGeneratorUI extends JFrame implements QRGenerator {
    private JTextField textField;
    private JLabel urlHeader;
    private JLabel qrCodeLabel;
    private JLabel listQRCodes;
    private QRCodeDAO qrCodeDAO;
    private JList<String> qrCodeList;
    private DefaultListModel<String> listModel;
    private DatabaseManager databaseManager;

    /**
     * Constructor for QRGeneratorUI class.
     * @param qrCodeDAO an instance of QRCodeDAO for interacting with the database
     * @param databaseManager an instance of DatabaseManager for managing database connections
     */
    public QRGeneratorUI(QRCodeDAO qrCodeDAO, DatabaseManager databaseManager) {
        this.qrCodeDAO = qrCodeDAO;
        this.databaseManager = databaseManager;
        setTitle("QR Code Generator");
        setPreferredSize(new Dimension(452, 470));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeTextField();
        initializeButtons();
        initializeQRCodeLabel();
        initializeQRCodeList();
        initializeListPanel();
    }

    public void initializeTextField(){
        textField = new JTextField();
        textField.setText("Enter the URL Here");
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when the component gains focus.
             * If the text field contains the placeholder text, it clears the text field and sets the text color to black.
             * @param e the event to be processed
             */
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals("Enter the URL Here")) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            /**
             * Invoked when the component loses focus.
             * If the text field is empty, it sets the placeholder text and changes the text color to gray.
             * @param e the event to be processed
             */
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText("Enter the URL Here");
                }
            }
        });
    }

    public void initializeButtons(){
        JButton generateButton = new JButton("Generate QR Code");
        generateButton.setEnabled(false);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            /**
             * Called when an insert into the document has occurred.
             * @param e the document event
             */
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTextField();
            }

            /**
             * Called when a remove from the document has occurred.
             * @param e the document event
             */
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTextField();
            }

            /**
             * Gives notification that an attribute or set of attributes changed.
             * @param e the document event
             */
            @Override
            public void changedUpdate(DocumentEvent e) {
                checkTextField();
            }

            /**
             * Checks the text field content and enables/disables the generate button accordingly.
             */
            private void checkTextField() {
                generateButton.setEnabled(!"Enter the URL Here".equals(textField.getText().trim()));
            }
        });

        generateButton.addActionListener(e -> generateQRCode());

        JButton deleteButton = new JButton("Delete Selected QR Code");
        deleteButton.addActionListener(e -> deleteSelectedQRCode());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(generateButton, BorderLayout.NORTH);
        buttonPanel.add(deleteButton, BorderLayout.SOUTH);

        add(buttonPanel, BorderLayout.CENTER);
        generateButton.requestFocusInWindow();
    }

    public void initializeQRCodeLabel(){
        qrCodeLabel = new JLabel();
        urlHeader = new JLabel("URL:");
        JPanel qrCodePanel = new JPanel(new GridBagLayout());
        qrCodePanel.add(qrCodeLabel);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(urlHeader, BorderLayout.NORTH);
        textPanel.add(textField, BorderLayout.CENTER);
        textPanel.add(qrCodePanel, BorderLayout.SOUTH);

        add(textPanel, BorderLayout.NORTH);
    }

    public void initializeQRCodeList(){
        listModel = new DefaultListModel<>();
        qrCodeList = new JList<>(listModel);

        /**
         * Invoked when a list selection event occurs.
         */
        qrCodeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {  // Only handle event once when selection is final
                String selectedText = qrCodeList.getSelectedValue();
                if (selectedText != null) {
                    displayQRCode(selectedText);
                }
            }
        });
    }

    public void initializeListPanel(){
        listQRCodes = new JLabel("Existing QR Codes: ");
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

    /**
     * Generates a QR Code based on the text entered in the text field.
     */
    public void generateQRCode() {
        String text = textField.getText();
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIcon icon = new ImageIcon(qrImage);

            byte[] qrCodeData = convertBufferedImageToByteArray(qrImage);

            QRCode qrCode = new QRCode(text, qrCodeData);

            boolean insertSuccessful = qrCodeDAO.insertQRCode(qrCode);
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

    /**
     * Deletes the selected QR Code from the database and updates the UI.
     */
    public void deleteSelectedQRCode() {
        String selectedText = qrCodeList.getSelectedValue();
        if (selectedText != null) {
            QRCode selectedQRCode = qrCodeDAO.queryQRCode(selectedText);
            if (selectedQRCode != null) {
                boolean deletionSuccessful = qrCodeDAO.deleteQRCode(selectedQRCode);
                if (deletionSuccessful) {
                    populateQRCodeList();  // Refresh the list
                    qrCodeLabel.setIcon(null);  // Clear the displayed QR code
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting QR code.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "QR code data not found for: " + selectedText, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No QR code selected.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Populates the list of QR Codes from the database.
     */
    public void populateQRCodeList(){
        List<String> qrCodes = qrCodeDAO.getAllQRCodes();
        listModel.clear();
        for (String qrCode : qrCodes) {
            listModel.addElement(qrCode);
        }
    }

    /**
     * Converts a BufferedImage to a byte array.
     * @param image the BufferedImage to be converted
     * @return byte array representation of the image
     */
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

    /**
     * Displays the QR Code corresponding to the selected text.
     * @param text the text for which the QR Code needs to be displayed
     */
    public void displayQRCode(String text) {
        QRCode qrCode = qrCodeDAO.queryQRCode(text);
        if (qrCode != null) {
            byte[] qrCodeData = qrCode.getQrCodeData();
            ImageIcon icon = new ImageIcon(qrCodeData);
            qrCodeLabel.setIcon(icon);
        } else {
            JOptionPane.showMessageDialog(this, "QR code data not found for: " + text, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Overrides the processWindowEvent method to handle window closing event.
     * @param e the window event
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            databaseManager.closeConnection();
        }
    }
}
