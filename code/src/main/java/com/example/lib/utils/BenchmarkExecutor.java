package com.example.lib.utils;

import java.util.function.Supplier;

/**
 * Abstraction for executing a benchmark task using a benchmarking framework (e.g., JMH).
 * <p>This interface provides two methods for running benchmarks:</p>
 * <ul>
 * <li>{@code run(Supplier)} - runs a benchmark with default warmup settings</li>
 * <li>{@code run(Supplier, boolean)} - runs a benchmark with optional warmup control</li>
 * </ul>
 */
public interface BenchmarkExecutor {
    /**
     * Executes a benchmark factory and returns the average execution time in seconds with optional warmup.
     * <p>
     * This method allows fine-grained control over whether the benchmark should perform warmup iterations.
     * Warmup iterations help stabilize measurements by allowing the JIT compiler to optimize code paths.
     * </p>
     *
     * @param taskFactory builds the task to be benchmarked
     * @param doWarmUp    if {@code true}, performs warmup iterations before measurement;
     *                    if {@code false}, skips warmup for faster execution
     * @return average execution time in seconds
     * @throws Exception if the benchmark execution fails
     */
    double run(Supplier<Supplier<?>> taskFactory, boolean doWarmUp) throws Exception;
}

