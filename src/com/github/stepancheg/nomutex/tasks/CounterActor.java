package com.github.stepancheg.nomutex.tasks;

import com.github.stepancheg.nomutex.common.Computation;
import com.github.stepancheg.nomutex.tasks.framework.ActorRunner;
import com.github.stepancheg.nomutex.tasks.framework.ArrayListQueue;
import com.github.stepancheg.nomutex.tasks.framework.LockFreeStackWithSize;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Simple version of this actor is {@link CounterSimpleActor}.
 *
 * @author Stepan Koltsov
 */
public class CounterActor implements Runnable {

    final Computation computation = new Computation();

    private List<ArrayListQueue<BigInteger>> queues = new ArrayList<>();
    {
        for (int i = 0; i < 10; ++i) {
            queues.add(new ArrayListQueue<>());
        }
    }

    public ArrayListQueue<BigInteger> getQueue(int i) {
        return queues.get(i);
    }

    private final ActorRunner runner;

    /**
     * @param executor to execute this actor
     */
    public CounterActor(Executor executor) {
        this.runner = new ActorRunner(this, executor);
    }

    @Override
    public void run() {
        for (ArrayListQueue<BigInteger> queue : queues) {
            for (BigInteger request : queue.dequeueAll()) {
                computation.update(request);
            }
        }
    }

    /**
     * Add task for this actor.
     */
    public void addWork(int queue, BigInteger item) {
        getQueue(queue).enqueue(item);

        //runner.scheduleHereAtMostOnce();
        runner.schedule();
    }

    public void complete() {
        runner.complete();
    }

    public int getQueueSize(int no) {
        return getQueue(no).size();
    }
}
