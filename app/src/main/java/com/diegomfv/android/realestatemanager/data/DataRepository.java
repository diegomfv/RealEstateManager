package com.diegomfv.android.realestatemanager.data;

import android.arch.lifecycle.LiveData;

import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.util.List;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class DataRepository {

    private static final String TAG = DataRepository.class.getSimpleName();

    //////////////////////////////////

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;

    private LiveData<List<RealEstate>> listOfListings;

    //////////////////////////////////

    private DataRepository(final AppDatabase database) {
        mDatabase = database;

        listOfListings = getAllListings();

    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    /**
     * Get the list of real estate from the database and get notified when the data changes.
     */
    public LiveData<List<RealEstate>> getObservableAllListings() {
        return listOfListings;
    }

    public LiveData<List<RealEstate>> getAllListings() {
        return mDatabase.realStateDao().getAllListingsOrderedByType();
    }

}
