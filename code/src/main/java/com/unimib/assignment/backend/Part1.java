package com.unimib.assignment.backend;

import com.unimib.assignment.backend.benchmark.BenchmarkExecutor;
import com.unimib.assignment.backend.constants.BenchmarkConstants;
import com.unimib.assignment.backend.constants.LogConstants;
import com.unimib.assignment.backend.lib.DCT2;
import com.unimib.assignment.backend.model.BenchmarkMeasurement;
import com.unimib.assignment.backend.utils.OpenCsvUtils;
import com.unimib.assignment.backend.benchmark.JmhBenchmarkExecutor;
import org.ejml.simple.SimpleMatrix;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jtransforms.dct.DoubleDCT_2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

import static com.unimib.assignment.backend.constants.BenchmarkConstants.TIMES_VS_SIZE_CSV_PATH;
import static com.unimib.assignment.backend.constants.LogConstants.LOG_BENCHMARK_CANCELLED;
import static com.unimib.assignment.backend.constants.BenchmarkConstants.BENCHMARK_CANCELLED_BY_USER;

/**
 * Part 1 - DCT Benchmark Comparison.
 * <p>
 * This class performs a comprehensive benchmark comparing the custom DCT implementation
 * against the JTransforms library implementation. The benchmark measures performance
 * across different matrix sizes and generates results for analysis.
 * </p>
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

    private static final Logger log = LogManager.getLogger(Part1.class);

    // Handles the actual benchmark execution strategy (JMH-based by default).
    private final BenchmarkExecutor benchmarkExecutor;

    /**
     * Constructs a Part1 instance with a default JMH-based benchmark executor.
     */
    public Part1() {
        // Use the production benchmark runner.
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
        // Store the executor so the benchmark logic stays testable and swappable.
        this.benchmarkExecutor = benchmarkExecutor;
    }


    // Accumulates one result row per image size before exporting to CSV.
    private final List<BenchmarkMeasurement> results = new ArrayList<>();

    /**
     * Executes the DCT benchmark across multiple matrix sizes
     *
     * @param sizes       array of matrix dimensions to benchmark (e.g., [8, 16, 32, 64, 128, 256])
     * @param matrices    list of pre-generated matrices matching the sizes array; each matrix
     *                    will be used to benchmark both implementations
     * @param isCancelled a {@link Supplier} returning {@code true} when the benchmark should stop early;
     *                    use {@code () -> false} to run without cancellation support
     * @throws Exception if benchmark execution or CSV export fails
     *
     * @see BenchmarkMeasurement
     * @see JmhBenchmarkExecutor
     */
    public void benchmark(int[] sizes, List<Object> matrices,
                          Supplier<Boolean> isCancelled) throws Exception {
        // Start from a clean result set for every benchmark invocation.
        results.clear();
        log.info(LogConstants.LOG_BENCHMARK_START, sizes.length);

        // Reuse the custom DCT instance across all sizes to avoid unnecessary allocations.
        DCT2 dct = new DCT2();
        int iterator = 0;

        try {
            for (int n : sizes) {

                // Stop immediately if the caller requested cancellation.
                if (isCancelled.get()) {
                    log.error(LOG_BENCHMARK_CANCELLED);
                    return;
                }

                log.debug(LogConstants.LOG_BENCHMARK_SIZE, n, n);
                // Read the matrix that corresponds to the current size.
                double[][] matrix = (double[][]) matrices.get(iterator++);

                // Measure the custom implementation first so both approaches use the same source data.
                double myTime = benchmarkCustomDCT(dct, matrix, isCancelled);

                // Check cancellation again before starting the library benchmark.
                if (isCancelled.get()) {
                    log.info(LOG_BENCHMARK_CANCELLED);
                    return;
                }

                // Measure the reference implementation on an isolated copy of the matrix.
                double libTime = benchmarkLibraryDCT(n, matrix, isCancelled);

                // Store the pair of timings for later export.
                BenchmarkMeasurement measurement = new BenchmarkMeasurement(n, myTime, libTime);
                results.add(measurement);

                log.info(LogConstants.LOG_RESULT_ROW,
                        measurement.size(),
                        String.format(BenchmarkConstants.TIME_FORMAT_HIGH_PRECISION, measurement.customMeanSeconds()),
                        String.format(BenchmarkConstants.TIME_FORMAT_HIGH_PRECISION, measurement.libraryMeanSeconds()));
            }

            log.info(LogConstants.LOG_BENCHMARK_DONE, results.size());

            // Persist the collected benchmark table after all sizes complete successfully.
            exportResultsToCSV();

        } catch (CancellationException e) {
            // JMH can surface cancellation as an exception when the workload aborts early.
            log.error(LOG_BENCHMARK_CANCELLED, e);
        } catch (InterruptedException e) {
            // Preserve the interruption signal so upstream code can react correctly.
            log.error(LOG_BENCHMARK_CANCELLED, e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Benchmarks the custom DCT implementation on a given matrix.
     *
     * @param dct         the custom DCT2 implementation
     * @param matrix      the input matrix to transform
     * @param isCancelled supplier polled at each iteration boundary
     * @return the average execution time in microsecond of the custom implementation
     * @throws Exception if benchmark execution fails or is canceled
     */
    private double benchmarkCustomDCT(DCT2 dct, double[][] matrix,
                                      Supplier<Boolean> isCancelled) throws Exception {
        log.debug(LogConstants.LOG_MEASURE_CUSTOM, matrix.length);
        // Wrap the input in an EJML matrix because the custom DCT operates on SimpleMatrix.
        SimpleMatrix simpleMatrix = new SimpleMatrix(matrix);
        // Delegate timing to the configured benchmark executor.
        return benchmarkExecutor.run(() -> () -> {
            // Let JMH stop cleanly between iterations when cancellation is requested.
            if (isCancelled.get()) {
                throw new CancellationException(BENCHMARK_CANCELLED_BY_USER);
            }
            // Execute the forward DCT once per measured invocation.
            return dct.forward(simpleMatrix);
        });
    }

    /**
     * Benchmarks the JTransforms library DCT implementation on a deep copy of the given matrix.
     *
     * @param n           the matrix dimension (n x n)
     * @param matrix      the original input matrix (not modified)
     * @param isCancelled supplier polled at each iteration boundary
     * @return the average execution time in microsecond of the library implementation
     * @throws Exception if benchmark execution fails or is canceled
     */
    private double benchmarkLibraryDCT(int n, double[][] matrix,
                                           Supplier<Boolean> isCancelled) throws Exception {
        log.debug(LogConstants.LOG_MEASURE_LIBRARY, n);
        // Create the JTransforms DCT instance for the current matrix size.
        DoubleDCT_2D libLocal = new DoubleDCT_2D(n, n);
        // Run the benchmark with a supplier that prepares a fresh matrix copy per execution.
        return benchmarkExecutor.run(() -> {
            // Deep-copy the source matrix so the in-place library call cannot modify the original matrix.
            double[][] matrixCopy = deepCopyMatrix(matrix);
            return () -> {
                // Respect user cancellation before doing the measured work.
                if (isCancelled.get()) {
                    throw new CancellationException(BENCHMARK_CANCELLED_BY_USER);
                }
                // JTransforms writes the result back into the provided array.
                libLocal.forward(matrixCopy, true);
                return null;
            };
        });
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
        // Clone each row independently so the returned matrix has no shared row arrays.
        return Arrays.stream(matrix)
                .map(double[]::clone)
                .toArray(double[][]::new);
    }

    /**
     * Collects benchmark results and exports them to a CSV file.
     * Each CSV file contains the matrix sizes, custom implementation times, library times
     */
    private void exportResultsToCSV() {
        log.debug(LogConstants.LOG_WRITING_CSV);
        try {
            // Write the benchmark table to the configured CSV output path.
            OpenCsvUtils.createCSVFile(TIMES_VS_SIZE_CSV_PATH, results);
            log.info(LogConstants.LOG_CSV_EXPORTED_SUCCESSFULLY, TIMES_VS_SIZE_CSV_PATH);
        } catch (Exception e) {
            // Log any export issue without breaking the benchmark flow retroactively.
            log.error(LogConstants.LOG_CSV_FAILED_PREFIX, e.getMessage(), e);
        }
    }
}