package com.diegomfv.android.realestatemanager.data;

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
public class AppExecutors {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    /**
     * Method to get an entry point to AppExecutors.
     */
    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    /**
     * DiskIO is a Single Thread Executor (see above).
     * It ensures that our database transactions are done in order so we do not have
     * race conditions
     */
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * The mainThread executor uses the "MainThreadExecutor" class which essentially will pause
     * executors using a handle associated with the main looper. When we are in an activity, we don't need
     * this main thread executor because we can use the run on UI Thread method. When we are in a
     * different class and we do not have the runOnUIThread() method, we can access the main thread
     * using this executor (It is difficult to imagine an example when we need this, see example
     * UDACITY, Android Development, Android Architecture Components
     */

    public Executor mainThread() {
        return mainThread;
    }

    /**
     * The networkIO Executor is a pool of three threads. This allows us to run different
     * network calls simultaneously while controlling the number or threads that we have.
     */
    public Executor networkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
