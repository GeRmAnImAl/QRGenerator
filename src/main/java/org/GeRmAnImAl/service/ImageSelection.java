package org.GeRmAnImAl.service;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * This class provides the functionality to handle image data for transfer operations,
 * such as copying and pasting. It implements the {@link Transferable} interface,
 * which is used by {@link java.awt.datatransfer.Clipboard} and {@link java.awt.dnd.DragGestureRecognizer}
 * to provide the standard data transfer mechanism in the Java platform.
 */
public class ImageSelection implements Transferable {
    private final Image image;

    /**
     * Constructs an ImageSelection object with the specified image.
     * @param image the image to be transferred
     */
    public ImageSelection(Image image) {
        this.image = image;
    }

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data
     * can be provided in. The array should be ordered according to preference
     * for providing the data (from most richly descriptive to least descriptive).
     * @return an array of data flavors in which data can be transferred
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.imageFlavor };
    }

    /**
     * Returns whether the specified data flavor is supported for this object.
     * @param flavor the requested flavor for the data
     * @return true if the data flavor is supported, false otherwise
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    /**
     * Returns an object which represents the data to be transferred. The class
     * of the object returned is defined by the representation class of the flavor.
     * @param flavor the requested flavor for the data
     * @return the data to be transferred, represented by an Image object
     * @throws UnsupportedFlavorException if the requested data flavor is not supported
     */
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (!DataFlavor.imageFlavor.equals(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return image;
    }
}

