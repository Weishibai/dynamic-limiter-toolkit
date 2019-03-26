package com.github.nicklaus.ratelimiter;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.nicklaus.limiter.factory.RateLimiterBuilder;
import com.github.nicklaus.limiter.factory.RateLimiterFactory;
import com.github.nicklaus.limiter.wrapper.RateLimiterFacade;
import com.github.nicklaus.service.AService;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;

/**
 * rate limiter test
 *
 * @author weishibai
 * @date 2019/03/25 9:53 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class RateLimiterTest {

    private RateLimiterFacade facade;

    private RateLimiterFacade dynamicFacade;

    private AService aService;

    @Before
    public void init() {
        facade = RateLimiterFactory.facade(RateLimiterBuilder.newBuilder()
                .limitForPeriod(10)
                .limitRefreshPeriod(1000)
                .timeoutDuration(25));

        dynamicFacade = RateLimiterFactory.facade(RateLimiterBuilder.newBuilder()
                .limitForPeriod(10)
                .limitRefreshPeriod(1000)
                .timeoutDuration(25), true);

        aService = new AService();
    }

//    @Test
    public void testRateLimiter() {
        final RateLimiter rateLimiter = facade.ofRateLimiter("rlTest");
        final CheckedRunnable runnable = RateLimiter.decorateCheckedRunnable(rateLimiter, () -> aService.doA("test"));

        for (int i = 0; i < 100; i++) {
            Try.run(runnable)
                    .onSuccess(v -> System.out.println("execute success"))
                    .onFailure(e -> System.out.println(e.getMessage()));
        }
    }

//    @Test
    public void testDynamicRateLimiter() {
        final RateLimiter rateLimiter = dynamicFacade.ofRateLimiter("rlTest");

        final CheckedRunnable runnable = RateLimiter.decorateCheckedRunnable(rateLimiter, () -> aService.doA("test"));

        for (int i = 0; i < 1000; i++) {
            Try.run(runnable)
                    .onSuccess(v -> System.out.println("execute success"))
                    .onFailure(e -> System.out.println(e.getMessage()));
        }

    }

}
