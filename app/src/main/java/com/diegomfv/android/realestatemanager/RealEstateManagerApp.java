package com.diegomfv.android.realestatemanager;

import android.app.Application;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.AppDatabase;
import com.diegomfv.android.realestatemanager.data.AppExecutors;
import com.diegomfv.android.realestatemanager.data.DataRepository;
import com.diegomfv.android.realestatemanager.data.FakeDataGenerator;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */
public class RealEstateManagerApp extends Application {

    private static final String TAG = RealEstateManagerApp.class.getSimpleName();

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: called!");

        mAppExecutors = AppExecutors.getInstance();

        FakeDataGenerator fakeDataGenerator = new FakeDataGenerator();

        for (int i = 0; i < 5; i++) {




        }




    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }

}
