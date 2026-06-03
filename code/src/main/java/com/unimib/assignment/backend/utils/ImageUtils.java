package com.unimib.assignment.backend.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.unimib.assignment.backend.constants.ImageConstants.*;

/** Class containing images helpers methods*/
public class ImageUtils {
    /**
     * Converts a grayscale {@link BufferedImage} into a 2D double array.
     *
     * @param img source image (expected grayscale; channel 0 is read)
     * @return matrix of pixel samples where {@code signal[y][x]} is the sample at {@code (x, y)}
     */
    public static double[][] convertImageToArray(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();

        double[][] signal = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Read the sample from the first channel (grayscale) and store it in the matrix.
                signal[y][x] = img.getRaster().getSample(x, y, 0);
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
                // Write the sample value from the matrix into the first channel of the image.
                img.getRaster().setSample(x, y, 0, signal[y][x]);
            }
        }

        return img;
    }

    /**
     * Saves a {@link BufferedImage} as a BMP file on disk.
     * <p>
     * The method appends {@code ".bmp"} to the provided path,
     * writes the file via ImageIO write object into the output folder
     * and prints the absolute output path to standard output.
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
