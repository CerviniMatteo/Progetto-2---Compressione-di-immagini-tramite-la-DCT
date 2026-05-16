package com.example.assignment.model;

/**
 * Immutable benchmark sample for a single matrix size.
 *
 * <p>Each instance stores:</p>
 * <ul>
 *   <li>the matrix {@code size} used for the benchmark run,</li>
 *   <li>the execution time of the custom implementation ({@code customTime}), and</li>
 *   <li>the execution time of the library implementation ({@code libraryTime}).</li>
 * </ul>
 *
 * <p>This type is implemented as a Java {@code record}, so all components are final
 * and accessors are generated automatically.</p>
 *
 * @param size matrix dimension used in the measurement
 * @param customTime execution time measured for the custom implementation
 * @param libraryTime execution time measured for the library implementation
 */
public record BenchmarkMeasurement(int size, double customTime, double libraryTime) {
}