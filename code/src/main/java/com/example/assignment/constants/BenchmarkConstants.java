package com.example.assignment.constants;

/**
 * Centralized constants used by the DCT benchmark workflow.
 * <p>This class groups together:</p>
 * <ul>
 *   <li>CSV output paths for benchmark results</li>
 *   <li>Log message templates for the benchmark lifecycle</li>
 *   <li>CSV column header names</li>
 *   <li>Formatting patterns used when writing benchmark data</li>
 * </ul>
 *
 * <p>
 * The class is intentionally non-instantiable and is used only as a namespace
 * for shared benchmark-related string constants.
 * </p>
 */
public class BenchmarkConstants {

    public static final String BENCHMARK_ERROR = "Benchmark error";

    /**
     * CSV prefix path
     */
    public static final String OUTPUT_PATH = "../plots/";


    /**
     * CSV path used when the benchmark is executed without JIT warmup.
     */
    public static final String TIMES_VS_SIZE_CSV_PATH = OUTPUT_PATH  + "times_vs_size.csv";

    /**
     * CSV path used when the benchmark is executed with JIT warmup enabled.
     */
    public static final String TIMES_VS_SIZE_CSV_PATH_WITH_WARMUP = OUTPUT_PATH + "times_vs_size_with_JIT_warm_up.csv";

    /**
     * Log message printed when the benchmark starts.
     * The placeholder represents the number of matrix sizes being tested.
     */
    public static final String LOG_BENCHMARK_START = "DCT Benchmark started with %d sizes";

    /**
     * Log message printed before benchmarking a specific matrix size.
     * The two placeholders represent the matrix dimensions.
     */
    public static final String LOG_BENCHMARK_SIZE = "Benchmarking matrix size: %dx%d";

    /**
     * Log message printed before measuring the custom DCT implementation.
     */
    public static final String LOG_MEASURE_CUSTOM = "Measuring custom DCT implementation for N=%d";

    /**
     * Log message printed before measuring the library DCT implementation.
     */
    public static final String LOG_MEASURE_LIBRARY = "Measuring library DCT implementation for N=%d";

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
    public static final String LOG_RESULT_ROW = "N=%d | MyDCT: %.6f s | LibDCT: %.6f s | Ratio: %.2fx";

    /**
     * Log message printed after all benchmark sizes have been processed.
     */
    public static final String LOG_BENCHMARK_DONE = "Benchmark completed for all %d sizes";

    /**
     * Log message printed before exporting the benchmark results to CSV.
     */
    public static final String LOG_WRITING_CSV = "Writing benchmark results to CSV...";
    /**
     * Log message printed when the benchmark is canceled by the user.
     */
    public static final String LOG_BENCHMARK_CANCELLED = "Benchmark has been cancelled";

    /**
     * Exception message used when a benchmark run is interrupted by user cancellation.
     */
    public static final String BENCHMARK_CANCELLED_BY_USER = "Benchmark cancelled by user.";

    /**
     * Exception message used when JMH aborts without producing any results.
     */
    public static final String BENCHMARK_ABORTED_NO_RESULTS = "Benchmark aborted: no results collected.";

    /**
     * Prefix used when logging a CSV export failure.
     */
    public static final String LOG_CSV_FAILED_PREFIX = "Failed to export CSV file: ";

    /**
     * CSV column name for the matrix size.
     */
    public static final String CSV_HEADER_SIZE = "Size";

    /**
     * CSV column name for the custom DCT execution time.
     */
    public static final String CSV_HEADER_MY_DCT_MS = "MyDCTTime (s)";

    /**
     * CSV column name for the library DCT execution time.
     */
    public static final String CSV_HEADER_LIB_DCT_MS = "LibDCTTime (s)";

    /**
     * CSV column name for the speed ratio between library and custom DCT.
     */
    public static final String CSV_HEADER_RATIO = "Custom slower than Lib (%)";

    /**
     * Log message used when creating a CSV file.
     * Placeholders represent the output path and number of entries.
     */
    public static final String LOG_CSV_CREATE = "Creating CSV file at: %s with %d entries";

    /**
     * Pattern used by JMH to select the benchmark runner method.
     */
    public static final String JMH_BENCHMARK_INCLUDE_TEMPLATE = ".*%s\\.run";

    /**
     * Log message used when a CSV file is created successfully.
     * The placeholder represents the number of rows written.
     */
    public static final String LOG_CSV_CREATED = "CSV file created successfully with %d rows";

    /**
     * Log message used when CSV creation fails.
     * The placeholder represents the target file path.
     */
    public static final String LOG_CSV_CREATE_FAILED = "Failed to create CSV file at %s";

    /**
     * Formatting pattern for time values written to CSV.
     */
    public static final String CSV_TIME_FORMAT = "%.6f";
}