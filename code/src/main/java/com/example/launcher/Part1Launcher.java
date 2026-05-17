package com.example.launcher;

import com.example.GUI.constants.GUIConstants;
import com.example.assignment.Part1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static com.example.assignment.constants.BenchmarkConstants.LOG_BENCHMARK_CANCELLED;

public class Part1Launcher {

    Log log = LogFactory.getLog(Part1Launcher.class);

    /** Block sizes to benchmark (powers of 2). */
    private static final int[] BENCHMARK_BLOCK_SIZES = {
            8, 16, 32, 64, 128, 256, 512, 1024, 2048
    };


    /** Cancellation flag for benchmark runs. */
    private final AtomicBoolean benchmarkCancelled;

    public Part1Launcher(AtomicBoolean benchmarkCancelled) {
        this.benchmarkCancelled = benchmarkCancelled;
    }

    public Void launchAndHandlePart1() throws Exception {
        // Reset cancellation flag so a fresh run is not immediately canceled
        benchmarkCancelled.set(false);

        Part1 part1 = new Part1();
        log.debug(GUIConstants.LOG_BENCHMARK_THREAD_START);

        List<Object> matrices = new ArrayList<>();
        for (int n : BENCHMARK_BLOCK_SIZES) {
            matrices.add(randomMatrix(n));
        }

        // Pass the shared benchmarkCancelled flag — the stop button sets this same flag
        part1.benchmark(BENCHMARK_BLOCK_SIZES, matrices, false, benchmarkCancelled::get);

        if (benchmarkCancelled.get()) {
            log.info(LOG_BENCHMARK_CANCELLED);
            return null;
        }

        part1.benchmark(BENCHMARK_BLOCK_SIZES, matrices, true, benchmarkCancelled::get);

        log.debug(GUIConstants.LOG_BENCHMARK_THREAD_DONE);
        return null;
    }


    /**
     * Generates a random matrix filled with random double values.
     *
     * @param n the size of the square matrix (n x n)
     * @return a randomly populated matrix
     */
    public static double[][] randomMatrix(int n) {

        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                m[i][j] = Math.random();
        return m;
    }
}
