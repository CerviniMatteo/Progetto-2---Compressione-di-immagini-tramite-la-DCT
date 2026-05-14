package com.example.assignment;

import com.example.lib.DCT2;
import com.example.lib.utils.BenchmarkExecutor;
import com.example.lib.utils.JmhBenchmarkExecutor;
import com.example.lib.utils.OpenCsvUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ejml.simple.SimpleMatrix;
import org.jtransforms.dct.DoubleDCT_2D;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Part 1 - DCT Benchmark Comparison.
 * <p>
 * This class performs a comprehensive benchmark comparing the custom DCT implementation
 * against the JTransforms library implementation. The benchmark measures performance
 * across different matrix sizes and generates results for analysis.
 * </p>
 * <p>
 * Key responsibilities:
 * <ul>
 * <li>Execute benchmarks on multiple matrix sizes</li>
 * <li>Measure execution time of custom DCT vs. library DCT</li>
 * <li>Control warmup iterations to optimize JIT compilation (optional)</li>
 * <li>Record and export benchmark results to CSV</li>
 * <li>Calculate performance ratios between implementations</li>
 * </ul>
 * </p>
 *
 * @see BenchmarkExecutor
 * @see JmhBenchmarkExecutor
 * @see BenchmarkMeasurement
 */
public class Part1 {

    private static final Log log = LogFactory.getLog(Part1.class);

    private final BenchmarkExecutor benchmarkExecutor;

    /**
     * Constructs a Part1 instance with a default JMH-based benchmark executor.
     */
    public Part1() {
        this(new JmhBenchmarkExecutor());
    }

    /**
     * Constructs a Part1 instance with a custom benchmark executor.
     * <p>
     * This constructor is useful for testing purposes, allowing injection of
     * a mock or alternative implementation of {@link BenchmarkExecutor}.
     * </p>
     *
     * @param benchmarkExecutor the executor to use for running benchmarks
     */
    public Part1(BenchmarkExecutor benchmarkExecutor) {
        this.benchmarkExecutor = benchmarkExecutor;
    }

    // ==================== BENCHMARK LOGIC ====================

    private final List<BenchmarkMeasurement> results = new ArrayList<>();

    /**
     * Executes the DCT benchmark across multiple matrix sizes with optional warmup control.
     * <p>
     * This method:
     * <ol>
     * <li>Generates random matrices of specified sizes</li>
     * <li>Benchmarks the custom DCT implementation on each matrix</li>
     * <li>Benchmarks the JTransforms DCT implementation on the same matrices</li>
     * <li>Records execution times and calculates performance ratios</li>
     * <li>Exports results to a CSV file for analysis</li>
     * </ol>
     * </p>
     * <p>
     * Warmup iterations are important for stabilizing measurements because they allow
     * the Java JIT compiler to optimize hot code paths. Skipping warmup (by setting
     * {@code doWarmUp} to {@code false}) may be useful for quick tests or to measure
     * cold-start performance.
     * </p>
     *
     * @param sizes   array of matrix dimensions to benchmark (e.g., [8, 16, 32, 64, 128, 256])
     * @param doWarmUp if {@code true}, allows JMH to run warmup iterations before measurements;
     *                if {@code false}, skips warmup for faster execution but less stable results
     * @throws Exception if benchmark execution or CSV export fails
     *
     * @see BenchmarkMeasurement
     * @see JmhBenchmarkExecutor
     */
    public void benchmark(int[] sizes, boolean doWarmUp) throws Exception {
        results.clear();
        log.info(String.format(BenchmarkConstants.LOG_BENCHMARK_START, sizes.length));

        DCT2 dct = new DCT2();
        for (int n : sizes) {
            log.debug(String.format(BenchmarkConstants.LOG_BENCHMARK_SIZE, n, n));

            double[][] matrix = randomMatrix(n);

            // ===== MY DCT =====
            log.debug(String.format(BenchmarkConstants.LOG_MEASURE_CUSTOM, n));

            double myTime = benchmarkExecutor.run(() -> () -> dct.forward(new SimpleMatrix(matrix.clone())), doWarmUp);

            // ===== LIB DCT =====
            log.debug(String.format(BenchmarkConstants.LOG_MEASURE_LIBRARY, n));

            double libTime = benchmarkExecutor.run(() -> {
                DoubleDCT_2D libLocal = new DoubleDCT_2D(n, n);
                return () -> {
                    libLocal.forward(matrix.clone(), true);
                    return null;
                };
            }, doWarmUp);

            results.add(new BenchmarkMeasurement(n, myTime, libTime));

            double ratio = myTime > 0 ? libTime / myTime : 0;
            log.info(String.format(BenchmarkConstants.LOG_RESULT_ROW, n, myTime, libTime, ratio));
        }

        log.info(String.format(BenchmarkConstants.LOG_BENCHMARK_DONE, results.size()));

        double[] nValues  = new double[results.size()];
        double[] myTimes  = new double[results.size()];
        double[] libTimes = new double[results.size()];
        double[] ratios   = new double[results.size()];

        for (int i = 0; i < results.size(); i++) {
            BenchmarkMeasurement r = results.get(i);
            nValues[i]  = r.size();
            myTimes[i]  = r.customTime() * 1000;
            libTimes[i] = r.libraryTime() * 1000;
            ratios[i]   = r.customTime() > 0 ? r.libraryTime() / r.customTime() : 0;
        }

        File file = new File("output/");
        file.mkdirs();

        log.debug(BenchmarkConstants.LOG_WRITING_CSV);
        try {
            if (doWarmUp) {
                OpenCsvUtils.createCSVFile(BenchmarkConstants.TIMES_VS_SIZE_CSV_PATH_WITH_WARMUP, sizes, myTimes, libTimes, ratios);
            } else {
                OpenCsvUtils.createCSVFile(BenchmarkConstants.TIMES_VS_SIZE_CSV_PATH, sizes, myTimes, libTimes, ratios);
            }
            log.info(BenchmarkConstants.LOG_CSV_SAVED);
        } catch (Exception e) {
            log.error(BenchmarkConstants.LOG_CSV_FAILED_PREFIX + e.getMessage(), e);
        }
    }

    // ==================== UTILITIES ====================

    /**
     * Generates a random matrix filled with random double values.
     * <p>
     * This utility is used to create test matrices for the benchmark. Each element
     * is filled with a random value in the range [0.0, 1.0).
     * </p>
     *
     * @param n the size of the square matrix (n × n)
     * @return a randomly populated n×n matrix
     */
    public static double[][] randomMatrix(int n) {
        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = Math.random();
        return m;
    }
}