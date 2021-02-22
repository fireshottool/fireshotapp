package me.fox.components;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;


/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

public class ClipboardImage {

    /**
     * Retrieves an image from the system clipboard.
     *
     * @return the image from the clipboard or null if no image is found
     */
    public static Image read() {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                return (Image) t.getTransferData(DataFlavor.imageFlavor);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * Places an image on the system clipboard.
     *
     * @param image - the image to be added to the system clipboard
     * @throws IllegalArgumentException if the image is null.
     */
    public static void write(Image image) throws IllegalArgumentException {
        if (image == null) throw new IllegalArgumentException("Image can't be null");

        ImageTransferable transferable = new ImageTransferable(image);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
    }

    static class ImageTransferable implements Transferable {
        private final Image image;

        public ImageTransferable(Image image) {
            this.image = image;
        }

        @SuppressWarnings("NullableProblems")
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (isDataFlavorSupported(flavor)) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == DataFlavor.imageFlavor;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }
    }
}