package com.example.lib.utils;

import com.example.assignment.constants.BenchmarkConstants;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * JMH-based (Java Microbenchmark Harness) implementation of {@link BenchmarkExecutor}.
 * <p>
 * This class uses the OpenJDK JMH framework to perform accurate microbenchmarking. It measures
 * the average execution time of a task by running it multiple times with configurable warmup
 * and measurement iterations.
 * </p>
 * <p>Key features:</p>
 * <ul>
 * <li>Configurable warmup iterations to let the JIT compiler optimize code</li>
 * <li>Multiple measurement iterations to reduce noise and improve accuracy</li>
 * <li>No forking to keep measurements in-process for faster execution</li>
 * <li>Time measurement in microseconds for fine-grained precision</li>
 * </ul>
 *
 * @see BenchmarkExecutor
 */
public class JmhBenchmarkExecutor implements BenchmarkExecutor {

    /** Default number of warmup iterations */
    private static final int DEFAULT_WARMUP_ITERATIONS = 3;

    /** Default number of measurement iterations */
    private static final int DEFAULT_MEASUREMENT_ITERATIONS = 5;

    /**
     * Shared static volatile field used to pass the task factory to JMH worker threads.
     * <p>
     * A ThreadLocal cannot be used here because JMH's @Setup methods run on internal
     * worker threads that are different from the thread that calls {@code run()}.
     * Since benchmarks are executed sequentially (one at a time), a static volatile
     * field is safe and visible across all threads without race conditions.
     * Always cleared in a finally block after each run.
     * </p>
     */
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
         * Initializes the task from the shared pending factory.
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
         * <p>
         * Any exception thrown by the task (including {@link CancellationException}) is
         * intentionally left to propagate. JMH will treat the iteration as a failure and
         * stop the benchmark session, returning no results. The caller's {@code run()} method
         * detects the empty result set and re-throws {@link CancellationException} for uniform
         * handling upstream.
         * </p>
         * <p>
         * <strong>Do not catch exceptions here.</strong> Catching them would cause JMH to
         * silently retry the failed iteration indefinitely, flooding the log.
         * </p>
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
     * The task factory is stored in a shared {@code static volatile} field for the duration
     * of the run and cleared in a {@code finally} block to prevent stale state from affecting
     * subsequent runs. If JMH returns no results (e.g. because the workload threw a
     * {@link CancellationException}), this method re-throws a {@link CancellationException}
     * so callers can handle cancellation uniformly.
     * </p>
     *
     * @param taskFactory builds the task to be benchmarked
     * @param doWarmUp    if {@code true}, runs {@value #DEFAULT_WARMUP_ITERATIONS} warmup iterations;
     *                    if {@code false}, skips warmup entirely
     * @return average execution time in seconds
     * @throws CancellationException if the benchmark was aborted and produced no results
     * @throws Exception             if the benchmark execution fails for another reason
     */
    @Override
    public double run(Supplier<Supplier<?>> taskFactory, boolean doWarmUp) throws Exception {

        Options opt = new OptionsBuilder()
                .include(String.format(BenchmarkConstants.JMH_BENCHMARK_INCLUDE_TEMPLATE, BenchmarkRunner.class.getSimpleName()))
                .forks(0)
                .warmupIterations(doWarmUp ? DEFAULT_WARMUP_ITERATIONS : 0)
                .measurementIterations(DEFAULT_MEASUREMENT_ITERATIONS)
                .build();

        Collection<RunResult> results;
        try {
            pendingFactory = taskFactory;
            results = new Runner(opt).run();
        } finally {
            pendingFactory = null; // always clear to prevent stale lambda on reuse
        }

        if (results.isEmpty()) {
            throw new CancellationException(BenchmarkConstants.BENCHMARK_ABORTED_NO_RESULTS);
        }

        // JMH score is in microseconds (per @OutputTimeUnit(TimeUnit.MICROSECONDS))
        // Convert to seconds for consistency
        return results.iterator().next().getPrimaryResult().getScore() / 1_000_000.0;
    }
}