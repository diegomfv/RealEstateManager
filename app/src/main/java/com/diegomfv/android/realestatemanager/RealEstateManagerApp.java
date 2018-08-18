package com.diegomfv.android.realestatemanager;

import android.app.Application;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.AppDatabase;
import com.diegomfv.android.realestatemanager.data.AppExecutors;
import com.diegomfv.android.realestatemanager.data.DataRepository;
import com.diegomfv.android.realestatemanager.data.FakeDataGenerator;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class RealEstateManagerApp extends Application {

    private static final String TAG = RealEstateManagerApp.class.getSimpleName();

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: called!");

        mAppExecutors = AppExecutors.getInstance();

        getDatabase().clearAllTables();

        FakeDataGenerator fakeDataGenerator = new FakeDataGenerator();

        for (int i = 0; i < 15; i++) {

            getDatabase()
                    .realStateDao()
                    .insertRealEstate(fakeDataGenerator.generateFakeData());

        }

    }

    public AppDatabase getDatabase() {
        Log.d(TAG, "getDatabase: called!");
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        Log.d(TAG, "getRepository: called!");
        return DataRepository.getInstance(getDatabase());
    }
}
