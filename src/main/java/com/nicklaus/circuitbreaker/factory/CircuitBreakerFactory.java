package com.nicklaus.circuitbreaker.factory;

import java.time.Duration;

import com.nicklaus.circuitbreaker.wrapper.CircuitBreakerFacade;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

/**
 * circuit break factory
 *
 * @author weishibai
 * @date 2019/03/24 9:30 PM
 */
public class CircuitBreakerFactory {

    public static CircuitBreakerConfig createConfig(CircuitBreakerBuilder builder) {
        final CircuitBreakerConfig.Builder result = CircuitBreakerConfig.custom();

        if (builder.threshold() > 0) {
            result.failureRateThreshold(builder.threshold());
        }

        if (builder.closedBufferSize()> 0) {
            result.ringBufferSizeInClosedState(builder.closedBufferSize());
        }

        if (builder.halfOpenBufferSize() > 0) {
            result.ringBufferSizeInHalfOpenState(builder.halfOpenBufferSize());
        }

        if (null != builder.justice()) {
            result.recordFailure(builder.justice());
        }

        if (builder.duration() > 0) {
            result.waitDurationInOpenState(Duration.ofSeconds(builder.duration()));
        }
        return result.build();
    }

    public static CircuitBreakerFacade facade(CircuitBreakerBuilder builder) {
        final CircuitBreakerConfig config = createConfig(builder);
        return new CircuitBreakerFacade(config, CircuitBreakerRegistry.of(config));
    }


}
