package com.github.nicklaus.limiter.boot;

/**
 * runnable with try catch
 *
 * @author weishibai
 * @date 2019/03/26 2:37 PM
 */
public class SafeRunnable implements Runnable {

    private final Runnable runnable;

    private CallbackWhenException callback;

    public SafeRunnable(Runnable runnable, CallbackWhenException callback) {
        this.runnable = runnable;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Throwable e) {
            callback.handle(e);
        }
    }

    public interface CallbackWhenException {
        void handle(Throwable t);
    }
}
