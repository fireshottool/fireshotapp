package me.fox.components;

import lombok.NonNull;

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
     * Places an image on the system clipboard.
     *
     * @param image - the image to be added to the system clipboard
     */
    public static void write(@NonNull Image image) {
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
                return this.image;
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