package com.github.nicklaus.limiter.boot;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

/**
 * manage boot services
 *
 * @author weishibai
 * @date 2019/03/25 10:45 PM
 */
public class ServiceManager {

    private static final ConcurrentMap<Class<? extends BootService>, BootService> bootServices;

    static {
        bootServices = loadAllServices();
    }

    private static ConcurrentMap<Class<? extends BootService>, BootService> loadAllServices() {
        final ServiceLoader<BootService> loader = ServiceLoader.load(BootService.class
                , Thread.currentThread().getContextClassLoader());
        final Iterator<BootService> iterator = loader.iterator();
        ConcurrentMap<Class<? extends BootService>, BootService> result = Maps.newConcurrentMap();
        iterator.forEachRemaining(bootService -> result.put(bootService.getClass(), bootService));
        return result;
    }

    /**
     * Find a {@link BootService} implementation, which is already started.
     *
     * @param serviceClass class name.
     * @param <T> {@link BootService} implementation class.
     * @return {@link BootService} instance
     */
    public <T extends BootService> T findService(Class<T> serviceClass) {
        //noinspection unchecked
        return (T) bootServices.get(serviceClass);
    }

    public static ServiceManager getInstance() {
        return Initializer.manager;
    }

    private static class Initializer {
        private static ServiceManager manager = new ServiceManager();
    }
}
