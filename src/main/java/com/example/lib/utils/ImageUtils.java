package com.example.lib.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.example.lib.constants.UtilsConstants.*;

/**
 * Utility methods for image conversion and display operations.
 * <p>
 * This class provides methods to convert between {@link BufferedImage} objects and 2D integer arrays,
 * save images to disk in BMP format, and display images in Swing windows. Images are padded to
 * multiples of 8 pixels to facilitate block-based processing (e.g., DCT compression).
 * </p>
 */
public class ImageUtils {
    /**
     * Converts a {@link BufferedImage} to a 2D integer array representing pixel values.
     * <p>
     * The image is padded to the nearest multiple of 8 pixels in both dimensions. Padding is done
     * by edge replication: pixels beyond the original bounds are set to the value of the nearest
     * edge pixel. This is useful for block-based image processing where block sizes are multiples of 8.
     * </p>
     *
     * @param img the {@link BufferedImage} to convert (must be a grayscale image)
     * @return a 2D integer array where {@code signal[y][x]} represents the pixel value at position {@code (x, y)};
     *         the array dimensions are padded to multiples of 8
     */
    public static int[][] convertImageToArray(BufferedImage img){
        int origWidth = img.getWidth();
        int origHeight = img.getHeight();

        int width = (origWidth % 8 == 0) ? origWidth : (origWidth / 8 + 1) * 8;
        int height = (origHeight % 8 == 0) ? origHeight : (origHeight / 8 + 1) * 8;

        int[][] signal = new int[height][width];

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
     * Converts a 2D integer array back into a {@link BufferedImage}.
     * <p>
     * The resulting image is created as a grayscale image with {@link BufferedImage#TYPE_BYTE_GRAY}
     * type. Each integer value in the array is directly set as a pixel value in the image.
     * </p>
     *
     * @param signal a 2D integer array where {@code signal[y][x]} represents the pixel value at position {@code (x, y)}
     * @return a new {@link BufferedImage} with the same dimensions as the input array
     */
    public static BufferedImage convertArrayToImage(int[][] signal) {

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
     * Saves a {@link BufferedImage} to disk in BMP format.
     * <p>
     * The method writes the image to the specified file path using {@link javax.imageio.ImageIO}.
     * The absolute path of the saved file is printed to standard output. If the write operation fails,
     * a {@link RuntimeException} is thrown.
     * </p>
     *
     * @param img the {@link BufferedImage} to save
     * @param path the destination file path (typically includes the filename with BMP extension)
     * @throws RuntimeException if the image cannot be written to the specified path
     */
    public static void saveAsBMP(BufferedImage img, String path) {
        try {
            File output = new File(path);

            ImageIO.write(img, BMP, output);

            System.out.println(SAVE_IMAGE_TO + output.getAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException(SAVING_ERROR, e);
        }
    }

    /**
     * Displays an image in a new Swing window.
     * <p>
     * The method creates a new {@link JFrame} window with the specified title, displays the image
     * using an {@link ImageIcon} wrapped in a {@link JLabel}, and positions the window at the
     * specified coordinates. The window will exit the application when closed.
     * </p>
     *
     * @param img the {@link BufferedImage} to display
     * @param title the title to display in the window's title bar
     * @param x the X coordinate (in screen pixels) where the window should be positioned
     * @param y the Y coordinate (in screen pixels) where the window should be positioned
     */
    public static void showImage(BufferedImage img, String title, int x, int y) {
        JFrame frame = new JFrame();
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icon = new ImageIcon(img);
        JLabel label = new JLabel(icon);

        frame.add(label);
        frame.pack();

        frame.setLocation(x, y);
        frame.setVisible(true);
    }
}
