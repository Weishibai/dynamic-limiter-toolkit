package com.github.nicklaus.limiter.boot.jvm.memory;

import java.math.BigDecimal;

/**
 * memory info
 *
 * @author weishibai
 * @date 2019/03/26 4:08 PM
 */
public class Memory {

    private boolean heap;

    private long init;

    private long used;

    private long max;

    public Memory(Builder builder) {
        this.heap = builder.heap;
        this.init = builder.init;
        this.used = builder.used;
        this.max = builder.max;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean heap() {
        return heap;
    }

    public long init() {
        return init;
    }

    public long used() {
        return used;
    }

    public long max() {
        return max;
    }

    public double memoryUsagePercent() {
        return BigDecimal.valueOf(used - init).divide(BigDecimal.valueOf(max - init), BigDecimal.ROUND_UP)
                .doubleValue();
    }

    public static class Builder {

        private boolean heap;

        private long init;

        private long used;

        private long max;

        public Builder heap(boolean heap) {
            this.heap = heap;
            return this;
        }

        public Builder init(long init) {
            this.init = init;
            return this;
        }

        public Builder used(long used) {
            this.used = used;
            return this;
        }

        public Builder max(long max) {
            this.max = max;
            return this;
        }

        public Memory build() {
            return new Memory(this);
        }
    }
}
