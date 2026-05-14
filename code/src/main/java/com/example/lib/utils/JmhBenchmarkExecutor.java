package com.example.lib.utils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * JMH-based (Java Microbenchmark Harness) implementation of {@link BenchmarkExecutor}.
 * <p>
 * This class uses the OpenJDK JMH framework to perform accurate microbenchmarking. It measures
 * the average execution time of a task by running it multiple times with configurable warmup
 * and measurement iterations.
 * </p>
 * <p>
 * Key features:
 * <ul>
 * <li>Configurable warmup iterations to let the JIT compiler optimize code</li>
 * <li>Multiple measurement iterations to reduce noise and improve accuracy</li>
 * <li>No forking to keep measurements in-process for faster execution</li>
 * <li>Time measurement in microseconds for fine-grained precision</li>
 * </ul>
 * </p>
 *
 * @see BenchmarkExecutor
 */
public class JmhBenchmarkExecutor implements BenchmarkExecutor {

    /** Default number of warmup iterations */
    private static final int DEFAULT_WARMUP_ITERATIONS = 3;

    /** Default number of measurement iterations */
    private static final int DEFAULT_MEASUREMENT_ITERATIONS = 5;

    private static volatile Supplier<Supplier<?>> pendingFactory;

    /**
     * JMH benchmark state holder that stores the task to be benchmarked.
     */
    @State(Scope.Benchmark)
    public static class BenchState {
        /** The task supplier to be executed during benchmarking */
        public Supplier<?> task;

        /**
         * JMH setup method called at the beginning of each benchmark trial.
         * Initializes the task from the pending factory.
         */
        @Setup(Level.Trial)
        public void setup() {
            task = pendingFactory.get();
        }
    }

    /**
     * The actual benchmark method executed by JMH.
     * <p>
     * This inner class is discovered and executed by JMH's annotation processing framework.
     * The benchmark measures the average time to execute a single task iteration.
     * </p>
     */
    public static class BenchmarkRunner {
        /**
         * The benchmark method that executes the task and consumes its result.
         *
         * @param state the benchmark state containing the task
         * @param bh    the Blackhole used to consume results and prevent dead code elimination
         */
        @Benchmark
        @BenchmarkMode(Mode.AverageTime)
        @OutputTimeUnit(TimeUnit.MICROSECONDS)
        @Warmup(iterations = DEFAULT_WARMUP_ITERATIONS)
        @Measurement(iterations = DEFAULT_MEASUREMENT_ITERATIONS)
        public void run(BenchState state, Blackhole bh) {
            bh.consume(state.task.get());
        }
    }

    /**
     * Executes a benchmark with optional warmup control.
     * <p>
     * This method allows dynamic control over warmup iterations. Setting {@code doWarmUp} to
     * {@code false} skips the warmup phase, which can be useful for:
     * <ul>
     * <li>Quick testing or development-time measurements</li>
     * <li>Comparing cold-start vs. warmed-up performance</li>
     * <li>Reducing total benchmark execution time</li>
     * </ul>
     * However, skipping warmup may result in less stable measurements due to JIT compilation variance.
     * </p>
     *
     * @param taskFactory builds the task to be benchmarked
     * @param doWarmUp    if {@code true}, runs {@value #DEFAULT_WARMUP_ITERATIONS} warmup iterations;
     *                    if {@code false}, skips warmup entirely
     * @return average execution time in seconds
     * @throws Exception if the benchmark execution fails
     */
    @Override
    public double run(Supplier<Supplier<?>> taskFactory, boolean doWarmUp) throws Exception {
        pendingFactory = taskFactory;

        Options opt = new OptionsBuilder()
                .include(".*" + BenchmarkRunner.class.getSimpleName() + "\\.run")
                .forks(0)
                .warmupIterations(doWarmUp ? DEFAULT_WARMUP_ITERATIONS : 0)
                .measurementIterations(DEFAULT_MEASUREMENT_ITERATIONS)
                .build();

        Collection<RunResult> results = new Runner(opt).run();

        // JMH score is in microseconds (per @OutputTimeUnit(TimeUnit.MICROSECONDS))
        // Convert to seconds for consistency
        return results.iterator().next().getPrimaryResult().getScore() / 1_000_000.0;
    }
}

