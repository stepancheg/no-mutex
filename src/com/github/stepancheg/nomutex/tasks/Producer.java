package com.github.stepancheg.nomutex.tasks;

import com.github.stepancheg.nomutex.common.Parameters;

import java.math.BigInteger;

/**
 * @author Stepan Koltsov
 */
public class Producer implements Runnable {

    private final Work work;

    public Producer(Work work) {
        this.work = work;
    }

    @Override
    public void run() {
        for (int i = 0; i < Parameters.EMIT_BY_THREAD; ++i) {
            if (i % (100 * 1000) == 0) {
                while (work.getWorkQueueSize() > 100 * 1000) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            work.addWork(BigInteger.valueOf(i));
        }

        System.out.println("producer completed");
    }
}
