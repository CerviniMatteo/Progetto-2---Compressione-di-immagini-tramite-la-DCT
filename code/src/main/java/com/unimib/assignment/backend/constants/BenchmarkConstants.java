package com.unimib.assignment.backend.constants;

/**
 * Constants used by the DCT benchmark workflow.
 *
 * <p>
 * The class is intentionally non-instantiable and is used only as a namespace
 * for shared benchmark-related string constants.
 * </p>
 */
public final class BenchmarkConstants {

    /**
     * Exception message used when the benchmark gets canceled
     */
    public static final String BENCHMARK_ERROR = "Benchmark error";

    /**
     * Exception message used when the benchmark cancellation flag is not properly configured.
     */
    public static final String BENCHMARK_CANCEL_FLAG_NOT_CONFIGURED = "Benchmark cancellation flag was not configured.";

    /**
     * CSV prefix path
     */
    public static final String OUTPUT_PATH = "../plots/";

    /**
     * CSV path used when the benchmark is executed without JIT warmup.
     */
    public static final String TIMES_VS_SIZE_CSV_PATH = OUTPUT_PATH  + "times_vs_size.csv";

    /**
     * Exception message used when a benchmark run is interrupted by user cancellation.
     */
    public static final String BENCHMARK_CANCELLED_BY_USER = "Benchmark cancelled by user.";

    /**
     * Exception message used when JMH aborts without producing any results.
     */
    public static final String BENCHMARK_ABORTED_NO_RESULTS = "Benchmark aborted: no results collected.";

    /**
     * Pattern used by JMH to select the benchmark runner method.
     */
    public static final String JMH_BENCHMARK_INCLUDE_TEMPLATE = ".*%s\\.run";

    /**
      * Formatting pattern for time values with higher precision (9 decimal places).
      * Used for detailed benchmark statistics display.
      */
    public static final String TIME_FORMAT_HIGH_PRECISION = "%.9f";



}