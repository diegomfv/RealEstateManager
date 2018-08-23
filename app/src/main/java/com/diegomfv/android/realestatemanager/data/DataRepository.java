package com.diegomfv.android.realestatemanager.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class DataRepository {

    private static final String TAG = DataRepository.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private LiveData<List<RealEstate>> listOfListings;

    private RealEstate realEstateCache;

    private List<ImageRealEstate> listOfImagesRealEstateCache;

    private List<PlaceRealEstate> listOfPlacesNearbyCache;

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //CACHE

    /** Cache for Real Estate
     * */
    public RealEstate getRealEstateCache() {
        Log.d(TAG, "getRealEstateCache: called!");
        if (realEstateCache == null) {
            return realEstateCache = new RealEstate.Builder().build();
        }
        return realEstateCache;
    }

    public List<ImageRealEstate> getListOfImagesRealEstateCache() {
        Log.d(TAG, "getRealEstateCache: called!");
        if (listOfImagesRealEstateCache == null) {
            return listOfImagesRealEstateCache = new ArrayList<>();
        }
        return listOfImagesRealEstateCache;
    }

    public List<PlaceRealEstate> getListOfPlacesRealEstateCache() {
        if (listOfPlacesNearbyCache == null) {
            return listOfPlacesNearbyCache = new ArrayList<>();
        }
        return listOfPlacesNearbyCache;
    }

    public void deleteCache () {
        Log.d(TAG, "nullifyCache: called!");

        realEstateCache = null;

        if (listOfImagesRealEstateCache != null) {
            listOfImagesRealEstateCache.clear();
        }

        listOfImagesRealEstateCache = null;

        if (listOfPlacesNearbyCache != null) {
            listOfPlacesNearbyCache.clear();
        }

        listOfPlacesNearbyCache = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Get the list of real estate from the database and get notified when the data changes.
     */
    public LiveData<List<RealEstate>> getObservableAllListings() {
        return listOfListings;
    }

    public LiveData<List<RealEstate>> getAllListings() {
        return mDatabase.realStateDao().getAllListingsOrderedByType();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: 22/08/2018 Fetch network data from the repository!


}
