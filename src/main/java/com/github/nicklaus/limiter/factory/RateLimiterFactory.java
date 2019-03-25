package com.github.nicklaus.limiter.factory;

import java.time.Duration;

import com.github.nicklaus.limiter.wrapper.RateLimiterFacade;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;

/**
 * rate limiter factory
 *
 * @author weishibai
 * @date 2019/03/25 9:35 PM
 */
public class RateLimiterFactory {

    public static RateLimiterConfig createConfig(RateLimiterBuilder builder) {
        final RateLimiterConfig.Builder result = RateLimiterConfig.custom();
        if (builder.period() > 0) {
            result.limitForPeriod(builder.period());
        }

        if (builder.refreshPeriod() > 0) {
            result.limitRefreshPeriod(Duration.ofMillis(builder.refreshPeriod()));
        }

        if (builder.timeoutDuration() > 0) {
            result.timeoutDuration(Duration.ofMillis(builder.timeoutDuration()));
        }
        return result.build();
    }

    public static RateLimiterFacade facade(RateLimiterBuilder builder) {
        final RateLimiterConfig config = createConfig(builder);
        return new RateLimiterFacade(RateLimiterRegistry.of(config), config);
    }
}
