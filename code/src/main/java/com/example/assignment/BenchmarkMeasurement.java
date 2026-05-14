package com.example.assignment;

/**
 * Immutable benchmark sample for a single matrix size.
 */
public record BenchmarkMeasurement(int size, double customTime, double libraryTime) {
}

