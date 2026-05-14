package com.example.lib.utils;

import java.util.function.Supplier;

/**
 * Abstraction for executing a benchmark task.
 */
public interface BenchmarkExecutor {

    /**
     * Executes a benchmark factory and returns the average execution time in seconds.
     *
     * @param taskFactory builds the task to be benchmarked
     * @return average time in seconds
     */
    double run(Supplier<Supplier<?>> taskFactory) throws Exception;
}

