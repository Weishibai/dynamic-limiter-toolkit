package com.github.nicklaus.limiter.boot.jvm.cpu;

import java.lang.management.ManagementFactory;

import com.sun.management.OperatingSystemMXBean;

/**
 * sun cpu calculator
 *
 * @author weishibai
 * @date 2019/03/26 2:16 PM
 */
class SunCpuCalculator extends CPUMetricCalculator {

    private final OperatingSystemMXBean osMBean;

    public SunCpuCalculator(int coreNum) {
        super(coreNum);
        this.osMBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        init();  //start
    }

    @Override
    protected long getCpuTime() {
        return osMBean.getProcessCpuTime();
    }
}
