package com.example.assignment.launcher;

import com.example.GUI.constants.GUIConstants;
import com.example.assignment.Part1;
import com.example.assignment.Part2;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.assignment.constants.BenchmarkConstants.LOG_BENCHMARK_CANCELLED;

/**
 * Helper class that launches Part 1 benchmarking and Part 2 compression tasks.
 *
 * <p>
 * This launcher is shared between different GUI controllers:
 * </p>
 *
 * <ul>
 *     <li>
 *         One controller launches Part 1 benchmarking and requires a shared
 *         {@link AtomicBoolean} cancellation flag.
 *     </li>
 *     <li>
 *         Another controller only launches Part 2 image compression and does
 *         not require benchmark cancellation support.
 *     </li>
 * </ul>
 *
 * <p>
 * For this reason, the launcher supports two initialization modes:
 * </p>
 *
 * <ul>
 *     <li>{@link #getInstance(AtomicBoolean)} for benchmark-aware workflows</li>
 *     <li>{@link #getInstance()} for compression-only workflows</li>
 * </ul>
 */
public class PartLauncher {

    /** Singleton instance of the launcher. */
    private static volatile PartLauncher INSTANCE;

    /** Logger used to report benchmark lifecycle events. */
    private static final Logger log = LogManager.getLogger(PartLauncher.class);

    /** Block sizes to benchmark (powers of 2). */
    private static final int[] BENCHMARK_BLOCK_SIZES = {
            8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192
    };

    /**
     * Shared cancellation flag used by Part 1 benchmarking.
     *
     * <p>
     * This field may remain {@code null} when the launcher is created by
     * controllers that only use Part 2 functionality.
     * </p>
     */
    private AtomicBoolean benchmarkCancelled;

    public static final String BENCHMARK_CANCEL_FLAG_NOT_CONFIGURED = "Benchmark cancellation flag was not configured.";


    /**
     * Returns the singleton launcher instance configured with a cancellation flag.
     *
     * <p>
     * Used by controllers that execute Part 1 benchmarking and require
     * cooperative cancellation support.
     * </p>
     *
     * @param benchmarkCancelled shared cancellation flag
     * @return singleton launcher instance
     */
    public static PartLauncher getInstance(AtomicBoolean benchmarkCancelled) {

        if (INSTANCE == null) {
            synchronized (PartLauncher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PartLauncher(benchmarkCancelled);
                }
            }
        } else if (benchmarkCancelled != null) {
            synchronized (PartLauncher.class) {
                INSTANCE.benchmarkCancelled = benchmarkCancelled;
            }
        }

        return INSTANCE;
    }

    /**
     * Returns the singleton launcher instance without benchmark cancellation support.
     *
     * <p>
     * Used by controllers that only need Part 2 compression functionality.
     * </p>
     *
     * @return singleton launcher instance
     */
    public static PartLauncher getInstance() {
        return getInstance(null);
    }

    /**
     * Creates a launcher backed by the provided cancellation flag.
     *
     * <p>
     * Intended for Part 1 benchmarking workflows.
     * </p>
     *
     * @param benchmarkCancelled shared cancellation flag
     */
    private PartLauncher(AtomicBoolean benchmarkCancelled) {
        this.benchmarkCancelled = benchmarkCancelled;
    }

    /**
     * Runs Part 1 benchmarking and handles cancellation flow.
     *
     * @return always {@code null}; method is used for side effects only
     * @throws Exception if benchmark execution fails
     * @throws IllegalStateException if benchmark cancellation support was not configured
     */
    public Void launchAndHandlePart1() throws Exception {

        if (benchmarkCancelled == null) {
            throw new IllegalStateException(BENCHMARK_CANCEL_FLAG_NOT_CONFIGURED);
        }

        // Reset cancellation state before starting a new benchmark
        benchmarkCancelled.set(false);

        Part1 part1 = new Part1();

        log.debug(GUIConstants.LOG_BENCHMARK_THREAD_START);

        List<Object> matrices = new ArrayList<>();

        for (int n : BENCHMARK_BLOCK_SIZES) {
            matrices.add(randomMatrix(n));
        }

        // Warmup benchmark
        part1.benchmark(
                BENCHMARK_BLOCK_SIZES,
                matrices,
                false,
                benchmarkCancelled::get
        );

        if (benchmarkCancelled.get()) {
            log.error(LOG_BENCHMARK_CANCELLED);
            return null;
        }

        // Actual benchmark
        part1.benchmark(
                BENCHMARK_BLOCK_SIZES,
                matrices,
                true,
                benchmarkCancelled::get
        );

        log.debug(GUIConstants.LOG_BENCHMARK_THREAD_DONE);

        return null;
    }

    /**
     * Launches Part 2 image compression.
     *
     * @param F compression parameter
     * @param d compression parameter
     * @param imagePair image to compress
     * @return compressed image
     */
    public BufferedImage launchPart2(
            int F,
            int d,
            Pair<String, BufferedImage> imagePair
    ) {

        return new Part2().compress(
                imagePair,
                F,
                d
        );
    }

    /**
     * Generates a random matrix filled with random double values.
     *
     * @param n size of the square matrix
     * @return randomly populated matrix
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