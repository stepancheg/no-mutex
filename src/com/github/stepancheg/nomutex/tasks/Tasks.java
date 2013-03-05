package com.github.stepancheg.nomutex.tasks;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Stepan Koltsov
 */
public class Tasks {

    private enum State {
        WAITING,
        RUNNING_NO_TASKS,
        RUNNING_GOT_TASKS,
    }

    private final AtomicReference<State> state = new AtomicReference<State>(State.WAITING);

    public boolean fetchTask() {
        for (;;) {
            State current = state.get();
            switch (current) {
                case RUNNING_GOT_TASKS:
                    if (state.compareAndSet(current, State.RUNNING_NO_TASKS))
                        return true;
                    break;
                case RUNNING_NO_TASKS:
                    if (state.compareAndSet(current, State.WAITING))
                        return false;
                    break;
                default:
                    // WAITING is not possible at this point
                    throw new AssertionError();
            }
        }
    }

    public boolean addTask() {
        for (;;) {
            State current = state.get();
            switch (current) {
                case WAITING:
                case RUNNING_NO_TASKS:
                    if (state.compareAndSet(current, State.RUNNING_GOT_TASKS))
                        return current == State.WAITING;
                    break;
                case RUNNING_GOT_TASKS:
                    return false;
                default:
                    throw new AssertionError();
            }
        }
    }

}
