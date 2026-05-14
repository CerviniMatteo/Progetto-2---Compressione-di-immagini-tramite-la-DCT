package com.example.assignment;

import com.example.lib.DCT2;
import com.example.lib.utils.BenchmarkExecutor;
import com.example.lib.utils.JmhBenchmarkExecutor;
import com.example.lib.utils.OpenCsvUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ejml.simple.SimpleMatrix;
import org.jtransforms.dct.DoubleDCT_2D;

import java.util.ArrayList;
import java.util.List;

public class Part1 {

    private static final Log log = LogFactory.getLog(Part1.class);

    private final BenchmarkExecutor benchmarkExecutor;

    public Part1() {
        this(new JmhBenchmarkExecutor());
    }

    public Part1(BenchmarkExecutor benchmarkExecutor) {
        this.benchmarkExecutor = benchmarkExecutor;
    }

    // ==================== BENCHMARK LOGIC ====================

    private final List<BenchmarkMeasurement> results = new ArrayList<>();

    public void benchmark(int[] sizes) throws Exception {
        results.clear();
        log.info(String.format(BenchmarkConstants.LOG_BENCHMARK_START, sizes.length));

        DCT2 dct = new DCT2();

        for (int n : sizes) {
            log.debug(String.format(BenchmarkConstants.LOG_BENCHMARK_SIZE, n, n));

            double[][] matrix = randomMatrix(n);

            // ===== MY DCT =====
            log.debug(String.format(BenchmarkConstants.LOG_MEASURE_CUSTOM, n));

            double myTime = benchmarkExecutor.run(() -> () -> dct.forward(new SimpleMatrix(matrix.clone())));

            // ===== LIB DCT =====
            log.debug(String.format(BenchmarkConstants.LOG_MEASURE_LIBRARY, n));

            double libTime = benchmarkExecutor.run(() -> {
                DoubleDCT_2D libLocal = new DoubleDCT_2D(n, n);
                return () -> {
                    libLocal.forward(matrix.clone(), true);
                    return null;
                };
            });

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

        log.debug(BenchmarkConstants.LOG_WRITING_CSV);
        try {
            OpenCsvUtils.createCSVFile(BenchmarkConstants.TIMES_VS_SIZE_CSV_PATH, sizes, myTimes, libTimes, ratios);
            log.info(BenchmarkConstants.LOG_CSV_SAVED);
        } catch (Exception e) {
            log.error(BenchmarkConstants.LOG_CSV_FAILED_PREFIX + e.getMessage(), e);
        }
    }

    // ==================== UTILITIES ====================

    public static double[][] randomMatrix(int n) {
        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = Math.random();
        return m;
    }
}