package com.github.nicklaus.limiter.wrapper;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;

/**
 * rate limiter facade
 *
 * @author weishibai
 * @date 2019/03/25 9:48 PM
 */
public class RateLimiterFacade {

    private final RateLimiterRegistry registry;

    private final RateLimiterConfig defaultConfig;

    public RateLimiterFacade(final RateLimiterRegistry registry, final RateLimiterConfig defaultConfig) {
        this.registry = registry;
        this.defaultConfig = defaultConfig;
    }

    public RateLimiterRegistry registry() {
        return registry;
    }

    public RateLimiterConfig config() {
        return defaultConfig;
    }

    public RateLimiter ofRateLimiter(String rlName) {
        return registry.rateLimiter(rlName, defaultConfig);
    }

    public <T> CheckedFunction0<T> decorateCheckedSupplier(String clName, CheckedFunction0<T> supplier) {
        return RateLimiter.decorateCheckedSupplier(ofRateLimiter(clName), supplier);
    }

    public <T> Supplier<T> decorateSupplier(String clName, Supplier<T> supplier) {
        return RateLimiter.decorateSupplier(ofRateLimiter(clName), supplier);
    }

    public <T, R> Function<T, R> decorateFunction(String cbName, Function<T, R> function) {
        return RateLimiter.decorateFunction(ofRateLimiter(cbName), function);
    }

    public <T, R> CheckedFunction1<T, R> decorateCheckedFunction(String cbName, CheckedFunction1<T, R> function) {
        return RateLimiter.decorateCheckedFunction(ofRateLimiter(cbName), function);
    }

    public void updateConfig(String rlName, int limitForPeriod, int timeoutDuration) {
        final RateLimiter rateLimiter = ofRateLimiter(rlName);
        rateLimiter.changeLimitForPeriod(limitForPeriod);
        rateLimiter.changeTimeoutDuration(Duration.ofMillis(timeoutDuration));
    }

}
