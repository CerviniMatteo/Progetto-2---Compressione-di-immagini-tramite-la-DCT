package com.unimib.assignment.backend.benchmark;

import java.util.function.Supplier;

/**
 * Contract for components that execute micro-benchmarks for lazily-created tasks.
 * <p>The benchmarked workload is provided as a two-level supplier:
 * {@code Supplier<Supplier<?>>}.</p>
 * <ul>
 * <li>Outer supplier: creates a fresh task for a benchmark run/trial.</li>
 * <li>Inner supplier: executes one benchmark operation and returns its result.</li>
 * </ul>
 */
public interface BenchmarkExecutor {

    /**
     * Executes the provided benchmark task and returns the average execution time.
     */
    double run(Supplier<Supplier<?>> taskFactory) throws Exception;
}
