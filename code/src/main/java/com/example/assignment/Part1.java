package com.example.assignment;

import com.example.assignment.constants.BenchmarkConstants;
import com.example.assignment.model.BenchmarkMeasurement;
import com.example.lib.DCT2;
import com.example.lib.utils.BenchmarkExecutor;
import com.example.lib.utils.JmhBenchmarkExecutor;
import com.example.lib.utils.OpenCsvUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ejml.simple.SimpleMatrix;
import org.jtransforms.dct.DoubleDCT_2D;
import org.openjdk.jmh.util.Statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

import static com.example.assignment.constants.BenchmarkConstants.LOG_BENCHMARK_CANCELLED;
import static com.example.assignment.constants.BenchmarkConstants.BENCHMARK_CANCELLED_BY_USER;

/**
 * Part 1 - DCT Benchmark Comparison.
 * <p>
 * This class performs a comprehensive benchmark comparing the custom DCT implementation
 * against the JTransforms library implementation. The benchmark measures performance
 * across different matrix sizes and generates results for analysis.
 * </p>
 * <p>Key responsibilities:</p>
 * <ul>
 * <li>Execute benchmarks on multiple matrix sizes</li>
 * <li>Measure execution time of custom DCT vs. library DCT</li>
 * <li>Control warmup iterations to optimize JIT compilation</li>
 * <li>Record and export benchmark results to CSV</li>
 * <li>Calculate performance ratios between implementations</li>
 * </ul>
 *
 * <p>
 * <strong>Important:</strong> Each benchmark is run twice (typically with and without warmup).
 * To ensure fair comparison, the library DCT always operates on a deep copy of the original
 * matrix to avoid state pollution between runs.
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
     * Executes the DCT benchmark across multiple matrix sizes with optional warmup control
     * and cooperative cancellation support.
     * <p>This method:</p>
     * <ol>
     * <li>Iterates through each specified matrix size</li>
     * <li>Checks for cancellation before each block and between custom/library runs</li>
     * <li>Benchmarks the custom DCT implementation using EJML's SimpleMatrix representation</li>
     * <li>Benchmarks the JTransforms DCT implementation using a deep copy of the original matrix</li>
     * <li>Records execution times and calculates performance ratios</li>
     * <li>Aggregates all results and exports them to a CSV file for analysis</li>
     * </ol>
     * <p>
     * <strong>Cancellation:</strong> The {@code isCancelled} supplier is checked at the start
     * of each size iteration and between the two benchmark calls. Inside each JMH session,
     * the same supplier is polled at every iteration boundary, so cancellation takes effect
     * within one iteration rather than requiring the full session to finish.
     * </p>
     * <p>
     * <strong>Matrix Handling:</strong> Since benchmarks may be executed multiple times,
     * the library DCT receives a complete deep copy of the input matrix for each run.
     * This ensures the transformation results do not affect subsequent comparisons.
     * </p>
     * <p>
     * <strong>Warmup Effect:</strong> Warmup iterations are important for stabilizing measurements
     * because they allow the Java JIT compiler to optimize hot code paths. Skipping warmup (by setting
     * {@code doWarmUp} to {@code false}) may be useful for quick tests or to measure cold-start performance,
     * though results may be less stable.
     * </p>
     *
     * @param sizes       array of matrix dimensions to benchmark (e.g., [8, 16, 32, 64, 128, 256])
     * @param matrices    list of pre-generated matrices matching the sizes array; each matrix
     *                    will be used to benchmark both implementations
     * @param doWarmUp    if {@code true}, allows JMH to run warmup iterations before measurements;
     *                    if {@code false}, skips warmup for faster execution but with potentially less stable results
     * @param isCancelled a {@link Supplier} returning {@code true} when the benchmark should stop early;
     *                    use {@code () -> false} to run without cancellation support
     * @throws Exception if benchmark execution or CSV export fails
     *
     * @see BenchmarkMeasurement
     * @see JmhBenchmarkExecutor
     */
    public void benchmark(int[] sizes, List<Object> matrices, boolean doWarmUp,
                          Supplier<Boolean> isCancelled) throws Exception {
        results.clear();
        log.info(String.format(BenchmarkConstants.LOG_BENCHMARK_START, sizes.length));

        DCT2 dct = new DCT2();
        int iterator = 0;

        try {
            for (int n : sizes) {

                if (isCancelled.get()) {
                    log.info(LOG_BENCHMARK_CANCELLED);
                    return;
                }

                log.debug(String.format(BenchmarkConstants.LOG_BENCHMARK_SIZE, n, n));
                double[][] matrix = (double[][]) matrices.get(iterator++);

                Statistics myTime = benchmarkCustomDCT(dct, matrix, doWarmUp, isCancelled);

                if (isCancelled.get()) {
                    log.info(LOG_BENCHMARK_CANCELLED);
                    return;
                }

                Statistics libTime = benchmarkLibraryDCT(n, matrix, doWarmUp, isCancelled);

                BenchmarkMeasurement measurement = new BenchmarkMeasurement(n, myTime, libTime);
                results.add(measurement);

                log.info(String.format(
                        BenchmarkConstants.LOG_RESULT_ROW,
                        measurement.size(),
                        measurement.customMeanSeconds(),
                        measurement.libraryMeanSeconds(),
                        measurement.ratioOnMean()));
            }

            log.info(String.format(BenchmarkConstants.LOG_BENCHMARK_DONE, results.size()));
            exportResultsToCSV(doWarmUp);

        } catch (CancellationException e) {
            log.error(LOG_BENCHMARK_CANCELLED, null);
        } catch (InterruptedException e) {
            log.error(LOG_BENCHMARK_CANCELLED, null);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Benchmarks the custom DCT implementation on a given matrix.
     * <p>
     * The matrix is converted to an EJML SimpleMatrix for processing and the forward
     * DCT transformation is measured. The {@code isCancelled} supplier is checked at the
     * start of each JMH iteration boundary — if cancellation is requested, a
     * {@link CancellationException} is thrown from within the workload, causing JMH to
     * abort the current session.
     * </p>
     *
     * @param dct         the custom DCT2 implementation
     * @param matrix      the input matrix to transform
     * @param doWarmUp    whether to include JIT warmup iterations
     * @param isCancelled supplier polled at each iteration boundary
     * @return the full JMH {@link Statistics} summary for the custom implementation
     * @throws Exception if benchmark execution fails or is canceled
     */
    private Statistics benchmarkCustomDCT(DCT2 dct, double[][] matrix, boolean doWarmUp,
                                      Supplier<Boolean> isCancelled) throws Exception {
        log.debug(String.format(BenchmarkConstants.LOG_MEASURE_CUSTOM, matrix.length));
        SimpleMatrix simpleMatrix = new SimpleMatrix(matrix);
        return benchmarkExecutor.run(() -> () -> {
            if (isCancelled.get()) {
                throw new CancellationException(BENCHMARK_CANCELLED_BY_USER);
            }
            dct.forward(simpleMatrix);
            return null;
        }, doWarmUp);
    }

    /**
     * Benchmarks the JTransforms library DCT implementation on a deep copy of the given matrix.
     * <p>
     * <strong>Deep Copy Strategy:</strong> A complete deep copy of the input matrix is created
     * to ensure that the in-place forward transformation does not affect subsequent benchmark runs.
     * Since benchmarks may be executed multiple times, this isolation is critical for fair
     * performance measurement.
     * </p>
     * <p>
     * The deep copy is performed row-by-row using {@code Arrays.stream().map(double[]::clone)},
     * ensuring each row array is independently copied.
     * </p>
     * <p>
     * The {@code isCancelled} supplier is checked at each JMH iteration boundary. If cancellation
     * is requested, a {@link CancellationException} is thrown from within the workload lambda,
     * causing JMH to abort the current benchmark session.
     * </p>
     *
     * @param n           the matrix dimension (n x n)
     * @param matrix      the original input matrix (not modified)
     * @param doWarmUp    whether to include JIT warmup iterations
     * @param isCancelled supplier polled at each iteration boundary
     * @return the full JMH {@link Statistics} summary for the library implementation
     * @throws Exception if benchmark execution fails or is canceled
     */
    private Statistics benchmarkLibraryDCT(int n, double[][] matrix, boolean doWarmUp,
                                           Supplier<Boolean> isCancelled) throws Exception {
        log.debug(String.format(BenchmarkConstants.LOG_MEASURE_LIBRARY, n));
        DoubleDCT_2D libLocal = new DoubleDCT_2D(n, n);
        return benchmarkExecutor.run(() -> {
            double[][] matrixCopy = deepCopyMatrix(matrix);
            return () -> {
                if (isCancelled.get()) {
                    throw new CancellationException(BENCHMARK_CANCELLED_BY_USER);
                }
                libLocal.forward(matrixCopy, true);
                return null;
            };
        }, doWarmUp);
    }

    /**
     * Creates a complete deep copy of a 2D double array.
     * <p>
     * This method ensures that each row of the original matrix is independently cloned,
     * producing a fully independent copy suitable for in-place transformations.
     * </p>
     *
     * @param matrix the original matrix to copy
     * @return a new matrix with independent row copies
     */
    private double[][] deepCopyMatrix(double[][] matrix) {
        return Arrays.stream(matrix)
                .map(double[]::clone)
                .toArray(double[][]::new);
    }

    /**
     * Collects benchmark results and exports them to a CSV file.
     * <p><strong>File Output:</strong></p>
     * <ul>
     * <li>With warmup: {@code output/times_vs_size_with_JIT_warm_up.csv}</li>
     * <li>Without warmup: {@code output/times_vs_size.csv}</li>
     * </ul>
     * Each CSV file contains the matrix sizes, custom implementation times, library times,
     * and computed performance ratios (library time / custom time).
     *
     * @param doWarmUp whether warmup was enabled (determines output filename)
     */
    private void exportResultsToCSV(boolean doWarmUp) {
        log.debug(BenchmarkConstants.LOG_WRITING_CSV);
        try {
            String outputPath = doWarmUp
                    ? BenchmarkConstants.TIMES_VS_SIZE_CSV_PATH_WITH_WARMUP
                    : BenchmarkConstants.TIMES_VS_SIZE_CSV_PATH;

            OpenCsvUtils.createCSVFile(outputPath, results);
            log.info(String.format("Benchmark CSV exported successfully to %s", outputPath));
        } catch (Exception e) {
            log.error(BenchmarkConstants.LOG_CSV_FAILED_PREFIX + e.getMessage(), e);
        }
    }
}