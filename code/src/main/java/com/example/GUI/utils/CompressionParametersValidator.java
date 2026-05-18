package com.example.GUI.utils;

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
     * <p>
     * <strong>Calculation example:</strong><br>
     * Valid F values: 2, 4, 8, 16, 32, etc. (all integers > 1)<br>
     * Invalid F values: 0, 1, -5 (must satisfy F > 1)
     * </p>
     *
     * @param F compression factor
     *
     * @throws IllegalArgumentException if F <= 1 with details about the requirement
     */
    private static void validateCompressionFactor(int F) {
        if (F <= MIN_COMPRESSION_FACTOR) {
            throw new IllegalArgumentException(
                String.format(
                    F_VALIDATION_ERROR_FORMAT,
                    F_GREATER_THAN_ONE,
                    F,
                    MIN_COMPRESSION_FACTOR,
                    F,
                    MIN_COMPRESSION_FACTOR
                )
            );
        }
    }

    /**
     * Validates that the truncation parameter d is within the valid range for the given F.
     * <p>
     * The truncation parameter must satisfy: 0 <= d <= 2*F - 2
     * This ensures that the frequency cutoff is within the valid DCT coefficient range.
     * </p>
     * <p>
     * <strong>Calculation example:</strong><br>
     * If F=8, then valid range for d is: 0 <= d <= 2*8-2 = 14<br>
     * If d=16, validation fails because: 16 > 14 (exceeds max coefficient count)
     * </p>
     *
     * @param d truncation parameter
     * @param F compression factor (used to calculate valid range)
     *
     * @throws IllegalArgumentException if d is outside the valid range with calculation details
     */
    private static void validateTruncationParameter(int d, int F) {
        if (d < MIN_TRUNCATION_PARAMETER) {
            throw new IllegalArgumentException(
                String.format(
                    D_NEGATIVE_ERROR_FORMAT,
                    D_VALUE_ERROR,
                    d,
                    MIN_TRUNCATION_PARAMETER
                )
            );
        }
        int maxD = (D_RANGE_MULTIPLIER * F) - D_RANGE_OFFSET;
        if (d > maxD) {
            throw new IllegalArgumentException(
                String.format(
                    D_EXCEEDS_MAX_ERROR_FORMAT,
                    D_VALUE_ERROR,
                    d,
                    D_RANGE_MULTIPLIER,
                    D_RANGE_OFFSET,
                    D_RANGE_MULTIPLIER,
                    F,
                    D_RANGE_OFFSET,
                    maxD
                )
            );
        }
    }


    /**
     * Validates that parameters F and d are not larger than image dimensions.
     * <p>
     * Both the compression factor and truncation parameter must be less than
     * the image's row and column dimensions to ensure valid block processing.
     * </p>
     * <p>
     * <strong>Calculation example:</strong><br>
     * If image is 512x512 and F=8:<br>
     * Check: rows >= F → 512 >= 8 ✓ and cols >= F → 512 >= 8 ✓ (Valid)<br>
     * If image is 256x100 and F=8:<br>
     * Check: rows >= F → 256 >= 8 ✓ and cols >= F → 100 >= 8 ✓ (Valid)<br>
     * If image is 64x4 and F=8:<br>
     * Check: cols >= F → 4 >= 8 ✗ (Invalid - block size exceeds image width)
     * </p>
     *
     * @param F compression factor
     * @param rows image height
     * @param cols image width
     *
     * @throws IllegalArgumentException if F or d exceeds image dimensions with calculations
     */
    private static void validateImageDimensions(int F, int rows, int cols) {
        if (rows < F) {
            throw new IllegalArgumentException(
                String.format(
                    F_HEIGHT_ERROR_FORMAT,
                    F_ROWS_COLS_ERROR,
                    rows,
                    F
                )
            );
        }
        if (cols < F) {
            throw new IllegalArgumentException(
                String.format(
                    F_WIDTH_ERROR_FORMAT,
                    F_ROWS_COLS_ERROR,
                    cols,
                    F
                )
            );
        }
    }

}

