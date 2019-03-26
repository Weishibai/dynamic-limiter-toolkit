package com.github.nicklaus.limiter.boot.jvm.cpu;

import java.math.BigDecimal;

/**
 * cpu metric accessor
 *
 * @author weishibai
 * @date 2019/03/26 2:09 PM
 */
abstract class CPUMetricCalculator {

    private long lastCPUTimeNs;

    private long lastSampleTimeNs;

    private final int cpuCoreNum;

    public CPUMetricCalculator(int cpuCoreNum) {
        this.cpuCoreNum = cpuCoreNum;
    }

    protected void init() {
        lastCPUTimeNs = getCpuTime();
        this.lastSampleTimeNs = System.nanoTime();
    }

    protected abstract long getCpuTime();

    public CPU getCPUMetric() {
        long cpuTime = getCpuTime();
        long cpuCost = cpuTime - lastCPUTimeNs;
        long now = System.nanoTime();
        return new CPU(BigDecimal.valueOf(cpuCost * 1.0d / ((now - lastSampleTimeNs) * cpuCoreNum)));
    }
}
