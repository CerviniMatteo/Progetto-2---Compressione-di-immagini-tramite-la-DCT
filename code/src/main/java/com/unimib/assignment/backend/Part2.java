package com.unimib.assignment.backend;

import com.unimib.assignment.backend.utils.ImageUtils;
import org.apache.commons.math3.util.Pair;
import org.jtransforms.dct.DoubleDCT_2D;
import java.awt.image.BufferedImage;

/**
 * Implements block-based grayscale image compression using the 2D Discrete Cosine Transform (DCT).
 * The compressed image is saved as BMP in {@code OUTPUT_PATH}.
 */
public class Part2 {

    /**
     * Default relative output folder used by utility methods.
     */
    public static final String OUTPUT_PATH = "output/";

    /**
     * Compresses an image using block DCT with frequency truncation.
     * <p>
     * Only the largest region whose dimensions are multiples of {@code F} is processed.
     * Any border pixels outside this region is cropped out.
     * </p>
     *
     * @param imageInfo pair containing:
     *                  <ul>
     *                    <li>first: output file name</li>
     *                    <li>second: source {@link BufferedImage}</li>
     *                  </ul>
     * @param F         block size (each block is {@code F x F})
     * @param d         frequency cutoff parameter; coefficients with {@code k + l >= d} are set to zero
     * @return the compressed {@link BufferedImage}
     */
    public BufferedImage compress(Pair<String, BufferedImage> imageInfo, int F, int d) {
        //Extract the second pair element(the image)
        BufferedImage image = imageInfo.getSecond();
        //Convert the image to a 2D array of doubles for processing
        double[][] signal = ImageUtils.convertImageToArray(image);

        //Calculate the largest dimensions that are multiples of F to ensure we only process complete blocks
        int rows = signal.length - signal.length % F;
        int cols = signal[0].length - signal[0].length % F;

        double[][] compressedImage = new double[rows][cols];
        //Cut image using new rows and cols dimension
        for (int i = 0; i < rows; i ++) {
            //Copying only the specified number of columns for each row to the compressedImage array
            System.arraycopy(signal[i], 0, compressedImage[i], 0, cols);
        }

        //Compress blocks
        for (int i = 0; i < rows; i += F) {
            for (int j = 0; j < cols; j += F) {
                //Call the compress block method
                compressBlock(compressedImage, i, j, F, d);
            }
        }

        //Convert the compressed 2D array back to a BufferedImage and save it as BMP
        BufferedImage result = ImageUtils.convertArrayToImage(compressedImage);
        ImageUtils.saveAsBMP(result, OUTPUT_PATH + imageInfo.getFirst());
        return result;
    }

    /**
     * Compresses a single {@code F x F} block of the image signal in place.
     *
     * @param signal full image signal matrix (modified in place)
     * @param i      top row index of the block
     * @param j      left column index of the block
     * @param F      block size
     * @param d      frequency cutoff parameter
     */
    private static void compressBlock(double[][] signal, int i, int j, int F, int d) {

        double[][] block = new double[F][F];

        DoubleDCT_2D dct = new DoubleDCT_2D(F, F);
        //Copy block
        for (int k = 0; k < F; k++) {
            System.arraycopy(signal[i + k], j, block[k], 0, F);
        }
        //Calculate DCT2
        dct.forward(block, true);
        //Cut frequencies
        for (int k = 0; k < F; k++) {
            for (int l = 0; l < F; l++) {
                //Check the frequency boundary
                if (k + l >= d) {
                    //Set to 0 the frequencies outside the boundary
                    block[k][l] = 0;
                }
            }
        }

        //Calculate IDCT2
        dct.inverse(block, true);

        // Shift into bitmap domain([0, 255])
        shiftBlockBy255(block);

        for (int k = 0; k < F; k++) {
            System.arraycopy(block[k], 0, signal[i + k], j, F);
        }
    }

    /**
     * Clamps all values in a 2D integer array to the range {@code [0, 255]}.
     *
     * @param block the 2D integer array to clamp; modified in-place
     */
    public static void shiftBlockBy255(double[][] block) {
        for (int y = 0; y < block.length; y++) {
            for (int x = 0; x < block[0].length; x++) {
                //Clamp the array into [0, 255] domain and round to the nearest integer
                block[y][x] = Math.round(Math.max(0, Math.min(255, block[y][x])));
            }
        }
    }
}