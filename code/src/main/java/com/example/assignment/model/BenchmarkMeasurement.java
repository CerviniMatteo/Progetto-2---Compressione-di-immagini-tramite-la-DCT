package com.example.assignment.model;

public record BenchmarkMeasurement(int size, double meanTimeMine, double meanTimeLib) {

	private static final double MICROSECONDS_TO_SECONDS = 1_000_000.0d;

	// ── Mean ─────────────────────────────────────────────────────────────────

	/** @return mean execution time of the custom implementation in seconds, or {@link Double#NaN} if unavailable */
	public double customMeanSeconds() {
		return toSeconds(meanTimeMine);
	}

	/** @return mean execution time of the library implementation in seconds, or {@link Double#NaN} if unavailable */
	public double libraryMeanSeconds() {
		return toSeconds(meanTimeLib);
	}

	// ── Helpers ───────────────────────────────────────────────────────────────

	private static double toSeconds(double microseconds) {
		return Double.isNaN(microseconds) ? Double.NaN : microseconds / MICROSECONDS_TO_SECONDS;
	}
}