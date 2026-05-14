package com.example.lib.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.example.lib.utils.UtilsConstants.*;

/**
 * Utility methods for image conversion, duplication, and persistence.
 * <p>
 * This class provides support for:
 * <ul>
 *   <li>Converting a {@link BufferedImage} to a 2D integer matrix for signal-style processing</li>
 *   <li>Converting a 2D integer matrix back to a grayscale image</li>
 *   <li>Creating a deep copy of an image</li>
 *   <li>Saving an image to disk in BMP format</li>
 * </ul>
 * </p>
 * <p>
 * The conversion routine pads image dimensions to multiples of 8, which is useful
 * for block-based algorithms such as DCT.
 * </p>
 */
public class ImageUtils {

    /**
     * Converts a grayscale {@link BufferedImage} into a 2D integer array.
     * <p>
     * If image dimensions are not multiples of 8, the method pads the signal by
     * replicating edge pixels. This avoids introducing artificial black borders and
     * keeps boundary values coherent for block-based processing.
     * </p>
     *
     * @param img source image (expected grayscale; channel 0 is read)
     * @return padded matrix of pixel samples where {@code signal[y][x]} is the sample at {@code (x, y)}
     */
    public static double[][] convertImageToArray(BufferedImage img){
        int origWidth = img.getWidth();
        int origHeight = img.getHeight();

        int width = (origWidth % 8 == 0) ? origWidth : (origWidth / 8 + 1) * 8;
        int height = (origHeight % 8 == 0) ? origHeight : (origHeight / 8 + 1) * 8;

        double[][] signal = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (x < origWidth && y < origHeight) {
                    signal[y][x] = img.getRaster().getSample(x, y, 0);
                } else {
                    int px = Math.min(x, origWidth - 1);
                    int py = Math.min(y, origHeight - 1);
                    signal[y][x] = img.getRaster().getSample(px, py, 0);
                }
            }
        }
        return signal;
    }

    /**
     * Converts a 2D integer matrix into a grayscale {@link BufferedImage}.
     * <p>
     * Each matrix value is written directly into the single channel of a
     * {@link BufferedImage#TYPE_BYTE_GRAY} image.
     * </p>
     *
     * @param signal image samples matrix where {@code signal[y][x]} is the sample at {@code (x, y)}
     * @return grayscale image with matrix width/height
     */
    public static BufferedImage convertArrayToImage(double[][] signal) {

        int height = signal.length;
        int width = signal[0].length;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                img.getRaster().setSample(x, y, 0, signal[y][x]);
            }
        }

        return img;
    }

    /**
     * Creates a deep copy of a {@link BufferedImage}.
     * <p>
     * The copy is rendered into a new {@link BufferedImage#TYPE_INT_RGB} buffer so that
     * pixel data is physically independent from the source image.
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

    /**
     * Converts an image to an RGB {@link BufferedImage} using a white background.
     * <p>
     * This is useful when a preview or export path needs a predictable RGB buffer
     * regardless of the source image type.
     * </p>
     *
     * @param image source image
     * @return a new RGB image containing the rendered source image
     */
    public static BufferedImage toRgbImage(BufferedImage image) {
        BufferedImage rgb = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = rgb.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rgb;
    }

    /**
     * Scales an image to fit inside the provided bounds while preserving aspect ratio.
     * <p>
     * The returned image keeps the source proportions and is rendered smoothly.
     * </p>
     *
     * @param image source image
     * @param maxWidth maximum allowed width
     * @param maxHeight maximum allowed height
     * @return scaled image instance ready for preview rendering
     */
    public static Image scaleImageToFit(BufferedImage image, int maxWidth, int maxHeight) {
        double ratio = Math.min(
                (double) maxWidth / image.getWidth(),
                (double) maxHeight / image.getHeight()
        );

        int scaledW = Math.max((int) (image.getWidth() * ratio), 1);
        int scaledH = Math.max((int) (image.getHeight() * ratio), 1);

        return image.getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);
    }

    /**
     * Returns the size of a file in kilobytes.
     *
     * @param file file to inspect
     * @return file size in kilobytes
     */
    public static double fileSizeInKb(File file) {
        return file.length() / 1024.0;
    }

    /**
     * Saves a {@link BufferedImage} as a BMP file on disk.
     * <p>
     * The method appends {@code ".bmp"} to the provided path, writes the file via
     * and prints
     * the absolute output path to standard output.
     * </p>
     *
     * @param img image to persist
     * @param path destination path without extension
     * @throws RuntimeException if the file cannot be written
     */
    public static void saveAsBMP(BufferedImage img, String path) {
        try {
            File output = new File(path + DOT_BMP);

            ImageIO.write(img, BMP, output);

            System.out.println(SAVE_IMAGE_TO + output.getAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException(SAVING_ERROR, e);
        }
    }
}