package com.github.stepancheg.nomutex.tasks.framework;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * @author Stepan Koltsov
 */
public class GreenThread {

    // state
    private final Tasks tasks = new Tasks();
    private volatile boolean requestCompleteSignal = false;
    private final CountDownLatch completeLatch = new CountDownLatch(1);

    private final Runnable actor;
    private final ExecutorService executor;

    public GreenThread(Runnable actor, ExecutorService executor) {
        this.actor = actor;
        this.executor = executor;
    }

    private class RunnableImpl implements Runnable {
        @Override
        public void run() {
            while (tasks.fetchTask()) {
                actor.run();
            }

            if (requestCompleteSignal) {
                completeLatch.countDown();
            }
        }
    }

    private final RunnableImpl runnable = new RunnableImpl();

    public void schedule() {
        if (tasks.addTask()) {
            executor.submit(runnable);
        }
    }

    public void complete() {
        requestCompleteSignal = true;
        schedule();
        try {
            completeLatch.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        executor.shutdown();
    }
}
