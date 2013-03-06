package com.github.stepancheg.nomutex.tasks.framework;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Stepan Koltsov
 */
class Tasks {

    private enum State {
        /** actor is not currently running */
        WAITING,
        /** actor is running, and has no more tasks */
        RUNNING_NO_TASKS,
        /** actor is running, but some queues probably updated, actor needs to recheck them */
        RUNNING_GOT_TASKS,
    }

    private final AtomicReference<State> state = new AtomicReference<State>(State.WAITING);

    /**
     * @return <code>true</code> iff we have to recheck queues
     */
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

    /**
     * @return <code>true</code> iff caller have to either schedule task or execute it
     */
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
