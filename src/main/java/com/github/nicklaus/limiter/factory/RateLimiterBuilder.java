package com.github.nicklaus.limiter.factory;

/**
 * rate limiter builder
 *
 * @author weishibai
 * @date 2019/03/25 9:20 PM
 */
public class RateLimiterBuilder {

    private int limitRefreshPeriod; //millis

    private int limitForPeriod; //millis

    private int timeoutDuration; //millis

    public int refreshPeriod() {
        return limitRefreshPeriod;
    }

    public int period() {
        return limitForPeriod;
    }

    public int timeoutDuration() {
        return timeoutDuration;
    }

    public static RateLimiterBuilder newBuilder() {
        return new RateLimiterBuilder();
    }

    public RateLimiterBuilder limitRefreshPeriod(int millisRefreshPeriod) {
        this.limitRefreshPeriod = millisRefreshPeriod;
        return this;
    }

    public RateLimiterBuilder limitForPeriod(int millisPeriod) {
        this.limitForPeriod = millisPeriod;
        return this;
    }

    public RateLimiterBuilder timeoutDuration(int timeoutMillis) {
        this.timeoutDuration = timeoutMillis;
        return this;
    }



}
