package com.example.launcher;

import com.example.GUI.constants.GUIConstants;
import com.example.assignment.Part1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static com.example.assignment.constants.BenchmarkConstants.LOG_BENCHMARK_CANCELLED;

/**
 * Helper class that launches Part 1 benchmarking and manages cancellation state.
 * <p>
 * The launcher is responsible for preparing the benchmark inputs, invoking
 * {@link Part1}, and logging lifecycle events for the benchmark thread.
 * </p>
 */
public class Part1Launcher {

    /** Logger used to report benchmark lifecycle events. */
    Log log = LogFactory.getLog(Part1Launcher.class);

    /** Block sizes to benchmark (powers of 2). */
    private static final int[] BENCHMARK_BLOCK_SIZES = {
            8, 16, 32, 64, 128, 256, 512, 1024, 2048
    };

    /** Cancellation flag shared with the benchmark workflow. */
    private final AtomicBoolean benchmarkCancelled;

    /**
     * Creates a launcher backed by the provided cancellation flag.
     *
     * @param benchmarkCancelled shared cancellation flag used to stop the benchmark
     */
    public Part1Launcher(AtomicBoolean benchmarkCancelled) {
        this.benchmarkCancelled = benchmarkCancelled;
    }

    /**
     * Runs Part 1 and handles the warmup and cancellation flow.
     *
     * @return always {@code null}; the method is used for side effects only
     * @throws Exception if the underlying benchmark execution fails
     */
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
