package com.github.stepancheg.nomutex;

import com.github.stepancheg.nomutex.mutex.MutexMain;
import com.github.stepancheg.nomutex.tasks.TasksMain;

/**
 * @author Stepan Koltsov
 */
public class Loop {

    public static void main(String[] args) throws Exception {
        for (;;) {
            TasksMain.main(args);
            MutexMain.main(args);
        }
    }

}
