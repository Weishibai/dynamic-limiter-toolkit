package com.nicklaus.circuitbreaker.factory;

import java.util.function.Predicate;

/**
 * curcuit breaker builder
 *
 * @author weishibai
 * @date 2019/03/24 8:28 PM
 */
public class CircuitBreakerBuilder {

    private int failureRateThreshold;

    private int waitDurationInOpenState; //seconds

    private int ringBufferSizeInHalfOpenState;

    private int ringBufferSizeInClosedState;

    private Predicate<? super Throwable> failureJustice;

    public int threshold() {
        return failureRateThreshold;
    }

    public int duration() {
        return waitDurationInOpenState;
    }

    public int halfOpenBufferSize() {
        return ringBufferSizeInHalfOpenState;
    }

    public int closedBufferSize() {
        return failureRateThreshold;
    }

    public Predicate<? super Throwable> justice() {
        return failureJustice;
    }

    public static CircuitBreakerBuilder newBuilder() {
        return new CircuitBreakerBuilder();
    }

    public CircuitBreakerBuilder failureRateThreshold(int threshold) {
        this.failureRateThreshold = threshold;
        return this;
    }

    public CircuitBreakerBuilder waitDurationInOpenState(int duration) {
        this.waitDurationInOpenState = duration;
        return this;
    }

    public CircuitBreakerBuilder ringBufferSizeInHalfOpenState(int halfOpenBufferSize) {
        this.ringBufferSizeInClosedState = halfOpenBufferSize;
        return this;
    }

    public CircuitBreakerBuilder ringBufferSizeInClosedState(int closedBufferSize) {
        this.ringBufferSizeInClosedState = closedBufferSize;
        return this;
    }

    public CircuitBreakerBuilder failureJustice(Predicate<? super Throwable> failureJustice) {
        this.failureJustice = failureJustice;
        return this;
    }
}
