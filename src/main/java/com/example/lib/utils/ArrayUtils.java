package com.example.lib.utils;

import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;

/**
 * Utility methods for array conversions and operations on 2D arrays.
 * <p>
 * This class provides conversion facilities between different array types (SimpleMatrix, double arrays,
 * and integer arrays) as well as helper methods for array manipulation including deep copying,
 * value clamping, and tolerance-based comparison.
 * </p>
 */
public class ArrayUtils {

    /**
     * Converts an EJML {@link SimpleMatrix} to a 2D double array.
     * <p>
     * Each element is rounded to the nearest integer using {@link Math#round}.
     * </p>
     *
     * @param m the SimpleMatrix to convert
     * @return a 2D double array with the same dimensions as the input matrix
     */
    public static double[][] toDoubleArray(SimpleMatrix m) {

        int rows = m.getNumRows();
        int cols = m.getNumCols();

        double[][] out = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                out[i][j] = Math.round(m.get(i, j));
            }
        }

        return out;
    }

    /**
     * Converts a 2D double array to a 2D integer array.
     * <p>
     * Each element is rounded to the nearest integer using {@link Math#round} before casting to {@code int}.
     * </p>
     *
     * @param m the 2D double array to convert
     * @return a 2D integer array with the same dimensions as the input array
     */
    public static int[][] toIntArray(double[][] m) {

        int rows = m.length;
        int cols = m[0].length;

        int[][] out = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                out[i][j] = (int) Math.round(m[i][j]);
            }
        }

        return out;
    }

    /**
     * Converts a 2D integer array to a 2D double array.
     * <p>
     * Values are cast without modification.
     * </p>
     *
     * @param array the 2D integer array to convert
     * @return a 2D double array with the same dimensions and values as the input array
     */
    public static double[][] toDoubleArray(int[][] array) {
        double[][] convertedArray = new double[array.length][array[0].length];

        for (int i = 0; i < convertedArray.length; i++) {
            for (int j = 0; j < convertedArray[0].length; j++) {
                convertedArray[i][j] = array[i][j];
            }
        }

        return convertedArray;
    }

    /**
     * Creates a shallow-copied copy of a 2D double array.
     * <p>
     * Each row is independently copied, ensuring that modifications to the returned array
     * do not affect the original array (and vice versa).
     * </p>
     *
     * @param original the 2D double array to copy
     * @return a deep copy of the input array
     */
    public static double[][] deepCopy(double[][] original) {

        double[][] copy = new double[original.length][];

        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }

        return copy;
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
    public static void shiftBlockBy255(int[][] block) {
        for (int y = 0; y < block.length; y++) {
            for (int x = 0; x < block[0].length; x++) {
                block[y][x] = Math.max(0, Math.min(255, block[y][x]));
            }
        }
    }

    /**
     * Compares two 2D double arrays for equality within a given tolerance.
     * <p>
     * Two arrays are considered equal if the absolute difference between corresponding
     * elements is no greater than {@code eps}.
     * </p>
     *
     * @param a the first 2D double array
     * @param b the second 2D double array
     * @param eps the tolerance threshold for element-wise comparison
     * @return {@code true} if all corresponding elements differ by at most {@code eps};
     *         {@code false} otherwise
     */
    public static boolean equalsWithTolerance(
            double[][] a,
            double[][] b,
            double eps
    ) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {

                if (Math.abs(a[i][j] - b[i][j]) > eps) {
                    return false;
                }
            }
        }
        return true;
    }
}
