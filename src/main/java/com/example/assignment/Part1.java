package com.example.assignment;

import com.example.lib.DCT2;
import com.example.lib.utils.OpenCsvUtils;
import com.example.lib.utils.PlotUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ejml.simple.SimpleMatrix;
import org.jtransforms.dct.DoubleDCT_2D;

import java.util.ArrayList;
import java.util.List;
import static com.example.lib.utils.Timer.measure;

/**
 * Runs the DCT benchmark for the custom implementation versus the library implementation.
 * <p>
 * The benchmark generates random square matrices, measures both implementations on the same
 * input data, and then exports the results as both a plot and a CSV file.
 * </p>
 */
public class Part1 {

    /**
     * Logger for benchmark progress and results.
     */
    private static final Log log = LogFactory.getLog(Part1.class);

    /**
     * Simple container for one benchmark sample.
     */
    static class Result {
        /** Matrix size used for the measurement. */
        int n;

        /** Execution time of the custom DCT implementation, in seconds. */
        double myTime;

        /** Execution time of the library DCT implementation, in seconds. */
        double libTime;

        /**
         * Creates a benchmark result record.
         *
         * @param n the matrix size
         * @param myTime the measured time for the custom DCT implementation
         * @param libTime the measured time for the library DCT implementation
         */
        public Result(int n, double myTime, double libTime) {
            this.n = n;
            this.myTime = myTime;
            this.libTime = libTime;
        }
    }

    /** Collected results for all benchmarked sizes. */
    List<Result> results = new ArrayList<>();

    /**
     * Runs the DCT benchmark for each size in the provided array.
     * <p>
     * For each matrix size, this method:
     * </p>
     * <ol>
     *   <li>Generates a random square matrix.</li>
     *   <li>Creates deep copies so both implementations use identical input data.</li>
     *   <li>Measures the custom DCT and the library DCT.</li>
     *   <li>Stores the results.</li>
     *   <li>Plots the benchmark and writes a CSV file in the {@code output/} directory.</li>
     * </ol>
     * <p>
     * The measured times are converted from seconds to milliseconds before plotting and export.
     * </p>
     *
     * @param sizes the matrix sizes to benchmark
     */
    public void benchmark(int[] sizes) {
        log.info(String.format(BenchmarkConstants.LOG_BENCHMARK_START, sizes.length));

        DCT2 dct = new DCT2();

        for (int n : sizes) {
            log.debug(String.format(BenchmarkConstants.LOG_BENCHMARK_SIZE, n, n));

            double[][] matrix = randomMatrix(n);

            double[][] copy1 = matrix.clone();
            double[][] copy2 = matrix.clone();

            SimpleMatrix input = new SimpleMatrix(copy1);

            DoubleDCT_2D lib = new DoubleDCT_2D(n, n);

            // ===== MY DCT =====
            log.debug(String.format(BenchmarkConstants.LOG_MEASURE_CUSTOM, n));
            double myTime = measure(() -> dct.DCT2(input), 3);

            // ===== LIB DCT =====
            log.debug(String.format(BenchmarkConstants.LOG_MEASURE_LIBRARY, n));
            double libTime = measure(() -> lib.forward(copy2, true), 3);

            results.add(new Result(n, myTime, libTime));

            double ratio = myTime > 0 ? libTime / myTime : 0;
            log.info(String.format(BenchmarkConstants.LOG_RESULT_ROW,
                    n, myTime, libTime, ratio));
        }

        log.info(String.format(BenchmarkConstants.LOG_BENCHMARK_DONE, results.size()));

        double[] nValues = new double[results.size()];
        double[] myTimes = new double[results.size()];
        double[] libTimes = new double[results.size()];
        double[] ratios = new double[results.size()];

        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            nValues[i] = r.n;
            myTimes[i] = r.myTime * 1000;
            libTimes[i] = r.libTime * 1000;
            ratios[i] = r.myTime > 0 ? r.libTime / r.myTime : 0;
        }

        log.debug(BenchmarkConstants.LOG_GENERATING_PLOT);
        try {
            PlotUtils.plotDCTBenchmark(
                    nValues,
                    myTimes,
                    libTimes,
                    BenchmarkConstants.DCT_BENCHMARK_TITLE
            );
            log.info(BenchmarkConstants.LOG_PLOT_SAVED);
        } catch (Exception e) {
            log.error(BenchmarkConstants.LOG_PLOT_FAILED_PREFIX + e.getMessage(), e);
        }

        log.debug(BenchmarkConstants.LOG_WRITING_CSV);
        try {
            OpenCsvUtils.createCSVFile(BenchmarkConstants.TIMES_VS_SIZE_CSV_PATH, sizes, myTimes, libTimes, ratios);
            log.info(BenchmarkConstants.LOG_CSV_SAVED);
        } catch (Exception e) {
            log.error(BenchmarkConstants.LOG_CSV_FAILED_PREFIX + e.getMessage(), e);
        }
    }

    /**
     * Creates an {@code n x n} matrix filled with random values in the range {@code [0, 1)}.
     *
     * @param n the size of the square matrix to generate
     * @return a randomly generated square matrix
     */
    public static double[][] randomMatrix(int n) {
        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                m[i][j] = Math.random();
            }
        }
        return m;
    }
}