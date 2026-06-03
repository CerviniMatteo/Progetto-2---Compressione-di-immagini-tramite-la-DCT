    package com.unimib.assignment.backend.lib;

    import org.ejml.simple.SimpleMatrix;

    import static org.apache.commons.math3.util.FastMath.*;

    /**
     * Implements the Discrete Cosine Transform (DCT) and its inverse (IDCT).
     * <p>
     * This class provides methods to transform 2D matrices between spatial and frequency domains using
     * the DCT-II variant.
     * </p>
     * @see org.ejml.simple.SimpleMatrix
     */
    public class DCT2 {
        // =========================================================
        // DCT 2D
        // =========================================================
        /**
         * Computes the forward 2D Discrete Cosine Transform of the input signal.
         *
         * @param signal the input 2D signal as a {@link SimpleMatrix}; shape: rows x cols
         * @return a new {@link SimpleMatrix} containing the DCT coefficients;
         * the matrix has the same dimensions as the input signal
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
         * <p>Given matrices Dn and Dm, this method applies the transform in two passes:</p>
         * <ol>
         *   <li>For each column: replace column c with (Dn * column_c)</li>
         *   <li>For each row: replace row r with (Dm * (row_r)^T)^T</li>
         * </ol>
         *
         * @param signal the input matrix to transform (will not be modified directly; a copy is used)
         * @param Dn     transform matrix to apply to columns (or its transpose depending on forward/inverse choice)
         * @param Dm     transform matrix to apply to rows (or its transpose depending on forward/inverse choice)
         * @return the transformed matrix (same dimensions as {@code signal})
         */
        private SimpleMatrix DCTII(SimpleMatrix signal, SimpleMatrix Dn, SimpleMatrix Dm){
            // Create a copy of the input signal to avoid modifying the caller's matrix
            SimpleMatrix result = signal.copy();

            // First pass: Apply column-wise transformation (multiply each column by Dn)
            // This corresponds to transforming along the column dimension
            for (int i = 0; i < result.getNumCols(); i++) {
                // Extract the i-th column as a vector(false flag is used to extract column)
                SimpleMatrix col = result.extractVector(false, i);
                // Apply the transform: Dn * column
                SimpleMatrix transformed = Dn.mult(col);
                // Insert the transformed column back into the result matrix
                result.insertIntoThis(0, i, transformed);
            }

            // Second pass: Apply row-wise transformation (multiply each row by Dm)
            // This corresponds to transforming along the row dimension
            for (int i = 0; i < result.getNumRows(); i++) {
                // Extract the i-th row as a vector(true flag is used to extract row)
                SimpleMatrix row = result.extractVector(true, i);
                // Apply the transform: (Dm * (row)^T)^T
                SimpleMatrix transformed = Dm.mult(row.transpose()).transpose();
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
         * <p>The basis matrix is constructed as follows:</p>
         * <ul>
         *   <li>The first row contains: {@code D[0][j] = 1 / sqrt(size)}</li>
         *   <li>Subsequent rows contain cosine basis functions:
         *       {@code D[i][j] = sqrt(2 / size) * cos(pi * (j + 0.5) * i / size)} for {@code i >= 1}</li>
         * </ul>
         * <p>
         * This matrix is orthonormal (with the chosen scaling) and is used to perform
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