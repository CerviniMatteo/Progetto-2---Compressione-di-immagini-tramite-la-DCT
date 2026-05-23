package com.example.utils;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Helper class to reduce boilerplate when working with SwingWorker.
 * <p>
 * Provides a fluent API for creating and executing SwingWorkers with
 * common error handling patterns already built-in.
 * </p>
 *
 * @param <T> the type of result produced by the worker
 */
public class SwingWorkerHelper<T> {

    private final SwingWorker<T, Void> worker;
    private Consumer<T> successHandler;
    private Consumer<Exception> errorHandler;

    /**
     * Create a new SwingWorkerHelper.
     *
     * @param backgroundTask the function to execute in the background
     */
    public SwingWorkerHelper(Supplier<T> backgroundTask) {
        this.worker = new SwingWorker<>() {
            @Override
            protected T doInBackground(){
                return backgroundTask.get();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    if (successHandler != null) {
                        successHandler.accept(result);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (errorHandler != null) {
                        errorHandler.accept(e);
                    }
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if (errorHandler != null && cause instanceof Exception) {
                        errorHandler.accept((Exception) cause);
                    }
                } catch (Exception e) {
                    if (errorHandler != null) {
                        errorHandler.accept(e);
                    }
                }
            }
        };
    }

    /**
     * Set the handler to call on successful completion.
     *
     * @param handler the success handler
     * @return this for chaining
     */
    public SwingWorkerHelper<T> onSuccess(Consumer<T> handler) {
        this.successHandler = handler;
        return this;
    }

    /**
     * Set the handler to call if an exception occurs.
     *
     * @param handler the error handler
     * @return this for chaining
     */
    public SwingWorkerHelper<T> onError(Consumer<Exception> handler) {
        this.errorHandler = handler;
        return this;
    }

    /**
     * Execute the worker.
     */
    public void execute() {
        worker.execute();
    }

}

