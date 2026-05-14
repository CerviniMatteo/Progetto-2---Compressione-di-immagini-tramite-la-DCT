package com.example.lib.utils;

import static com.example.lib.utils.UtilsConstants.TIMED_RESULT_FORMAT;

/**
 * Utility class for measuring and benchmarking operation execution times.
 * <p>
 * This class provides methods to run operations multiple times and calculate the average
 * execution time. It uses {@link System#nanoTime()} for high-precision timing and converts
 * results to seconds for easier interpretation.
 * </p>
 */
public class Timer {

    // ========================================================
    // CONSTANTS
    // ========================================================

    /** Conversion factor from nanoseconds to seconds. */
    private static final double NANOS_TO_SECONDS = 1_000_000_000.0;

    /** Default number of repetitions for benchmarking. */
    private static final int DEFAULT_REPETITIONS = 1;


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
     * Runs the given operation once and returns the elapsed time.
     * <p>
     * This is a convenience method equivalent to calling {@link #measure(Runnable, int)}
     * with repetitions = 1.
     * </p>
     *
     * @param r the {@link Runnable} operation to execute and measure
     * @return the execution time in seconds
     */
    public static double measure(Runnable r) {
        return measure(r, DEFAULT_REPETITIONS);
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
            double start = System.nanoTime() / NANOS_TO_SECONDS;
            r.run();
            double end = System.nanoTime() / NANOS_TO_SECONDS;
            total += (end - start);
        }

        return total / repetitions;
    }

    /**
     * Runs the given operation once and returns the result along with elapsed time.
     * <p>
     * This method combines result capture and timing into a single call.
     * </p>
     *
     * @param op the {@link Operation} to execute and measure
     * @param <T> the return type of the operation
     * @return a {@link TimedResult} containing both the operation result and elapsed time
     */
    public static <T> TimedResult<T> measureWithResult(Operation<T> op) {
        return measureWithResult(op, DEFAULT_REPETITIONS);
    }

    /**
     * Runs the given operation multiple times and returns the result with average elapsed time.
     * <p>
     * The operation is executed the specified number of times, and the average execution
     * time is calculated. The result returned is from the last execution.
     * </p>
     *
     * @param op the {@link Operation} to execute and measure
     * @param repetitions the number of times to execute the operation; should be at least 1
     * @param <T> the return type of the operation
     * @return a {@link TimedResult} containing the last result and average elapsed time
     */
    public static <T> TimedResult<T> measureWithResult(Operation<T> op, int repetitions) {
        T result = null;
        double total = 0;

        for (int i = 0; i < repetitions; i++) {
            double start = System.nanoTime() / NANOS_TO_SECONDS;
            result = op.run();
            double end = System.nanoTime() / NANOS_TO_SECONDS;
            total += (end - start);
        }

        double avgTime = total / repetitions;
        return new TimedResult<>(result, avgTime);
    }

    // ========================================================
    // RESULT CONTAINER
    // ========================================================

    /**
     * Container for a result with associated measurement time.
     *
     * @param <T> the type of the result value
     */
    public static class TimedResult<T> {
        /** The result of the operation. */
        private final T result;

        /** The elapsed time in seconds. */
        private final double timeSeconds;

        /**
         * Creates a timed result container.
         *
         * @param result the operation result
         * @param timeSeconds the elapsed time in seconds
         */
        public TimedResult(T result, double timeSeconds) {
            this.result = result;
            this.timeSeconds = timeSeconds;
        }

        /**
         * Returns the operation result.
         *
         * @return the result
         */
        public T getResult() {
            return result;
        }

        /**
         * Returns the elapsed time in seconds.
         *
         * @return elapsed time in seconds
         */
        public double getTimeSeconds() {
            return timeSeconds;
        }

        /**
         * Returns elapsed time in milliseconds.
         *
         * @return elapsed time in milliseconds
         */
        public double getTimeMillis() {
            return timeSeconds * 1000;
        }

        /**
         * Returns a formatted string representation.
         *
         * @return string with result and time
         */
        @Override
        public String toString() {
            return String.format(TIMED_RESULT_FORMAT, result, timeSeconds);
        }
    }
}