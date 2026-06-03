package com.unimib.assignment.backend.model;

/**
 * A record representing a single benchmark measurement comparing the performance
 * of a custom DCT implementation against a library implementation.
 * <p>
 * This record encapsulates the execution time metrics for both implementations
 * at a specific input size, allowing for performance comparison and analysis.
 * <p>
 * The execution times are stored in microseconds and can be converted to seconds
 * using the convenience methods {@link #customMeanSeconds()} and {@link #libraryMeanSeconds()}.
 *
 * @param size          the input size for which the benchmark was executed
 * @param meanTimeMine  the mean execution time of the custom implementation in microseconds,
 *                      or {@link Double#NaN} if the measurement is unavailable
 * @param meanTimeLib   the mean execution time of the library implementation in microseconds,
 *                      or {@link Double#NaN} if the measurement is unavailable
 */
public record BenchmarkMeasurement(int size, double meanTimeMine, double meanTimeLib) {

	/** Conversion factor from microseconds to seconds. */
	private static final double MICROSECONDS_TO_SECONDS = 1_000_000.0d;

	/**
	 * Returns the mean execution time of the custom implementation in seconds.
	 * <p>
	 * If the measurement is unavailable, returns {@link Double#NaN}.
	 *
	 * @return the mean execution time in seconds, or {@link Double#NaN} if unavailable
	 */
	public double customMeanSeconds() {
		return toSeconds(meanTimeMine);
	}

	/**
	 * Returns the mean execution time of the library implementation in seconds.
	 * <p>
	 * If the measurement is unavailable, returns {@link Double#NaN}.
	 *
	 * @return the mean execution time in seconds, or {@link Double#NaN} if unavailable
	 */
	public double libraryMeanSeconds() {
		return toSeconds(meanTimeLib);
	}

	/**
	 * Converts a time value from microseconds to seconds.
	 * <p>
	 * Handles the special case where the input is {@link Double#NaN} by returning
	 * {@link Double#NaN} unchanged, indicating unavailable or invalid data.
	 *
	 * @param microseconds the time value in microseconds
	 * @return the equivalent time value in seconds, or {@link Double#NaN} if the input is NaN
	 */
	private static double toSeconds(double microseconds) {
		return Double.isNaN(microseconds) ? Double.NaN : microseconds / MICROSECONDS_TO_SECONDS;
	}
}