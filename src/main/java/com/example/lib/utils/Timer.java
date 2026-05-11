package com.example.lib.utils;

/**
 * Utility class for measuring and benchmarking operation execution times.
 * <p>
 * This class provides methods to run operations multiple times and calculate the average
 * execution time. It uses {@link System#nanoTime()} for high-precision timing and converts
 * results to seconds for easier interpretation.
 * </p>
 */
public class Timer {

    /**
     * Functional interface for a callable operation that returns no value.
     * <p>
     * This interface is used with {@link Timer#measure} to benchmark code blocks
     * without return values.
     * </p>
     *
     * @param <T> the return type of the operation (typically {@code Void} for no-op implementations)
     */
    @FunctionalInterface
    public interface Operation<T> {
        /**
         * Executes the operation and returns a value.
         *
         * @return the result of the operation, or {@code null} if no value is produced
         */
        T run();
    }

    /**
     * Runs the given operation multiple times and returns the average elapsed time.
     * <p>
     * The operation is executed the specified number of times, and the average execution
     * time is calculated. High-precision timing using {@link System#nanoTime()} is converted
     * to seconds for the result.
     * </p>
     *
     * @param r the {@link Runnable} operation to execute and measure
     * @param repetitions the number of times to execute the operation; should be at least 1
     * @return the average execution time in seconds
     */
    public static double measure(Runnable r, int repetitions) {
        double total = 0;

        for (int i = 0; i < repetitions; i++) {
            double start = System.nanoTime() / 1_000_000_000.0;

            r.run();

            double end = System.nanoTime()  / 1_000_000_000.0;
            total += (end - start);
        }

        return total / repetitions;
    }
}