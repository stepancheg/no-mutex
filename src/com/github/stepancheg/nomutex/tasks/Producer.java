package com.github.stepancheg.nomutex.tasks;

import com.github.stepancheg.nomutex.common.Parameters;

/**
 * @author Stepan Koltsov
 */
public class Producer implements Runnable {

    private final CounterActor work;
    private final int no;

    public Producer(CounterActor work, int no) {
        this.work = work;
        this.no = no;
    }

    @Override
    public void run() {
        for (int i = 0; i < Parameters.EMIT_BY_THREAD; ++i) {
            if (i % (100 * 1000) == 0) {
                // make sure queue is not overflowed
                // this hack is for test only
                while (work.getQueueSize(no) > 100 * 1000) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            work.addWork(no, Parameters.getNumber(i));
        }

        System.out.println("producer completed");
    }
}
