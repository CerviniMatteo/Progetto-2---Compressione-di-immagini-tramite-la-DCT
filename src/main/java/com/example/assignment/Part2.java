package com.example.assignment;

import com.example.UI.IntegersPicker;
import com.example.lib.utils.ArrayUtils;
import org.apache.commons.math3.util.Pair;
import org.jtransforms.dct.DoubleDCT_2D;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.example.lib.constants.ProjectHelperConstants.*;
import static com.example.lib.constants.UtilsConstants.*;
import static com.example.lib.utils.ArrayUtils.toDoubleArray;
import static com.example.lib.utils.ImageUtils.*;

/**
 * Image compression pipeline using block-wise Discrete Cosine Transform (DCT).
 * <p>
 * This class applies lossy compression to images by decomposing them into blocks,
 * applying a forward DCT to each block, zeroing high-frequency coefficients, and then
 * applying an inverse DCT. The compression ratio is controlled by two user-configurable
 * parameters: the block size ({@code F}) and the frequency cutoff ({@code d}).
 * </p>
 */
public class Part2 {

    // =========================
    // ENCODE
    // =========================

    /**
     * Compresses an image using block-wise DCT with user-configurable parameters.
     * <p>
     * This method displays a UI for the user to select the block size ({@code F})
     * and frequency cutoff ({@code d}), then applies DCT-based compression to the
     * provided image and exports the result. Both the original and compressed images
     * are displayed side-by-side in separate windows.
     * </p>
     * <p>
     * The compression process:
     * </p>
     * <ol>
     *   <li>Converts the input image to a 2D integer array.</li>
     *   <li>Pads the image dimensions to be multiples of {@code F}.</li>
     *   <li>Iterates through blocks of size {@code F x F} and applies {@code compressBlock}.</li>
     *   <li>Converts the compressed array back to a BufferedImage.</li>
     *   <li>Saves the result as a BMP file to the {@code output/} directory.</li>
     *   <li>Displays both the original and compressed images on screen.</li>
     * </ol>
     *
     * @param imgPair a {@link Pair} containing the image filename (as the key) and the
     *        {@link BufferedImage} to compress (as the value)
     */
    public void compress(Pair<String, BufferedImage> imgPair) {

        System.out.println(COMPRESSING + imgPair.getFirst());

        IntegersPicker integersPicker = new IntegersPicker();

        integersPicker.subscribe(integerIntegerPair -> {
            System.out.println(WORKING_WITH + integerIntegerPair.getFirst() + COMPRESSION_FACTOR + integerIntegerPair.getSecond());
            int[][] signal = convertImageToArray(imgPair.getSecond());

            int F = integerIntegerPair.getFirst();
            int d = integerIntegerPair.getSecond();

            int rows = signal.length - signal.length % F;
            int cols = signal[0].length - signal[0].length % F;


            for (int i = 0; i < rows; i += F) {
                for (int j = 0; j < cols; j += F) {
                    compressBlock(signal, i, j, F, d);
                }
            }

            BufferedImage img = convertArrayToImage(signal);
            saveAsBMP(img, OUTPUT_PATH + imgPair.getFirst());
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            showImage(img, COMPRESSED_IMAGE, COMPRESSED_IMAGE_WINDOW_X, COMPRESSED_IMAGE_WINDOW_Y);
            showImage(imgPair.getSecond(),
                    ORIGINAL_IMAGE,
                    screenSize.width - imgPair.getSecond().getWidth(),
                    ORIGINAL_IMAGE_WINDOW_Y);
        });
        integersPicker.showUI();
    }

    // =========================
    // BLOCK PIPELINE
    // =========================

    /**
     * Compresses a single block of the signal using DCT with frequency coefficient removal.
     * <p>
     * This method:
     * </p>
     * <ol>
     *   <li>Extracts an {@code F x F} block from the signal at position {@code (i, j)}.</li>
     *   <li>Applies a forward DCT to convert spatial data to frequency domain.</li>
     *   <li>Zeros all coefficients where {@code k + l >= d} to remove high-frequency components.</li>
     *   <li>Applies an inverse DCT to reconstruct spatial domain data.</li>
     *   <li>Converts the result back to integer values and shifts to the valid range {@code [0, 255]}.</li>
     *   <li>Writes the compressed block back to the signal array in-place.</li>
     * </ol>
     * <p>
     * The frequency cutoff threshold ({@code d}) controls compression: smaller values result in
     * more aggressive compression by removing more high-frequency coefficients.
     * </p>
     *
     * @param signal a 2D array representing the image pixel data; modified in-place
     * @param i the starting row index of the block within the signal array
     * @param j the starting column index of the block within the signal array
     * @param F the size of the square block ({@code F x F}) to compress
     * @param d the frequency cutoff threshold; coefficients at positions where
     *        {@code k + l >= d} are zeroed
     */
    private static void compressBlock(int[][] signal, int i, int j, int F, int d) {
        double[][] block;
        int[][] integerBlock = new int[F][F];

        DoubleDCT_2D dct = new DoubleDCT_2D(F, F);

        for (int k = 0; k < F; k++) {
            System.arraycopy(signal[i + k], j, integerBlock[k], 0, F);
        }
        block = toDoubleArray(integerBlock);
        dct.forward(block, true);

        for (int k = 0; k < F; k++) {
            for (int l = 0; l < F; l++) {
                if (k + l >= d) {
                    block[k][l] = 0;
                }
            }
        }

        dct.inverse(block, true);

        int[][] roundedArray = ArrayUtils.toIntArray(block);

        ArrayUtils.shiftBlockBy255(roundedArray);

        for (int k = 0; k < F; k++) {
            System.arraycopy(roundedArray[k], 0, signal[i + k], j, F);
        }
    }
}