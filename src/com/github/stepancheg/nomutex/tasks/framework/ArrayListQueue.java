package com.github.stepancheg.nomutex.tasks.framework;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Stepan Koltsov
 */
public class ArrayListQueue<T> {

    private final Lock lock = new ReentrantLock();
    private ArrayList<T> arrayList = new ArrayList<>();

    public void enqueue(T t) {
        lock.lock();
        try {
            arrayList.add(t);
        } finally {
            lock.unlock();
        }
    }

    public ArrayList<T> dequeueAll() {
        lock.lock();
        try {
            ArrayList<T> r = arrayList;
            arrayList = new ArrayList<>();
            return r;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return arrayList.size();
        } finally {
            lock.unlock();
        }
    }
}
