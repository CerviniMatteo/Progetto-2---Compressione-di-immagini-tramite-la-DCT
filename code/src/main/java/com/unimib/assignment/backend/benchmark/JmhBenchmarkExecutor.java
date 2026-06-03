package com.unimib.assignment.backend.benchmark;

import com.unimib.assignment.backend.constants.BenchmarkConstants;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
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
 * {@link BenchmarkExecutor} implementation based on JMH (Java Microbenchmark Harness).
 * <p>This class measures average execution time by repeatedly invoking a supplied task
 * under JMH-managed warmup and measurement iterations.</p>
 * <p><b>Timing unit:</b> this implementation returns JMH's score in microseconds</p>
 */
public class JmhBenchmarkExecutor implements BenchmarkExecutor {

    /** Number of warmup iterations used to stabilize JIT-optimized execution. */
    private static final int DEFAULT_WARMUP_ITERATIONS = 3;

    /** Number of measured iterations used to compute the reported average score. */
    private static final int DEFAULT_MEASUREMENT_ITERATIONS = 5;

    /** Temporary handoff from caller thread to JMH worker threads.
     * <p>JMH setup/execution occurs on framework-managed threads; therefore
     * the task factory is exposed through a static volatile field to guarantee visibility.
     * It is always cleared in a {@code finally} block after each run.</p>
     */
    private static volatile Supplier<Supplier<?>> pendingFactory;

    /** Benchmark-scoped state object created by JMH.
     * <p>It stores the per-run task instance obtained from {@link #pendingFactory}.</p>
     */
    @State(Scope.Benchmark)
    public static class BenchState {

        /** Task supplier invoked by each benchmark operation. */
        public Supplier<?> task;

        /** Initializes state before each JMH trial.
         * <p>Reads the task supplier produced by the caller-provided factory.</p>
         */
        @Setup(Level.Trial)
        public void setup() {
            task = pendingFactory.get();
        }
    }

    /** JMH benchmark entry point.
    * <p>The method executes one task operation and feeds its result to a
    * {@link Blackhole} to prevent dead-code elimination.</p>
    */
    public static class BenchmarkRunner {

        /** Executes one measured benchmark operation.
        * <p>Exceptions are intentionally not caught: if the workload fails,
        * JMH aborts and may return no results; the outer {@code run(...)} method
        * converts that condition into a {@link CancellationException}.</p>
        * @param state benchmark state containing the task to execute
        * @param bh sink used to consume results safely during microbenchmarking
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
     * Runs a JMH benchmark session for the provided task factory.
     * <p>Configuration used:</p>
     * <ul>
     * <li>No forking ({@code forks(0)}), so execution stays in-process.</li>
     * <li>Fixed warmup and measurement iteration counts from class constants.</li>
     * <li>Benchmark inclusion by simple name pattern from {@link BenchmarkConstants}.</li>
     * </ul>
     * @param taskFactory factory that creates the task supplier to benchmark
     * @return average execution time score in microseconds
     * @throws CancellationException if JMH returns no results (aborted benchmark)
     * @throws Exception if JMH setup or execution fails for other reasons
     */
    @Override
    public double run(Supplier<Supplier<?>> taskFactory) throws Exception {

        Options opt = new OptionsBuilder()
                // Format the JMH include regex with the benchmark class name.
                // e.g. ".*BenchmarkRunner\\.run" matches any fully-qualified name ending with "BenchmarkRunner.run"
                .include(String.format(
                        BenchmarkConstants.JMH_BENCHMARK_INCLUDE_TEMPLATE,
                        BenchmarkRunner.class.getSimpleName()))
                .forks(0)
                .warmupIterations(DEFAULT_WARMUP_ITERATIONS)
                .measurementIterations(DEFAULT_MEASUREMENT_ITERATIONS)
                .build();

        Collection<RunResult> results;
        try {
            pendingFactory = taskFactory;
            results = new Runner(opt).run();
        } finally {
            // Clear the shared factory after this run so it cannot be reused
            // accidentally in later benchmarks and so captured objects can be garbage-collected.
            pendingFactory = null;
        }

        if (results.isEmpty()) {
            throw new CancellationException(BenchmarkConstants.BENCHMARK_ABORTED_NO_RESULTS);
        }
        return results.iterator().next().getPrimaryResult().getScore();
    }
}