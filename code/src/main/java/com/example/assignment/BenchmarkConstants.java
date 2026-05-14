package com.example.assignment;

public class BenchmarkConstants {
    public static final String TIMES_VS_SIZE_CSV_PATH = "output/times_vs_size.csv";
    public static final String TIMES_VS_SIZE_CSV_PATH_WITH_WARMUP = "output/times_vs_size_with_JIT_warm_up.csv";

    public static final String LOG_BENCHMARK_START = "DCT Benchmark started with %d sizes";
    public static final String LOG_BENCHMARK_SIZE = "Benchmarking matrix size: %dx%d";
    public static final String LOG_MEASURE_CUSTOM = "Measuring custom DCT implementation for N=%d";
    public static final String LOG_MEASURE_LIBRARY = "Measuring library DCT implementation for N=%d";
    public static final String LOG_RESULT_ROW = "N=%d | MyDCT: %.6f s | LibDCT: %.6f s | Ratio: %.2fx";
    public static final String LOG_BENCHMARK_DONE = "Benchmark completed for all %d sizes";
    public static final String LOG_WRITING_CSV = "Writing benchmark results to CSV...";
    public static final String LOG_CSV_SAVED = "Benchmark CSV exported successfully to output/times_vs_size.csv";
    public static final String LOG_CSV_FAILED_PREFIX = "Failed to export CSV file: ";

    public static final String CSV_HEADER_SIZE = "Size";
    public static final String CSV_HEADER_MY_DCT_MS = "MyDCTTime (s)";
    public static final String CSV_HEADER_LIB_DCT_MS = "LibDCTTime (s)";
    public static final String CSV_HEADER_RATIO = "Ratio (Lib/My)";

    public static final String LOG_CSV_CREATE = "Creating CSV file at: %s with %d entries";
    public static final String LOG_CSV_CREATED = "CSV file created successfully with %d rows";
    public static final String LOG_CSV_CREATE_FAILED = "Failed to create CSV file at %s";

    public static final String CSV_TIME_FORMAT = "%.6f";
    public static final String CSV_RATIO_FORMAT = "%.4f";
}

