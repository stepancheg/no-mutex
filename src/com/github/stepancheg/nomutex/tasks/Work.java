package com.github.stepancheg.nomutex.tasks;

import com.github.stepancheg.nomutex.common.Computation;
import com.github.stepancheg.nomutex.tasks.framework.GreenThread;
import com.github.stepancheg.nomutex.tasks.framework.LockFreeStackWithSize;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Stepan Koltsov
 */
public class Work {

    //private final Queue<BigInteger> workQueue = new ConcurrentLinkedQueue<BigInteger>();
    //private final AtomicInteger workQueueSize = new AtomicInteger(0);
    private final LockFreeStackWithSize<BigInteger> workQueue = new LockFreeStackWithSize<BigInteger>();

    public int getWorkQueueSize() {
        return workQueue.size();
        //return workQueueSize.get();
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Runnable actor = new ActorImpl();
    private final GreenThread thread = new GreenThread(actor, executor);

    final Computation computation = new Computation();


    // actor implementation
    private void run() {
        /*
        for (;;) {
            BigInteger item = workQueue.poll();
            if (item == null)
                return;
            workQueueSize.decrementAndGet();
            computation.update(item);
        }
        */
        List<BigInteger> items = workQueue.dequeueAll();
        for (BigInteger item : items) {
            computation.update(item);
        }

    }

    class ActorImpl implements Runnable {
        @Override
        public void run() {
            Work.this.run();
        }
    }

    public void addWork(BigInteger item) {
        boolean added = workQueue.add(item);
        if (!added)
            throw new AssertionError();

        //workQueueSize.incrementAndGet();

        thread.schedule();
    }

    public void complete() {
        thread.complete();
    }

}
