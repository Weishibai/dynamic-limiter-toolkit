package com.github.nicklaus.limiter.boot.jvm.cpu;

import java.math.BigDecimal;

/**
 * cpu metrics
 *
 * @author weishibai
 * @date 2019/03/26 2:12 PM
 */
public class CPU {

    private BigDecimal usagePercent;

    public CPU(BigDecimal usagePercent) {
        this.usagePercent = usagePercent;
    }

    public BigDecimal usagePercent() {
        return usagePercent;
    }

}
