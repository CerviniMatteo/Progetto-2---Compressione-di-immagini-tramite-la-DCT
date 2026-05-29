package com.unimib.assignment.GUI.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utility methods for image conversion, duplication, and persistence.
 * <p>This class provides support for:</p>
 * <ul>
 *   <li>Converting a {@link BufferedImage} to a 2D integer matrix for signal-style processing</li>
 *   <li>Converting a 2D integer matrix back to a grayscale image</li>
 *   <li>Creating a deep copy of an image</li>
 *   <li>Saving an image to disk in BMP format</li>
 * </ul>
 * <p>
 * The conversion routine pads image dimensions to multiples of 8, which is useful
 * for block-based algorithms such as DCT.
 * </p>
 */
public class ImageUtils {

    /**
     * Creates a deep copy of a {@link BufferedImage}.
     * <p>
     * The copy is rendered into a new {@link BufferedImage#TYPE_INT_RGB} buffer so that
     * pixel data is physically independent of the source image.
     * </p>
     * <p>
     * Note: alpha information is not preserved because the destination type is RGB.
     * </p>
     *
     * @param bi source image to copy
     * @return independent copied image
     */
    public static BufferedImage copyBufferedImage(BufferedImage bi) {
        // Create a completely independent deep copy using Graphics2D for guaranteed independence
        // This approach ensures no pixel data sharing between original and copy
        int width = bi.getWidth();
        int height = bi.getHeight();

        // Create new BufferedImage with TYPE_INT_RGB to ensure standard pixel format
        BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Use Graphics2D to draw the original onto the copy - this ensures complete independence
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(bi, 0, 0, null);
        g2d.dispose();

        return copy;
    }

}