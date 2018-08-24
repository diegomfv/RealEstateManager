package com.diegomfv.android.realestatemanager.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class DataRepository {

    private static final String TAG = DataRepository.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private LiveData<List<RealEstate>> listOfListingsLiveData;

    private LiveData<List<PlaceRealEstate>> listOfPlacesRealEstateLiveData;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //CACHE

    private RealEstate realEstateCache;

    private List<ImageRealEstate> listOfImagesRealEstateCache;

    private List<PlaceRealEstate> listOfPlacesNearbyCache;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        listOfListingsLiveData = getAllListingsLiveData();
        listOfPlacesRealEstateLiveData = getAllPlacesRealEstateLiveData();
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
            return realEstateCache = new RealEstate.Builder().setAddress(new AddressRealEstate()).build();
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

    /** Get lists from the database
     * and get notified when the data changes.
     */
    public LiveData<List<RealEstate>> getObservableAllListings() {
        return listOfListingsLiveData;
    }

    public LiveData<List<PlaceRealEstate>> getObservableAllPlacesRealEstate() {
        return listOfPlacesRealEstateLiveData;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private LiveData<List<RealEstate>> getAllListingsLiveData() {
        return mDatabase.realStateDao().getAllListingsOrderedByTypeLiveData();
    }

    private LiveData<List<PlaceRealEstate>> getAllPlacesRealEstateLiveData() {
        return mDatabase.placeRealEstateDao().getAllPlacesRealEstateLiveData();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: 22/08/2018 Fetch network data from the repository!

}
