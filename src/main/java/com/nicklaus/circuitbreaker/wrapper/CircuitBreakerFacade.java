package com.nicklaus.circuitbreaker.wrapper;

import java.util.function.Function;
import java.util.function.Supplier;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;

/**
 * facade
 *
 * @author weishibai
 * @date 2019/03/24 9:41 PM
 */
public class CircuitBreakerFacade {

    private final CircuitBreakerConfig config;

    private final CircuitBreakerRegistry registry;

    public CircuitBreakerFacade(CircuitBreakerConfig config, CircuitBreakerRegistry registry) {
        this.config = config;
        this.registry = registry;
    }

    public CircuitBreakerConfig config() {
        return config;
    }

    public CircuitBreakerRegistry registry() {
        return registry;
    }

    public CircuitBreaker ofCircuitBreaker(String cbName) {
        return registry.circuitBreaker(cbName);
    }

    public <T> CheckedFunction0<T> decorateCheckedSupplier(String cbName, CheckedFunction0<T> supplier) {
        return CircuitBreaker.decorateCheckedSupplier(ofCircuitBreaker(cbName), supplier);
    }

    public <T> Supplier<T> decorateSupplier(String cbName, Supplier<T> supplier) {
        return CircuitBreaker.decorateSupplier(ofCircuitBreaker(cbName), supplier);
    }

    public <T, R> Function<T, R> decorateFunction(String cbName, Function<T, R> function) {
        return CircuitBreaker.decorateFunction(ofCircuitBreaker(cbName), function);
    }

    public <T, R> CheckedFunction1<T, R> decorateCheckedFunction(String cbName, CheckedFunction1<T, R> function) {
        return CircuitBreaker.decorateCheckedFunction(ofCircuitBreaker(cbName), function);
    }

}
