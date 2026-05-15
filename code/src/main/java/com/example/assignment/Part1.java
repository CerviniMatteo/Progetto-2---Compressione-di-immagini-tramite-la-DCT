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
import java.util.Arrays;
import java.util.List;

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
     * Executes the DCT benchmark across multiple matrix sizes with optional warmup control.
     * <p>This method:</p>
     * <ol>
     * <li>Iterates through each specified matrix size</li>
     * <li>Benchmarks the custom DCT implementation using EJML's SimpleMatrix representation</li>
     * <li>Benchmarks the JTransforms DCT implementation using a deep copy of the original matrix</li>
     * <li>Records execution times and calculates performance ratios</li>
     * <li>Aggregates all results and exports them to a CSV file for analysis</li>
     * </ol>
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
     * @param sizes    array of matrix dimensions to benchmark (e.g., [8, 16, 32, 64, 128, 256])
     * @param matrices list of pre-generated matrices matching the sizes array; each matrix
     *                 will be used to benchmark both implementations
     * @param doWarmUp if {@code true}, allows JMH to run warmup iterations before measurements;
     *                if {@code false}, skips warmup for faster execution but with potentially less stable results
     * @throws Exception if benchmark execution or CSV export fails
     *
     * @see BenchmarkMeasurement
     * @see JmhBenchmarkExecutor
     */
    public void benchmark(int[] sizes, List<Object> matrices, boolean doWarmUp) throws Exception {
        results.clear();
        log.info(String.format(BenchmarkConstants.LOG_BENCHMARK_START, sizes.length));

        DCT2 dct = new DCT2();
        int iterator = 0;
        for (int n : sizes) {
            log.debug(String.format(BenchmarkConstants.LOG_BENCHMARK_SIZE, n, n));
            double[][] matrix = (double[][]) matrices.get(iterator++);

            // Benchmark custom DCT implementation
            double myTime = benchmarkCustomDCT(dct, matrix, doWarmUp);

            // Benchmark library DCT implementation with deep copy
            double libTime = benchmarkLibraryDCT(n, matrix, doWarmUp);

            results.add(new BenchmarkMeasurement(n, myTime, libTime));

            double ratio = myTime > 0 ? libTime / myTime : 0;
            log.info(String.format(BenchmarkConstants.LOG_RESULT_ROW, n, myTime, libTime, ratio));
        }

        log.info(String.format(BenchmarkConstants.LOG_BENCHMARK_DONE, results.size()));
        exportResultsToCSV(sizes, doWarmUp);
    }

    /**
     * Benchmarks the custom DCT implementation on a given matrix.
     * <p>
     * The matrix is converted to an EJML SimpleMatrix for processing and the forward
     * DCT transformation is measured.
     * </p>
     *
     * @param dct      the custom DCT2 implementation
     * @param matrix   the input matrix to transform
     * @param doWarmUp whether to include JIT warmup iterations
     * @return execution time in seconds
     * @throws Exception if benchmark execution fails
     */
    private double benchmarkCustomDCT(DCT2 dct, double[][] matrix, boolean doWarmUp) throws Exception {
        log.debug(String.format(BenchmarkConstants.LOG_MEASURE_CUSTOM, matrix.length));
        SimpleMatrix simpleMatrix = new SimpleMatrix(matrix);
        return benchmarkExecutor.run(() -> () -> {
            dct.forward(simpleMatrix);
            return null;
        }, doWarmUp);
    }

    /**
     * Benchmarks the JTransforms library DCT implementation on a deep copy of the given matrix.
     * <p>Error:  Failed to execute goal org.apache.maven.plugins:maven-javadoc-plugin:3.12.0:javadoc (default-cli) on project matrix-library: An error has occurred in Javadoc report generation:
4583
Error:  Exit code: 1
4584
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/assignment/Part1.java:24: warning: empty <p> tag
4585
Error:   * <p>Key responsibilities:<p>
4586
Error:                             ^
4587
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/GUI/UI/CompressionCoefficientsPicker.java:33: error: malformed HTML
4588
Error:   *   <li>{@code d} d must satisfy: 0 <= d <= 2*F - 2</li>
4589
Error:                                       ^
4590
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/GUI/UI/CompressionCoefficientsPicker.java:33: error: malformed HTML
4591
Error:   *   <li>{@code d} d must satisfy: 0 <= d <= 2*F - 2</li>
4592
Error:                                            ^
4593
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/Application.java:19: warning: use of default constructor, which does not provide a comment
4594
Error:  public class Application {
4595
Error:         ^
4596
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/assignment/BenchmarkConstants.java:19: warning: use of default constructor, which does not provide a comment
4597
Error:  public class BenchmarkConstants {
4598
Error:         ^
4599
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/lib/DCT2.java:26: warning: use of default constructor, which does not provide a comment
4600
Error:      public class DCT2 {
4601
Error:             ^
4602
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/GUI/constants/GUIConstants.java:16: warning: use of default constructor, which does not provide a comment
4603
Error:  public class GUIConstants {
4604
Error:         ^
4605
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/GUI/UI/ImagePicker.java:48: warning: use of default constructor, which does not provide a comment
4606
Error:  public class ImagePicker {
4607
Error:         ^
4608
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/lib/utils/ImageUtils.java:25: warning: use of default constructor, which does not provide a comment
4609
Error:  public class ImageUtils {
4610
Error:         ^
4611
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/lib/utils/JmhBenchmarkExecutor.java:31: warning: use of default constructor, which does not provide a comment
4612
Error:  public class JmhBenchmarkExecutor implements BenchmarkExecutor {
4613
Error:         ^
4614
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/lib/utils/JmhBenchmarkExecutor.java:66: warning: use of default constructor, which does not provide a comment
4615
Error:      public static class BenchmarkRunner {
4616
Error:                    ^
4617
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/lib/utils/JmhBenchmarkExecutor.java:45: warning: use of default constructor, which does not provide a comment
4618
Error:      public static class BenchState {
4619
Error:                    ^
4620
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/GUI/observer/Observable.java:18: warning: use of default constructor, which does not provide a comment
4621
Error:  public class Observable<T> {
4622
Error:         ^
4623
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/lib/utils/OpenCsvUtils.java:14: warning: use of default constructor, which does not provide a comment
4624
Error:  public class OpenCsvUtils {
4625
Error:         ^
4626
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/assignment/Part1.java:75: error: malformed HTML
4627
Error:       * <p>This method:</p
4628
Error:                        ^
4629
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/assignment/Part2.java:20: warning: use of default constructor, which does not provide a comment
4630
Error:  public class Part2 {
4631
Error:         ^
4632
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/GUI/constants/PickerConstants.java:13: warning: use of default constructor, which does not provide a comment
4633
Error:  public class PickerConstants {
4634
Error:         ^
4635
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/GUI/factory/StylingFactory.java:24: warning: use of default constructor, which does not provide a comment
4636
Error:  public class StylingFactory {
4637
Error:         ^
4638
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/lib/utils/UtilsConstants.java:11: warning: use of default constructor, which does not provide a comment
4639
Error:  public class UtilsConstants {
4640
Error:         ^
4641
Error:  /home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/src/main/java/com/example/GUI/UI/ImageCompressionWindow.java:56: warning: no comment
4642
Error:      private JPanel compressedBox;
4643
Error:                     ^
4644
Error: [ERROR] 3 errors
4645
Error:  17 warnings
4646
Error:  Command line was: /usr/lib/jvm/temurin-21-jdk-amd64/bin/javadoc -J-Duser.language= -J-Duser.country= @options @packages
4647
Error:
4648
Error:  Refer to the generated Javadoc files in '/home/runner/work/Progetto-2---Compressione-di-immagini-tramite-la-DCT/Progetto-2---Compressione-di-immagini-tramite-la-DCT/code/target/reports/apidocs' dir.
4649
Error:
4650
Error:  -> [Help 1]
4651
Error:
4652
Error:  To see the full stack trace of the errors, re-run Maven with the -e switch.
4653
Error:  Re-run Maven using the -X switch to enable full debug logging.
4654
Error:
4655
Error:  For more information about the errors and possible solutions, please read the following articles:
4656
Error:  [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException
4657
Error: Process completed with exit code 1.
     * <strong>Deep Copy Strategy:</strong> A complete deep copy of the input matrix is created
     * to ensure that the in-place forward transformation does not affect subsequent benchmark runs.
     * Since benchmarks may be executed multiple times, this isolation is critical for fair
     * performance measurement.
     * </p>
     * <p>
     * The deep copy is performed row-by-row using {@code Arrays.stream().map(double[]::clone)},
     * ensuring each row array is independently copied.
     * </p>
     *
     * @param n        the matrix dimension (n x n)
     * @param matrix   the original input matrix (not modified)
     * @param doWarmUp whether to include JIT warmup iterations
     * @return execution time in seconds
     * @throws Exception if benchmark execution fails
     */
    private double benchmarkLibraryDCT(int n, double[][] matrix, boolean doWarmUp) throws Exception {
        log.debug(String.format(BenchmarkConstants.LOG_MEASURE_LIBRARY, n));

        DoubleDCT_2D libLocal = new DoubleDCT_2D(n, n);

        // Create deep copy of matrix to prevent state pollution between benchmark runs
        double[][] matrixCopy = deepCopyMatrix(matrix);

        return benchmarkExecutor.run(() -> () -> {
            libLocal.forward(matrixCopy, true);
            return null;
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
     * Collects benchmark results into arrays and exports them to a CSV file.
     * <p><strong>File Output:</strong></p>
     * <ul>
     * <li>With warmup: {@code output/times_vs_size_with_JIT_warm_up.csv}</li>
     * <li>Without warmup: {@code output/times_vs_size.csv}</li>
     * </ul>
     * Each CSV file contains the matrix sizes, custom implementation times, library times,
     * and computed performance ratios (library time / custom time).
     *
     * @param sizes    array of matrix dimensions used in the benchmark
     * @param doWarmUp whether warmup was enabled (determines output filename)
     */
    private void exportResultsToCSV(int[] sizes, boolean doWarmUp) {
        double[] myTimes  = new double[results.size()];
        double[] libTimes = new double[results.size()];
        double[] ratios   = new double[results.size()];

        for (int i = 0; i < results.size(); i++) {
            BenchmarkMeasurement r = results.get(i);
            myTimes[i]  = r.customTime();
            libTimes[i] = r.libraryTime();
            ratios[i]   = r.customTime() > 0 ? r.libraryTime() / r.customTime() : 0;
        }

        File outputDir = new File("output/");
        outputDir.mkdirs();

        log.debug(BenchmarkConstants.LOG_WRITING_CSV);
        try {
            String outputPath = doWarmUp
                    ? BenchmarkConstants.TIMES_VS_SIZE_CSV_PATH_WITH_WARMUP
                    : BenchmarkConstants.TIMES_VS_SIZE_CSV_PATH;

            OpenCsvUtils.createCSVFile(outputPath, sizes, myTimes, libTimes, ratios);
            log.info(BenchmarkConstants.LOG_CSV_SAVED);
        } catch (Exception e) {
            log.error(BenchmarkConstants.LOG_CSV_FAILED_PREFIX + e.getMessage(), e);
        }
    }
}