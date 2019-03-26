package com.github.nicklaus.limiter.boot.jvm.cpu;

import java.lang.management.ManagementFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * cpu provider
 *
 * @author weishibai
 * @date 2019/03/26 2:20 PM
 */
public class CPUMetricProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CPUMetricProvider.class);

    private CPUMetricCalculator cpuMetricCalculator;

    private CPUMetricProvider() {
        final int coreNum = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
        try {
            this.cpuMetricCalculator = (CPUMetricCalculator) CPUMetricProvider.class.getClassLoader()
                    .loadClass("com.github.nicklaus.limiter.boot.jvm.cpu.SunCpuCalculator")
                    .getConstructor(int.class).newInstance(coreNum);
        } catch (Exception e) {
            LOGGER.error("only support calculate cpu metric in sum jvm platform.");
        }
    }

    public CPU getCpuMetric() {
        return cpuMetricCalculator.getCPUMetric();
    }

    public static CPUMetricProvider getInstance() {
        return Initializer.provider;
    }

    private static class Initializer {
        private static CPUMetricProvider provider = new CPUMetricProvider();
    }
}
