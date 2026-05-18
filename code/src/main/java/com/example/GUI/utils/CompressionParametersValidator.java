package com.example.GUI.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.example.GUI.constants.PickerConstants.*;

/**
 * Utility class for validating compression parameters.
 * <p>
 * This class provides business logic validation for DCT compression parameters,
 * ensuring that:
 * <ul>
 *   <li>Compression factor F is valid and non-negative</li>
 *   <li>Truncation parameter d is within valid range relative to F</li>
 *   <li>Parameters are valid relative to image dimensions</li>
 * </ul>
 * </p>
 */
public class CompressionParametersValidator {

    private static final Log log = LogFactory.getLog(CompressionParametersValidator.class);

    /**
     * Validates the input parameters against compression business rules.
     * <p>
     * Validation checks:
     * <ul>
     *   <li>F must be non-negative (F >= 0)</li>
     *   <li>d must satisfy: 0 <= d <= 2*F - 2</li>
     *   <li>Both F and d must be less than or equal to image dimensions (rows and cols)</li>
     * </ul>
     * </p>
     *
     * @param F compression factor (block size)
     * @param d truncation parameter (frequency cutoff)
     * @param rows image height in pixels
     * @param cols image width in pixels
     *
     * @throws IllegalArgumentException if any validation rule is violated
     *
     * @see #validateCompressionFactor(int)
     * @see #validateTruncationParameter(int, int)
     * @see #validateImageDimensions(int, int, int)
     */
    public static void validateInputs(int F, int d, int rows, int cols) {
        validateCompressionFactor(F);
        validateTruncationParameter(d, F);
        validateImageDimensions(F, rows, cols);
    }

    /**
     * Validates that the compression factor F is non-negative or less than 1.
     * <p>
     * The compression factor represents the block size for DCT transformation.
     * It must be a positive integer.
     * </p>
     *
     * @param F compression factor
     *
     * @throws IllegalArgumentException if F <= 1
     */
    private static void validateCompressionFactor(int F) {
        if (F <= 1) {
            throw new IllegalArgumentException(F_GREATER_THAN_ONE);
        }
    }

    /**
     * Validates that the truncation parameter d is within the valid range for the given F.
     * <p>
     * The truncation parameter must satisfy: 0 <= d <= 2*F - 2
     * This ensures that the frequency cutoff is within the valid DCT coefficient range.
     * </p>
     *
     * @param d truncation parameter
     * @param F compression factor (used to calculate valid range)
     *
     * @throws IllegalArgumentException if d is outside the valid range
     */
    private static void validateTruncationParameter(int d, int F) {
        if (d < 0 || d > (2 * F) - 2) {
            throw new IllegalArgumentException(D_VALUE_ERROR);
        }
    }


    /**
     * Validates that parameters F and d are not larger than image dimensions.
     * <p>
     * Both the compression factor and truncation parameter must be less than
     * the image's row and column dimensions to ensure valid block processing.
     * </p>
     *
     * @param F compression factor
     * @param rows image height
     * @param cols image width
     *
     * @throws IllegalArgumentException if F or d exceeds image dimensions
     */
    private static void validateImageDimensions(int F, int rows, int cols) {
        if (rows < F || cols < F) {
            throw new IllegalArgumentException(F_ROWS_COLS_ERROR);
        }
    }

}

