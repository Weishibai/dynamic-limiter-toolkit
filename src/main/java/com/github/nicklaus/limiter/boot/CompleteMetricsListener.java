package com.github.nicklaus.limiter.boot;

import java.util.Collection;

/**
 * complete metric listener
 *
 * @author weishibai
 * @date 2019/03/26 3:06 PM
 */
public interface CompleteMetricsListener {

    void onComplete(Collection<JVMMetric> currentMetrics);

}
