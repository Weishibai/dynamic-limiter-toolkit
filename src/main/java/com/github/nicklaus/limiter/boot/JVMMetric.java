package com.github.nicklaus.limiter.boot;

import com.github.nicklaus.limiter.boot.jvm.cpu.CPU;
import com.github.nicklaus.limiter.boot.jvm.memory.Memory;

/**
 * jvm metric
 *
 * @author weishibai
 * @date 2019/03/26 2:49 PM
 */
public class JVMMetric {

    private CPU cpu;

    private Memory memory;

    private long currentTime;

    public CPU cpu() {
        return cpu;
    }

    public Memory memory() {
        return memory;
    }

    public long currentTime() {
        return currentTime;
    }

    /**
     * consider cpu usage with weight 0.55
     * memory usage with weight 0.4
     * gc count with weight 0.05
     * @return load score
     */
    public double generateScore() {
        return 0.55 * (1.0 - cpu.usagePercent().doubleValue())
                + 0.4 * (1.0 - memory.memoryUsagePercent());
    }

    public JVMMetric(Builder builder) {
        this.cpu = builder.cpu;
        this.memory = builder.memory;
        this.currentTime = builder.currentTime;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private CPU cpu;

        private Memory memory;

        private long currentTime;

        public Builder cpu(CPU cpuMetric) {
            this.cpu = cpuMetric;
            return this;
        }

        public Builder memory(Memory memory) {
            this.memory = memory;
            return this;
        }

        public Builder currentTime(long time) {
            this.currentTime = time;
            return this;
        }

        public JVMMetric build() {
            return new JVMMetric(this);
        }
    }
}
