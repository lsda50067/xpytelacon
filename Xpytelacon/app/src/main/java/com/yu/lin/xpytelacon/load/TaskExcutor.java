package com.yu.lin.xpytelacon.load;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hayashisakai on 30/10/2017.
 */

public class TaskExcutor {
    private  static TaskExcutor mInstance = null;
    private  static ExecutorService mExcutor = null;

    private TaskExcutor () {
        mExcutor = (ExecutorService) Executors.newCachedThreadPool();
    }

    public  static TaskExcutor getInstance() {
        if(mInstance == null){
            mInstance = new TaskExcutor();
        }
        return mInstance;
    }

    public ExecutorService getExcutor () {
        return mExcutor;
    }
}
