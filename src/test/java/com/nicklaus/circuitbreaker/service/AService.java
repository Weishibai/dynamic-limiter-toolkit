package com.nicklaus.circuitbreaker.service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * test service
 *
 * @author weishibai
 * @date 2019/03/24 10:00 PM
 */
public class AService {

    public String doA(String input) {
        return "receive " + input;
    }

    public String ping(String input) {
        return input;
    }

    public String doWithRandomFailure() {
        final int result = ThreadLocalRandom.current().nextInt(100);
        if (result >= 0 && result <= 79) {
            throw new RuntimeException("execute failed");
        }
        return "success execute";
    }
}
