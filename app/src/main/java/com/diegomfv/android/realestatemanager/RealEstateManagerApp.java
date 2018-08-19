package com.diegomfv.android.realestatemanager;

import android.app.Application;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.AppDatabase;
import com.diegomfv.android.realestatemanager.data.AppExecutors;
import com.diegomfv.android.realestatemanager.data.DataRepository;
import com.diegomfv.android.realestatemanager.data.FakeDataGenerator;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.utils.FirebasePushIdGenerator;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;
import com.snatik.storage.Storage;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class RealEstateManagerApp extends Application {

    private static final String TAG = RealEstateManagerApp.class.getSimpleName();

    private AppExecutors mAppExecutors;

    private Storage internalStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: called!");

        mAppExecutors = AppExecutors.getInstance();

//        getDatabase().clearAllTables();
//
//        FakeDataGenerator fakeDataGenerator = new FakeDataGenerator();
//
//        for (int i = 0; i < 15; i++) {
//
//            getDatabase()
//                    .realStateDao()
//                    .insertRealEstate(fakeDataGenerator.generateFakeData());
//        }

    }

    public AppDatabase getDatabase() {
        Log.d(TAG, "getDatabase: called!");
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        Log.d(TAG, "getRepository: called!");
        return DataRepository.getInstance(getDatabase());
    }

    public Storage getInternalStorage() {
        Log.d(TAG, "getInternalStorage: called!");
        if (internalStorage == null) {
            internalStorage = new Storage(getApplicationContext());
            return internalStorage;
        }
        else return internalStorage;
    }
}
