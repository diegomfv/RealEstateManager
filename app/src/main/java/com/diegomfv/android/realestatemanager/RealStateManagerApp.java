package com.diegomfv.android.realestatemanager;

import android.app.Application;
import android.util.Log;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */
public class RealStateManagerApp extends Application {

    private static final String TAG = RealStateManagerApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: called!");




    }
}
