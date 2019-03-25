package com.github.nicklaus.circuitbreaker;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.nicklaus.circuitbreaker.factory.CircuitBreakerBuilder;
import com.github.nicklaus.circuitbreaker.factory.CircuitBreakerFactory;
import com.github.nicklaus.service.AService;
import com.github.nicklaus.circuitbreaker.wrapper.CircuitBreakerFacade;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerOpenException;

/**
 * cb test
 *
 * @author weishibai
 * @date 2019/03/24 9:49 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class CircuitBreakerTest {

    private CircuitBreakerFacade facade;

    private AService aService;

    @Before
    public void init() {
        facade = CircuitBreakerFactory.facade(CircuitBreakerBuilder.newBuilder()
                .failureJustice(Throwable.class::isInstance)
                .failureRateThreshold(50)
                .waitDurationInOpenState(1)
                .ringBufferSizeInHalfOpenState(10));

        aService = new AService();
    }


    @Test
    public void testCircuitBreaker1() {
        final CircuitBreaker circuitBreaker = facade.ofCircuitBreaker("cbTest");
        final String result = circuitBreaker.executeSupplier(() -> aService.doA("hello cb"));
        System.out.println(result);
    }

    @Test
    public void testCircuitBreakerDecorator() {
        final Function<String, String> decorateFunction = facade.decorateFunction("cbTest", aService::ping);
        System.out.println(decorateFunction.apply("hello cb"));
    }

    @Test
    public void testCBFailure() {
        final Supplier<String> supplier = facade.decorateSupplier("cbTest", aService::doWithRandomFailure);

        for (int i = 0; i < 500; i++) {
            try {
                System.out.println("i: " + i + " - result: " + supplier.get());
            } catch (CircuitBreakerOpenException e) {
                System.out.println("cb occur i: " + i);
            } catch (RuntimeException businessException) {
                System.out.println("ig biz exception i: " + i);
            }
        }
    }


}
