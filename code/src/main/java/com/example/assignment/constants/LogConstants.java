package com.example.assignment.constants;

public class LogConstants {
    /**
     * Log message printed when the benchmark starts.
     * The placeholder represents the number of matrix sizes being tested.
     */
    public static final String LOG_BENCHMARK_START = "DCT Benchmark started with {} sizes";
    /**
     * Log message printed before benchmarking a specific matrix size.
     * The two placeholders represent the matrix dimensions.
     */
    public static final String LOG_BENCHMARK_SIZE = "Benchmarking matrix size: {}x{}";
    /**
     * Log message printed before measuring the custom DCT implementation.
     */
    public static final String LOG_MEASURE_CUSTOM = "Measuring custom DCT implementation for N={}";
    /**
     * Log message printed before measuring the library DCT implementation.
     */
    public static final String LOG_MEASURE_LIBRARY = "Measuring library DCT implementation for N={}";
    /**
     * Log message used to print a single benchmark result row.
     * Placeholders represent:
     * <ul>
     *   <li>matrix size</li>
     *   <li>custom implementation time</li>
     *   <li>library implementation time</li>
     *   <li>ratio between library and custom time</li>
     * </ul>
     */
    public static final String LOG_RESULT_ROW = "N={} | MyDCT: {} s | LibDCT: {} s";
    /**
     * Log message printed after all benchmark sizes have been processed.
     */
    public static final String LOG_BENCHMARK_DONE = "Benchmark completed for all {} sizes";
    /**
     * Log message printed before exporting the benchmark results to CSV.
     */
    public static final String LOG_WRITING_CSV = "Writing benchmark results to CSV...";
    /**
     * Log message printed when the benchmark is canceled by the user.
     */
    public static final String LOG_BENCHMARK_CANCELLED = "Benchmark has been cancelled";
    /**
     * Prefix used when logging a CSV export failure.
     */
    public static final String LOG_CSV_FAILED_PREFIX = "Failed to export CSV file: {}";
    /**
     * Log message used when creating a CSV file.
     * Placeholders represent the output path and number of entries.
     */
    public static final String LOG_CSV_CREATE = "Creating CSV file at: {} with {} entries";
    /**
     * Log message used when a CSV file is created successfully.
     * The placeholder represents the number of rows written.
     */
    public static final String LOG_CSV_CREATED = "CSV file created successfully with {} rows";
    /**
     * Log message printed after benchmark CSV export completes successfully.
     */
    public static final String LOG_CSV_EXPORTED_SUCCESSFULLY = "Benchmark CSV exported successfully to {}";
    /**
     * Log message used when CSV creation fails.
     * The placeholder represents the target file path.
     */
    public static final String LOG_CSV_CREATE_FAILED = "Failed to create CSV file at {}";
}