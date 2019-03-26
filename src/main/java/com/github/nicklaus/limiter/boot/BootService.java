package com.github.nicklaus.limiter.boot;

/**
 * boot service for metrics
 *
 * @author weishibai
 * @date 2019/03/25 10:42 PM
 */
public interface BootService {

    void prepare();

    void boot();

    void onComplete();

    void shutdown();

}
