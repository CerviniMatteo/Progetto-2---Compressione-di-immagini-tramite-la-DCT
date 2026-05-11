package com.example.lib.utils;

import java.util.function.Consumer;

/**
 * A simple generic observable container implementing the observer pattern.
 * <p>
 * This class allows a single subscriber to be notified whenever a new value is set.
 * The subscriber is invoked immediately when {@link #set(Object)} is called with a new value.
 * </p>
 * <p>
 * Note: This implementation supports only a single listener. If multiple listeners are required,
 * consider using a more complete event bus or reactive library.
 * </p>
 *
 * @param <T> the type of value held by this observable
 */
public class Observable<T> {

    /** The listener to be notified when the value changes. */
    private Consumer<T> listener;

    /** The current value held by this observable. */
    private T value;

    /**
     * Registers a listener to be called whenever the value is updated.
     * <p>
     * If a listener is already registered, it will be replaced by the new one.
     * The listener is invoked immediately upon calls to {@link #set(Object)}.
     * </p>
     *
     * @param listener the {@link Consumer} function to invoke when the value changes;
     *                 may be {@code null} to deregister the current listener
     */
    public void subscribe(Consumer<T> listener) {
        this.listener = listener;
    }

    /**
     * Sets the value and notifies the registered listener, if any.
     * <p>
     * The listener's {@link Consumer#accept} method is called with the new value,
     * provided a listener has been registered via {@link #subscribe}.
     * </p>
     *
     * @param value the new value to set and broadcast
     */
    public void set(T value) {
        this.value = value;

        if (listener != null) {
            listener.accept(value);
        }
    }

    /**
     * Returns the current value held by this observable.
     *
     * @return the current value, or {@code null} if no value has been set
     */
    public T get() {
        return value;
    }
}