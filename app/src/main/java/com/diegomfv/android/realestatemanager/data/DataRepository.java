package com.diegomfv.android.realestatemanager.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.BehaviorSubject;

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

    private Set<String> setOfBuildingTypes;

    private Set<String> setOfLocalities;

    private Set<String> setOfCities;

    private Set<String> setOfTypesOfPointsOfInterest;

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
        Log.d(TAG, "getObservableAllListings: called!");
        return listOfListingsLiveData;
    }

    public LiveData<List<PlaceRealEstate>> getObservableAllPlacesRealEstate() {
        Log.d(TAG, "getObservableAllPlacesRealEstate: called!");
        return listOfPlacesRealEstateLiveData;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private LiveData<List<RealEstate>> getAllListingsLiveData() {
        Log.d(TAG, "getAllListingsLiveData: called!");
        return mDatabase.realStateDao().getAllListingsOrderedByTypeLiveData();
    }

    private LiveData<List<PlaceRealEstate>> getAllPlacesRealEstateLiveData() {
        Log.d(TAG, "getAllPlacesRealEstateLiveData: called!");
        return mDatabase.placeRealEstateDao().getAllPlacesRealEstateLiveData();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<RealEstate> getAllListings() {
        Log.d(TAG, "getAllListingsLiveData: called!");
        return mDatabase.realStateDao().getAllListingsOrderedByType();
    }

    private List<PlaceRealEstate> getAllPlacesRealEstate () {
        Log.d(TAG, "getAllPlacesRealEstate: called!");
        return mDatabase.placeRealEstateDao().getAllPlacesRealEstate();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Set<String> getSetOfBuildingTypes () {
        Log.d(TAG, "getSetOfBuildingTypes: called!");
        if (setOfBuildingTypes == null) {
            setOfBuildingTypes = new HashSet<>();
            List<RealEstate> temporaryList = getAllListings();
            for (int i = 0; i < temporaryList.size(); i++) {
                setOfBuildingTypes.add(temporaryList.get(i).getType());
            }
            return setOfBuildingTypes;
        }
        return setOfBuildingTypes;
    }

    public Set<String> getSetOfLocalities () {
        Log.d(TAG, "getSetOfLocalities: called");
        if (setOfLocalities == null) {
            setOfLocalities = new HashSet<>();
            List<RealEstate> temporaryList = getAllListings();
            for (int i = 0; i < temporaryList.size(); i++) {
                setOfLocalities.add(temporaryList.get(i).getAddress().getLocality());
            }
            return setOfLocalities;
        }
        return setOfLocalities;
    }

    public Set<String> getSetOfCities () {
        Log.d(TAG, "getSetOfCities: called!");
        if (setOfCities == null) {
            setOfCities = new HashSet<>();
            List<RealEstate> temporaryList = getAllListings();
            if (temporaryList != null) {
                for (int i = 0; i < getAllListings().size(); i++) {
                    setOfCities.add(temporaryList.get(i).getAddress().getCity());
                }
            }
            return setOfCities;
        }
        return setOfCities;
    }

    public Set<String> getSetOfTypesOfPointsOfInterest () {
        Log.d(TAG, "getSetOfTypesOfPointsOfInterest: called!");
        if (setOfTypesOfPointsOfInterest == null) {
            setOfTypesOfPointsOfInterest = new HashSet<>();
            List<PlaceRealEstate> temporaryList = getAllPlacesRealEstate();
            if (temporaryList != null) {
                for (int i = 0; i < temporaryList.size() ; i++) {
                    setOfTypesOfPointsOfInterest.addAll(temporaryList.get(i).getTypesList());
                }
            }
            return setOfTypesOfPointsOfInterest;
        }
        return setOfTypesOfPointsOfInterest;
    }

    public void refreshSets () {
        Log.d(TAG, "refreshSets: called!");
        setOfBuildingTypes = null;
        setOfLocalities = null;
        setOfCities = null;
        setOfTypesOfPointsOfInterest = null;
        getSetOfBuildingTypes();
        getSetOfLocalities();
        getSetOfCities();
        getSetOfTypesOfPointsOfInterest();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////






    // TODO: 22/08/2018 Fetch network data from the repository!

}
