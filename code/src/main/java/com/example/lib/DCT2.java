package com.example.lib;

import org.ejml.simple.SimpleMatrix;

import static org.apache.commons.math3.util.FastMath.*;

/**
 * Utility class that performs a 2D Discrete Cosine Transform (DCT-II) and its inverse.
 *
 * <p>The implementation uses the separability of the 2D DCT:
 * it first transforms one dimension, then the other, through matrix multiplications.
 *
 * <p>Given an input matrix {@code X}, the forward transform is:
 * {@code Y = Dn * X * Dm^T} (implemented through row/column passes).
 *
 * <p>The inverse is computed using transposed basis matrices, leveraging orthonormality.
 */
public class DCT2 {
    // =========================================================
    // DCT 2D
    // =========================================================

    /**
     * Computes the forward 2D DCT-II of the input matrix.
     *
     * @param signal input signal/image matrix
     */
    public void forward(SimpleMatrix signal) {
        // Build an DCT basis for rows (N) and columns (M).
        SimpleMatrix Dn = computeDMatrix(signal.getNumRows());
        SimpleMatrix Dm = computeDMatrix(signal.getNumCols());

        // Forward 2D DCT:
        DCTII(signal, Dn, Dm);
    }

    // =========================================================
    // IDCT 2D
    // =========================================================

    public void inverse(SimpleMatrix signal) {
        // Build same orthonormal bases.
        SimpleMatrix Dn = computeDMatrix(signal.getNumRows());
        SimpleMatrix Dm = computeDMatrix(signal.getNumCols());

        // Inverse uses transposed bases (orthonormal transform).
        DCTII(signal, Dn.transpose(), Dm.transpose());
    }
    /**
     * Applies a separable 2D transform using the provided basis matrices.
     *
     * <p>Pass 1: transform each column with {@code Dm}.<br>
     * Pass 2: transform each row with {@code Dn}.
     *
     * @param signal input matrix
     * @param Dn basis matrix used on rows
     * @param Dm basis matrix used on columns
     */
    private void DCTII(SimpleMatrix signal, SimpleMatrix Dn, SimpleMatrix Dm) {
        // Work on a copy to avoid mutating input data.;

        // Pass 1 (column-wise):
        // for each column vector x, compute Dm * x.
        for (int i = 0; i < signal.getNumCols(); i++) {
            SimpleMatrix col = signal.extractVector(false, i);
            SimpleMatrix transformed = Dm.mult(col);
            signal.insertIntoThis(0, i, transformed);
        }

        // Pass 2 (row-wise):
        // for each row vector r, compute (Dn * r^T)^T.
        for (int i = 0; i < signal.getNumRows(); i++) {
            SimpleMatrix row = signal.extractVector(true, i);
            SimpleMatrix transformed = Dn.mult(row.transpose()).transpose();
            signal.insertIntoThis(i, 0, transformed);
        }
    }
    // =========================================================
    // D MATRIX
    // =========================================================

    /**
     * Builds the orthonormal DCT-II basis matrix of size {@code size x size}.
     *
     * <p>Row 0 corresponds to the DC component (constant basis function).
     * Remaining rows correspond to higher-frequency cosine basis functions.
     *
     * @param size transform size
     * @return DCT-II basis matrix
     */
    private SimpleMatrix computeDMatrix(int size) {
        double[][] D = new double[size][size];

        // First row (k = 0): constant normalized component.
        for (int j = 0; j < size; j++) {
            D[0][j] = 1.0 / sqrt(size);
        }

        // Rows k = 1..size-1: normalized cosine basis vectors.
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < size; j++) {
                D[i][j] = sqrt(2.0 / size) *
                        cos(PI * (j + 0.5) * i / size);
            }
        }

        return new SimpleMatrix(D);
    }
}