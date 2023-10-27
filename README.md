# QR Code Generator Application

This application provides a simple user interface to generate, display, and manage QR codes based on user input.

## Features

- Generate QR codes from URLs.
- Display generated QR codes within the application.
- Store QR codes in a database for later retrieval.
- Manage (add/delete) QR codes through a user interface.
- Copy or save QR codes as image files.
- Right-click context menu for image operations (copy, save).

## Structure

The project is organized into three main packages:

- `org.GeRmAnImAl.model`: Contains the `QRCode` class representing a QR code with associated URL.
- `org.GeRmAnImAl.view`: Contains the `QRGeneratorUI` class for user interface and QR code generation.
- `org.GeRmAnImAl.repository`: Contains the `DatabaseManager` and `QRCodeDAO` classes for database interaction.
- `org.GeRmAnImAl.service`: Contains the `ImageSelection` and `QRGenerator` classes for image handling and QR code generation services.

## Usage

1. Run the `QRGeneratorUI` class to launch the application.
2. Enter a URL in the provided text field.
3. Click "Generate QR Code" to generate a QR code for the entered URL.
4. The generated QR code will appear below the text field.
5. Existing QR codes are listed at the bottom of the window. Select one to display it.
6. Use the right-click context menu on the QR code image to copy or save the image.

## Building

This project can be built into a JAR file using IntelliJ IDEA as follows:

1. Open the project in IntelliJ IDEA.
2. Go to `File` -> `Project Structure`.
3. Select `Artifacts` from the left-hand menu.
4. Click the `+` button, choose `JAR` -> `From modules with dependencies`.
5. Select the module and the main class (`QRGeneratorUI`).
6. Apply the changes and click `OK`.
7. Build the project by going to `Build` -> `Build Artifacts` -> `Build`.

## Dependencies

- ZXing library for QR code generation.
- SQLite for database management.
- Swing for the user interface.

## Notes

- Ensure that the necessary libraries are included in the project classpath before building and running the application.
- The database file is created automatically if it doesn't exist.

## Screenshots

(TODO: Add screenshots of the application in action.)

---

For more information or assistance, feel free to contact the developers.
