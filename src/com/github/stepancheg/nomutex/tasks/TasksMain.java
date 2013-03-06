package com.github.stepancheg.nomutex.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stepan Koltsov
 */
public class TasksMain {

    public static void main(String[] args) throws Exception {
        System.out.println(TasksMain.class.getSimpleName());

        Work work = new Work();

        List<Thread> threads = new ArrayList<Thread>();

        for (int i = 0; i < 2; ++i) {
            threads.add(new Thread(new Producer(work)));
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
        System.out.print("took " + duration + "ms");
    }

}
