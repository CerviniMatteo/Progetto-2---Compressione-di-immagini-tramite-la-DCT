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
 * JMH-based implementation of {@link BenchmarkExecutor}.
 */
public class JmhBenchmarkExecutor implements BenchmarkExecutor {

    private static volatile Supplier<Supplier<?>> pendingFactory;

    @State(Scope.Benchmark)
    public static class BenchState {
        public Supplier<?> task;

        @Setup(Level.Trial)
        public void setup() {
            task = pendingFactory.get();
        }
    }

    public static class BenchmarkRunner {
        @Benchmark
        @BenchmarkMode(Mode.AverageTime)
        @OutputTimeUnit(TimeUnit.NANOSECONDS)
        @Warmup(iterations = 1)
        @Measurement(iterations = 3)
        public void run(BenchState state, Blackhole bh) {
            bh.consume(state.task.get());
        }
    }

    @Override
    public double run(Supplier<Supplier<?>> taskFactory) throws Exception {
        pendingFactory = taskFactory;

        Options opt = new OptionsBuilder()
                .include(".*" + BenchmarkRunner.class.getSimpleName() + "\\.run")
                .forks(0)
                .build();

        Collection<RunResult> results = new Runner(opt).run();

        // JMH score is in nanoseconds because of @OutputTimeUnit(TimeUnit.NANOSECONDS)
        return results.iterator().next().getPrimaryResult().getScore() / 1_000_000_000.0;
    }
}

