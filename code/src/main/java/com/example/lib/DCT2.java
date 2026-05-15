package com.example.lib;

import org.ejml.simple.SimpleMatrix;

import static org.apache.commons.math3.util.FastMath.*;

/**
 * Implements the Discrete Cosine Transform (DCT) and its inverse (IDCT) for 2D signals.
 * <p>
 * This class provides methods to transform 2D signals between spatial and frequency domains using
 * the DCT-II variant. The transformation is performed by computing DCT basis matrices and applying
 * them as matrix multiplications. This approach is useful for signal compression, since high-frequency
 * components can be selectively zeroed to reduce data without significant perceptual loss.
 * </p>
 * <p>
 * Implementation notes:
 * <ul>
 *   <li>The forward DCT-II for a 2D signal X is computed as: D * X * D^T (where D is the DCT basis matrix).</li>
 *   <li>The inverse (IDCT) is computed as: D^T * X * D.</li>
 *   <li>This class builds the DCT basis matrices using the orthonormal DCT-II formulation:
 *       the first row is constant (1/sqrt(N)) and subsequent rows are scaled cosine functions.</li>
 *   <li>The transformations operate on EJML {@link SimpleMatrix} objects.</li>
 * </ul>
 * </p>
 * <p>
 * Performance / correctness hints:
 * <ul>
 *   <li>Computing the D matrix is O(N^2). For repeated transforms of the same size, cache the D matrix externally.</li>
 *   <li>The current implementation transforms columns then rows (in-place via extraction & insertion).
 *       While correct, it may not be the most efficient for very large matrices — consider full matrix multiplies
 *       or specialized DCT libraries (e.g. jtransforms) for performance-critical code.</li>
 *   <li>All calculations use double precision; numerical roundoff is possible for very large sizes.</li>
 * </ul>
 * </p>
 *
 * Example usage:
 * <pre>
 *     DCT2 dct2 = new DCT2();
 *     SimpleMatrix spatial = new SimpleMatrix(new double[][]{{ ... }}); // rows x cols
 *     SimpleMatrix freq = dct2.DCT2(spatial);   // forward transform
 *     SimpleMatrix recon = dct2.IDCT2(freq);    // inverse transform (reconstruction)
 * </pre>
 *
 * @see org.ejml.simple.SimpleMatrix
 */
public class DCT2 {
    // =========================================================
    // DCT 2D
    // =========================================================

    /**
     * Computes the forward 2D Discrete Cosine Transform of the input signal.
     * <p>
     * The transform is applied as: {@code result = Dn * signal * Dm^T}, where {@code Dn} and {@code Dm}
     * are DCT basis matrices computed for the row and column dimensions respectively.
     * </p>
     *
     * @param signal the input 2D signal as a {@link SimpleMatrix}; shape: rows x cols
     * @return a new {@link SimpleMatrix} containing the DCT coefficients; the matrix has the same
     *         dimensions as the input signal
     */
    public SimpleMatrix forward(SimpleMatrix signal) {
        // Compute DCT basis matrices for the row and column dimensions
        SimpleMatrix Dn = computeDMatrix(signal.getNumRows());
        SimpleMatrix Dm = computeDMatrix(signal.getNumCols());
        // Apply the forward DCT transformation
        return DCTII(signal, Dn, Dm);
    }

    // =========================================================
    // IDCT 2D
    // =========================================================

    /**
     * Computes the inverse 2D Discrete Cosine Transform of the input signal.
     * <p>
     * The inverse transform is applied as: {@code result = Dn^T * signal * Dm}, where {@code Dn} and {@code Dm}
     * are DCT basis matrices computed for the row and column dimensions respectively. This operation
     * reconstructs a spatial-domain signal from its frequency-domain DCT coefficients.
     * </p>
     *
     * @param signal the input signal in the frequency domain (typically the result of {@link #forward})
     * @return a new {@link SimpleMatrix} containing the reconstructed spatial-domain signal; the matrix has the same
     *         dimensions as the input signal
     */
    public SimpleMatrix inverse(SimpleMatrix signal) {
        // Compute DCT basis matrices for the row and column dimensions
        SimpleMatrix Dn = computeDMatrix(signal.getNumRows());
        SimpleMatrix Dm = computeDMatrix(signal.getNumCols());
        // Apply the inverse DCT transformation
        // (using transposed basis matrices to perform the inverse operation)
        return DCTII(signal, Dn.transpose(), Dm.transpose());
    }

    /**
     * Core transform helper that applies separable transformations to the input signal.
     * <p>
     * Given matrices Dn and Dm, this method applies the transform in two passes:
     * <ol>
     *   <li>For each column: replace column c with (Dm * column_c)</li>
     *   <li>For each row: replace row r with (Dn * (row_r)^T)^T</li>
     * </ol>
     * The combination of these two passes produces the effect of: result = Dn * signal * Dm^T when
     * Dn and Dm were constructed as forward D matrices. If Dn and Dm are transposed versions,
     * the net effect corresponds to the inverse transform.
     * </p>
     *
     * Implementation notes:
     * <ul>
     *   <li>The method performs operations by extracting column/row vectors and reinserting transformed vectors.
     *       This avoids allocating many full intermediate matrices, but still does many vector operations.</li>
     *   <li>All insertions use {@link SimpleMatrix#insertIntoThis(int, int, SimpleMatrix)} which mutates
     *       the working matrix copy. The input {@code signal} is copied at the start to avoid mutating the caller's matrix.</li>
     * </ul>
     *
     * @param signal the input matrix to transform (will not be modified directly; a copy is used)
     * @param Dn     transform matrix to apply to rows (or its transpose depending on forward/inverse choice)
     * @param Dm     transform matrix to apply to columns (or its transpose depending on forward/inverse choice)
     * @return the transformed matrix (same dimensions as {@code signal})
     */
    private SimpleMatrix DCTII(SimpleMatrix signal, SimpleMatrix Dn, SimpleMatrix Dm){
        // Create a copy of the input signal to avoid modifying the caller's matrix
        SimpleMatrix result = signal.copy();

        // First pass: Apply column-wise transformation (multiply each column by Dm)
        // This corresponds to transforming along the column dimension
        for (int i = 0; i < result.getNumCols(); i++) {
            // Extract the i-th column as a vector
            SimpleMatrix col = result.extractVector(false, i);
            // Apply the transform: Dm * column
            SimpleMatrix transformed = Dm.mult(col);
            // Insert the transformed column back into the result matrix
            result.insertIntoThis(0, i, transformed);
        }

        // Second pass: Apply row-wise transformation (multiply each row by Dn)
        // This corresponds to transforming along the row dimension
        // After this pass, the overall effect is Dn * signal * Dm^T (or inverse variants)
        for (int i = 0; i < result.getNumRows(); i++) {
            // Extract the i-th row as a vector
            SimpleMatrix row = result.extractVector(true, i);
            // Apply the transform: (Dn * (row)^T)^T = row * Dn^T
            SimpleMatrix transformed = Dn.mult(row.transpose()).transpose();
            // Insert the transformed row back into the result matrix
            result.insertIntoThis(i, 0, transformed);
        }

        return result;
    }

    // =========================================================
    // D MATRIX
    // =========================================================

    /**
     * Computes the DCT-II basis matrix for the specified size.
     * <p>
     * The basis matrix is constructed as follows:
     * </p>
     * <ul>
     *   <li>The first row contains the constant basis: {@code D[0][j] = 1 / sqrt(size)}</li>
     *   <li>Subsequent rows contain cosine basis functions:
     *       {@code D[i][j] = sqrt(2 / size) * cos(π * (j + 0.5) * i / size)} for {@code i >= 1}</li>
     * </ul>
     * <p>
     * This matrix is orthonormal (with the chosen scaling) and is used in matrix multiplications to perform
     * the forward and inverse transforms.
     * </p>
     *
     * @param size the dimension of the square basis matrix to compute
     * @return a {@link SimpleMatrix} of size {@code size x size} containing the DCT basis functions
     */
    private SimpleMatrix computeDMatrix(int size) {
        // Initialize a size x size matrix to hold the DCT basis
        double[][] D = new double[size][size];

        // First row is constant basis: D[0][j] = 1 / sqrt(size)
        // This corresponds to the DC (zero frequency) component
        for (int j = 0; j < size; j++) {
            D[0][j] = 1.0 / sqrt(size);
        }

        // Remaining rows follow the DCT-II basis formula
        // D[i][j] = sqrt(2/size) * cos(π * (j + 0.5) * i / size) for i >= 1
        // Each row represents a different frequency component (AC components)
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < size; j++) {
                D[i][j] = sqrt(2.0 / size) *
                        cos(PI * (j + 0.5) * i / size);
            }
        }

        return new SimpleMatrix(D);
    }
}