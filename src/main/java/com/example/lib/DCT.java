package com.example.lib;

import org.ejml.simple.SimpleMatrix;

import static org.apache.commons.math3.util.FastMath.*;

/**
 * Implements the Discrete Cosine Transform (DCT) and its inverse (IDCT) for 2D signals.
 * <p>
 * This class provides methods to transform 2D signals between spatial and frequency domains using
 * the DCT-II variant. The transformation is performed by computing DCT basis matrices and applying
 * them as matrix multiplications. This is useful for signal compression, as high-frequency
 * components can be selectively zeroed to reduce data without significant perceptual loss.
 * </p>
 */
public class DCT {
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
     * @param signal the input 2D signal as a SimpleMatrix
     * @return a new SimpleMatrix containing the DCT coefficients; the matrix has the same
     *         dimensions as the input signal
     */
    public SimpleMatrix DCT2(SimpleMatrix signal) {

            int n = signal.getNumRows();
            int m = signal.getNumCols();

            SimpleMatrix Dn = computeDMatrix(n);
            SimpleMatrix Dm = computeDMatrix(m);

            SimpleMatrix temp = Dn.mult(signal);

            return temp.mult(Dm.transpose());
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
     * @param signal the input signal in the frequency domain (typically the result of {@link #DCT2})
     * @return a new SimpleMatrix containing the reconstructed spatial-domain signal; the matrix has the same
     *         dimensions as the input signal
     */
    public SimpleMatrix IDCT2(SimpleMatrix signal) {
        int n = signal.getNumRows();
        int m = signal.getNumCols();

        SimpleMatrix Dn = computeDMatrix(n);
        SimpleMatrix Dm = computeDMatrix(m);

        return Dn.transpose().mult(signal).mult(Dm);
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
     * This matrix is then used in matrix multiplications to perform the forward and inverse transforms.
     * </p>
     *
     * @param size the dimension of the square basis matrix to compute
     * @return a {@link SimpleMatrix} of size {@code size x size} containing the DCT basis functions
     */
    private SimpleMatrix computeDMatrix(int size) {

        double[][] D = new double[size][size];

        for (int j = 0; j < size; j++) {
            D[0][j] = 1.0 / sqrt(size);
        }

        for (int i = 1; i < size; i++) {
            for (int j = 0; j < size; j++) {
                D[i][j] = sqrt(2.0 / size) *
                        cos(PI * (j + 0.5) * i / size);
            }
        }

        return new SimpleMatrix(D);
    }
}