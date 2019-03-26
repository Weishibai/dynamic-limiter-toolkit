package com.github.nicklaus.limiter.boot.jvm.memory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * memory metric provider
 *
 * @author weishibai
 * @date 2019/03/26 4:06 PM
 */
public class MemoryMetricProvider {

    private final MemoryMXBean memoryMXBean;

    private MemoryMetricProvider() {
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    /* tmp return heap usage only */
    public Memory getMemoryMetric() {
        final MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        return Memory.newBuilder()
                .heap(true)
                .init(heapMemoryUsage.getInit())
                .used(heapMemoryUsage.getUsed())
                .max(heapMemoryUsage.getMax())
                .build();
    }

    public static MemoryMetricProvider getInstance() {
        return Initializer.provider;
    }

    private static class Initializer {
        private static MemoryMetricProvider provider = new MemoryMetricProvider();
    }
}
