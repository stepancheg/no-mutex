package com.github.stepancheg.nomutex.tasks;

import com.github.stepancheg.nomutex.common.Computation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Stepan Koltsov
 */
public class Work {

    private final Tasks tasks = new Tasks();

    //private final Queue<BigInteger> workQueue = new ArrayBlockingQueue<BigInteger>(200000);
    private final LockFreeStack<BigInteger> workQueue = new LockFreeStack<BigInteger>();
    //private final AtomicInteger workQueueSize = new AtomicInteger(0);

    public int getWorkQueueSize() {
        return workQueue.size();
        //return workQueueSize.get();
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    final Computation computation = new Computation();

    private volatile boolean requestCompleteSignal = false;
    private final CountDownLatch completeLatch = new CountDownLatch(1);

    private void run() {
        /*
        for (;;) {
            BigInteger item = workQueue.poll();
            if (item == null)
                return;
            //workQueueSize.decrementAndGet();
            computation.update(item);
        }
        */
        List<BigInteger> items = workQueue.dequeueAll();
        for (BigInteger item : items) {
            computation.update(item);
        }
    }

    class RunnableImpl implements Runnable {

        @Override
        public void run() {
            while (tasks.fetchTask()) {
                Work.this.run();
            }
            if (requestCompleteSignal) {
                completeLatch.countDown();
            }
        }
    }

    private final RunnableImpl runnable = new RunnableImpl();

    private void schedule() {
        if (tasks.addTask()) {
            executor.submit(runnable);
        }
    }

    public void addWork(BigInteger item) {
        boolean added = workQueue.add(item);
        if (!added)
            throw new AssertionError();

        //workQueueSize.incrementAndGet();

        schedule();
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
