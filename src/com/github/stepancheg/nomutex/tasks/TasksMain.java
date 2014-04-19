package com.github.stepancheg.nomutex.tasks;

import com.github.stepancheg.nomutex.common.Parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Stepan Koltsov
 */
public class TasksMain {

    static {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    public static void main(String[] args) throws Exception {
        System.out.println(TasksMain.class.getSimpleName() + " " + Parameters.describe());

        ExecutorService executor = Executors.newSingleThreadExecutor();

        CounterActor work = new CounterActor(executor);

        List<Thread> threads = new ArrayList<Thread>();

        for (int i = 0; i < Parameters.nThreads; ++i) {
            threads.add(new Thread(new Producer(work, i)));
        }

        long start = System.currentTimeMillis();

        for (Thread thread : threads) {
            thread.start();
        }

        System.out.println("joining producers");

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("waiting for completion");

        work.complete();

        long duration = System.currentTimeMillis() - start;

        System.out.println(work.computation.getSum());
        System.out.println("took " + duration + "ms");

        executor.shutdown();
    }

}
