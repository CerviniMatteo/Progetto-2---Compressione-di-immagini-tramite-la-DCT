package com.example.assignment;

import com.example.lib.utils.UtilsConstants;
import org.apache.commons.math3.util.Pair;
import org.jtransforms.dct.DoubleDCT_2D;
import java.awt.image.BufferedImage;
import static com.example.lib.utils.ImageUtils.*;

/**
 * Implements block-based grayscale image compression using the 2D Discrete Cosine Transform (DCT).
 * <p>Compression strategy:</p>
 * <ul>
 *   <li>Split the image into non-overlapping {@code F x F} blocks</li>
 *   <li>Apply forward DCT to each block</li>
 *   <li>Zero high-frequency coefficients according to threshold {@code d}</li>
 *   <li>Apply inverse DCT and write reconstructed pixels back</li>
 * </ul>
 * The compressed image is saved as BMP in {@code OUTPUT_PATH}.
 */
public class Part2 {

    /**
     * Compresses an image using block DCT with frequency truncation.
     * <p>
     * Only the largest region whose dimensions are multiples of {@code F} is processed.
     * Any border pixels outside this region are left unchanged.
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
        BufferedImage image = imageInfo.getSecond();
        double[][] signal = convertImageToArray(image);

        int rows = signal.length - signal.length % F;
        int cols = signal[0].length - signal[0].length % F;

        for (int i = 0; i < rows; i += F) {
            for (int j = 0; j < cols; j += F) {
                compressBlock(signal, i, j, F, d);
            }
        }

        BufferedImage result = convertArrayToImage(signal);
        BufferedImage cropped = result.getSubimage(0, 0, result.getWidth() - result.getWidth() % F, result.getHeight() - result.getHeight() % F);
        saveAsBMP(cropped, UtilsConstants.OUTPUT_PATH + imageInfo.getFirst());
        return cropped;
    }

    /**
     * Compresses a single {@code F x F} block of the image signal in place.
     * <p>Steps:</p>
     * <ol>
     *   <li>Copy block from {@code signal} to a temporary matrix</li>
     *   <li>Convert to {@code double[][]} and apply forward DCT</li>
     *   <li>Discard high-frequency components using diagonal rule {@code k + l >= d}</li>
     *   <li>Apply inverse DCT</li>
     *   <li>Round to integers, shift values by 255 (project-specific normalization), and copy back</li>
     * </ol>
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

        for (int k = 0; k < F; k++) {
            System.arraycopy(signal[i + k], j, block[k], 0, F);
        }

        dct.forward(block, true);

        for (int k = 0; k < F; k++) {
            for (int l = 0; l < F; l++) {
                if (k + l >= d) {
                    block[k][l] = 0;
                }
            }
        }

        dct.inverse(block, true);

        shiftBlockBy255(block);

        for (int k = 0; k < F; k++) {
            System.arraycopy(block[k], 0, signal[i + k], j, F);
        }
    }

    /**
     * Clamps all values in a 2D integer array to the range {@code [0, 255]}.
     * <p>
     * This method is typically used to ensure pixel values remain within the valid
     * byte range after image processing operations.
     * </p>
     *
     * @param block the 2D integer array to clamp; modified in-place
     */
    public static void shiftBlockBy255(double[][] block) {
        for (int y = 0; y < block.length; y++) {
            for (int x = 0; x < block[0].length; x++) {
                block[y][x] = Math.round(Math.max(0, Math.min(255, block[y][x])));
            }
        }
    }
}