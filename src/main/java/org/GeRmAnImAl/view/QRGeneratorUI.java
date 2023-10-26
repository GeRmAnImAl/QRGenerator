package org.GeRmAnImAl.view;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.GeRmAnImAl.model.QRCode;
import org.GeRmAnImAl.repository.DatabaseManager;
import org.GeRmAnImAl.repository.QRCodeDAO;
import org.GeRmAnImAl.service.ImageSelection;
import org.GeRmAnImAl.service.QRGenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private final QRCodeDAO qrCodeDAO;
    private JList<String> qrCodeList;
    private DefaultListModel<String> listModel;
    private final DatabaseManager databaseManager;
    private JPopupMenu contextMenu;
    private JMenuItem copyMenuItem;
    private JMenuItem saveMenuItem;

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
        initializeQRCodeLabel();
        initializeContextMenu();
    }

    /**
     * Initializes the text field for URL input with a placeholder text.
     * It sets up focus listeners to handle the appearance of placeholder text
     * when the text field gains or loses focus.
     */
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

    /**
     * Initializes the buttons for generating and deleting QR Codes.
     * It sets up action listeners for button clicks and a document listener
     * on the text field to enable or disable the generate button based on text field content.
     */
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

    /**
     * Initializes the QR code label and arranges the UI components within a panel.
     * This method sets up the UI layout for displaying the URL header, text field, and QR code label.
     */
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

    /**
     * Initializes the list component used for displaying the existing QR codes.
     * This method sets up a JList with a DefaultListModel and adds a ListSelectionListener
     * to the list to handle item selection events. When a list item is selected,
     * the corresponding QR code is displayed.
     */
    public void initializeQRCodeList(){
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
    }

    /**
     * Initializes the panel that contains the list of existing QR codes.
     * This method sets up a JPanel with a BorderLayout, creates a JScrollPane containing
     * the QR code list, and adds these components to the panel. Additionally, it calls
     * the method to populate the QR code list from the database.
     */
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
     * Initializes the context menu with "Copy Image" and "Save Image" options.
     * This method creates a new JPopupMenu and associates it with the qrCodeLabel,
     * so that the menu is displayed when the user right-clicks on the label.
     * It also initializes the actions to be taken when the menu items are selected.
     */
    public void initializeContextMenu() {
        contextMenu = new JPopupMenu();
        copyMenuItem = new JMenuItem("Copy Image");
        saveMenuItem = new JMenuItem("Save Image");

        contextMenu.add(copyMenuItem);
        contextMenu.add(saveMenuItem);

        qrCodeLabel.setComponentPopupMenu(contextMenu);

        setupContextMenuActions();
    }

    /**
     * Sets up the actions to be performed when the user selects the options from the context menu.
     * This method specifies the behavior for copying the image to the system clipboard
     * and saving the image to a file when the respective menu items are selected.
     */
    private void setupContextMenuActions() {
        copyMenuItem.addActionListener(e -> {
            ImageIcon icon = (ImageIcon) qrCodeLabel.getIcon();
            if (icon != null) {
                BufferedImage image = (BufferedImage) icon.getImage();
                ImageSelection imgSel = new ImageSelection(image);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
            }
        });

        saveMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
            int userSelection = fileChooser.showSaveDialog(QRGeneratorUI.this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                ImageIcon icon = (ImageIcon) qrCodeLabel.getIcon();
                if (icon != null) {
                    BufferedImage image = (BufferedImage) icon.getImage();
                    try {
                        ImageIO.write(image, "png", new File(fileToSave.getAbsolutePath() + ".png"));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

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

            byte[] qrCodeData = QRCode.convertBufferedImageToByteArray(qrImage);

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
