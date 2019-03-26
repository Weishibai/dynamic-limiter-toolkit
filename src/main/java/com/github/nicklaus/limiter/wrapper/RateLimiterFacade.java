package com.github.nicklaus.limiter.wrapper;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nicklaus.limiter.boot.BootService;
import com.github.nicklaus.limiter.boot.JVMMetric;
import com.github.nicklaus.limiter.boot.JVMMetricService;
import com.github.nicklaus.limiter.boot.ServiceManager;
import com.google.common.collect.Sets;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedFunction1;

/**
 * rate limiter facade
 * support dynamic and normal version
 *
 * @author weishibai
 * @date 2019/03/25 9:48 PM
 */
public class RateLimiterFacade implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterFacade.class);

    private final RateLimiterRegistry registry;

    private volatile RateLimiterConfig defaultConfig;

    private volatile double lastDynamicScore;

    private BootService metricService;

    private final Set<String> usedKeys;

    private final boolean dynamic;

    private double maxRate;

    private double minRate;

    public RateLimiterFacade(final RateLimiterRegistry registry, RateLimiterConfig defaultConfig, boolean dynamic) {
        this.registry = registry;
        this.defaultConfig = defaultConfig;
        this.usedKeys = Sets.newConcurrentHashSet();
        this.dynamic = dynamic;

        if (dynamic) {
            this.metricService = ServiceManager.getInstance().findService(JVMMetricService.class);
            this.maxRate = 1.2d;
            this.minRate = 0.8d;

            /* prepare and start */
            metricService.prepare();
            metricService.boot();

            /* add listener */
            ((JVMMetricService) metricService).addCompleteMetricListener(currentMetrics -> {
            /* calculate new config builder */
                final int previousPeriodLimit = defaultConfig.getLimitForPeriod();
                final Duration previousTimeoutDuration = defaultConfig.getTimeoutDuration();

                final double currentDynamicScore = calculateAvgScore(currentMetrics);
                if (lastDynamicScore <= 0.0d) {
                    lastDynamicScore = currentDynamicScore;
                    return;
                }

                double rate = currentDynamicScore / lastDynamicScore;
                rate = rate > 1.0d ? Math.min(rate, maxRate) : Math.max(rate, minRate);
                LOGGER.info("update score from {} to {}", lastDynamicScore, currentDynamicScore);

                final int currentPeriodLimit = (int) (previousPeriodLimit * rate + 1);
                /* shrunk wait time only when load is higher than last time */
                final long currentTimeoutNanos = rate < 1.0d
                        ? (long) ((double) previousTimeoutDuration.getNano() / rate + 1) : previousTimeoutDuration.getNano();

                lastDynamicScore = lastDynamicScore * rate;
                updateUsedConfig(currentPeriodLimit, Duration.ofNanos(currentTimeoutNanos));
                LOGGER.info("update rate limiter config from pe{} timeout nanos {} to {} timeout nanos {}"
                        , previousPeriodLimit, previousTimeoutDuration, currentPeriodLimit, currentTimeoutNanos);
            });
        }
    }

    public boolean dynamic() {
        return dynamic;
    }

    private double calculateAvgScore(Collection<JVMMetric> metrics) {
        return metrics.stream()
                .mapToDouble(JVMMetric::generateScore)
                .average()
                .getAsDouble();
    }

    public RateLimiterRegistry registry() {
        return registry;
    }

    public RateLimiterConfig config() {
        return defaultConfig;
    }

    public RateLimiter ofRateLimiter(String rlName) {
        usedKeys.add(rlName);
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

    public void updateConfig(String rlName, int limitForPeriod, Duration timeoutDuration) {
        final RateLimiter rateLimiter = ofRateLimiter(rlName);
        rateLimiter.changeLimitForPeriod(limitForPeriod);
        rateLimiter.changeTimeoutDuration(timeoutDuration);
    }

    public void updateUsedConfig(int limitForPeriod, Duration timeoutDuration) {
        if (CollectionUtils.isNotEmpty(usedKeys)) {
            usedKeys.forEach(usedKey -> updateConfig(usedKey, limitForPeriod, timeoutDuration));
        }
        /* update default config */
        defaultConfig = RateLimiterConfig.custom()
                .limitForPeriod(limitForPeriod)
                .timeoutDuration(timeoutDuration)
                .limitRefreshPeriod(defaultConfig.getLimitRefreshPeriod())
                .build();
    }

    @Override
    public void close() throws IOException {
        if (dynamic && null != metricService) {
            metricService.shutdown();
        }
    }
}
