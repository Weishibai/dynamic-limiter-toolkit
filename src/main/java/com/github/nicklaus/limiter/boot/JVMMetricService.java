package com.github.nicklaus.limiter.boot;

import static java.lang.Thread.MIN_PRIORITY;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.nicklaus.limiter.boot.jvm.cpu.CPUMetricProvider;
import com.github.nicklaus.limiter.boot.jvm.memory.MemoryMetricProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

/**
 * jvm metric service
 *
 * @author weishibai
 * @date 2019/03/26 2:27 PM
 */
public class JVMMetricService implements BootService, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JVMMetricService.class);

    private static final int CALCULATE_INTERVAL = 2; //sec

    private volatile ScheduledFuture<?> collectMetricFuture;

    /* keep last five metrics to predict trends */
    private LinkedBlockingQueue<JVMMetric> metricQueue;

    private volatile CompleteMetricsListener metricsListener;

    @Override
    public void prepare() {
        metricQueue = Queues.newLinkedBlockingQueue(5);
    }

    @Override
    public void boot() {
        this.collectMetricFuture = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
                .namingPattern("jvm-metrics-%d")
                .daemon(false)
                .priority(MIN_PRIORITY)
                .build())
                .scheduleAtFixedRate(new SafeRunnable(this
                                , e -> LOGGER.error("JVMMetricService produces metrics failure.", e))
                        , 0, CALCULATE_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    public void onComplete() {}

    @Override
    public void shutdown() {
        collectMetricFuture.cancel(true);
    }

    public void addCompleteMetricListener(CompleteMetricsListener listener) {
        this.metricsListener = listener;
    }

    @Override
    public void run() {
        final JVMMetric metric = JVMMetric.newBuilder()
                .currentTime(System.currentTimeMillis())
                .cpu(CPUMetricProvider.getInstance().getCpuMetric())
                .memory(MemoryMetricProvider.getInstance().getMemoryMetric())
                .build();

        if (!metricQueue.offer(metric)) {
            /* current is full and drain to another collector */
            final List<JVMMetric> list = Lists.newLinkedList();
            metricQueue.drainTo(list);

            /* fire listener */
            if (null != metricsListener) {
                metricsListener.onComplete(list);
            }
            metricQueue.offer(metric);
        }
    }
}
