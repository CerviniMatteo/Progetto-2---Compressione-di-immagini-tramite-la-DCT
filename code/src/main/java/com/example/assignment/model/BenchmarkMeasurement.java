package com.example.assignment.model;

import org.openjdk.jmh.util.Statistics;

/**
 * Immutable benchmark sample for a single matrix size.
 *
 * <p>Each instance stores:</p>
 * <ul>
 *   <li>the matrix {@code size} used for the benchmark run,</li>
 *   <li>the execution time of the custom implementation ({@code customStatistics}), and</li>
 *   <li>the execution time of the library implementation ({@code libraryStatistics}).</li>
 * </ul>
 *
 * <p>This type is implemented as a Java {@code record}, so all components are final
 * and accessors are generated automatically.</p>
 *
 * <p><strong>Ratio semantics:</strong> {@link #ratioOnMean()} returns a pure ratio
 * {@code libraryMean / customMean}. A value less than 1.0 means the library is faster;
 * greater than 1.0 means the library is slower.</p>
 *
 * @param size              matrix dimension used in the measurement
 * @param customStatistics  execution-time statistics for the custom implementation (microseconds)
 * @param libraryStatistics execution-time statistics for the library implementation (microseconds)
 */
public record BenchmarkMeasurement(int size, Statistics customStatistics, Statistics libraryStatistics) {

	private static final double MICROSECONDS_TO_SECONDS = 1_000_000.0d;

	// ── Mean ─────────────────────────────────────────────────────────────────

	/** @return mean execution time of the custom implementation in seconds, or {@link Double#NaN} if unavailable */
	public double customMeanSeconds() {
		return toSeconds(customStatistics == null ? Double.NaN : customStatistics.getMean());
	}

	/** @return mean execution time of the library implementation in seconds, or {@link Double#NaN} if unavailable */
	public double libraryMeanSeconds() {
		return toSeconds(libraryStatistics == null ? Double.NaN : libraryStatistics.getMean());
	}

	// ── Min / Max ─────────────────────────────────────────────────────────────

	/** @return minimum execution time of the custom implementation in seconds, or {@link Double#NaN} if unavailable */
	public double customMinSeconds() {
		return toSeconds(customStatistics == null ? Double.NaN : customStatistics.getMin());
	}

	/** @return minimum execution time of the library implementation in seconds, or {@link Double#NaN} if unavailable */
	public double libraryMinSeconds() {
		return toSeconds(libraryStatistics == null ? Double.NaN : libraryStatistics.getMin());
	}

	/** @return maximum execution time of the custom implementation in seconds, or {@link Double#NaN} if unavailable */
	public double customMaxSeconds() {
		return toSeconds(customStatistics == null ? Double.NaN : customStatistics.getMax());
	}

	/** @return maximum execution time of the library implementation in seconds, or {@link Double#NaN} if unavailable */
	public double libraryMaxSeconds() {
		return toSeconds(libraryStatistics == null ? Double.NaN : libraryStatistics.getMax());
	}

	// ── Sum / N ───────────────────────────────────────────────────────────────

	/** @return sum of execution times of the custom implementation in seconds, or {@link Double#NaN} if unavailable */
	public double customSumSeconds() {
		return toSeconds(customStatistics == null ? Double.NaN : customStatistics.getSum());
	}

	/** @return sum of execution times of the library implementation in seconds, or {@link Double#NaN} if unavailable */
	public double librarySumSeconds() {
		return toSeconds(libraryStatistics == null ? Double.NaN : libraryStatistics.getSum());
	}

	/** @return number of samples for the custom implementation, or 0 if unavailable */
	public long customN() {
		return customStatistics == null ? 0L : customStatistics.getN();
	}

	/** @return number of samples for the library implementation, or 0 if unavailable */
	public long libraryN() {
		return libraryStatistics == null ? 0L : libraryStatistics.getN();
	}

	// ── Ratio ─────────────────────────────────────────────────────────────────

	/**
	 * Returns the pure speed ratio {@code libraryMean / customMean}.
	 *
	 * <ul>
	 *   <li>&lt; 1.0 → library is faster than custom</li>
	 *   <li>&gt; 1.0 → library is slower than custom</li>
	 * </ul>
	 *
	 * @return {@code libraryMean / customMean}, or {@link Double#NaN} if either value is unavailable
	 */
	public double ratioOnMean() {
		double customMean  = customMeanSeconds();
		double libraryMean = libraryMeanSeconds();
		if (customMean <= 0 || Double.isNaN(customMean) || Double.isNaN(libraryMean)) {
			return Double.NaN;
		}
		return libraryMean / customMean;
	}

	// ── Helpers ───────────────────────────────────────────────────────────────

	private static double toSeconds(double microseconds) {
		return Double.isNaN(microseconds) ? Double.NaN : microseconds / MICROSECONDS_TO_SECONDS;
	}
}