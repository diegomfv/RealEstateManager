package com.diegomfv.android.realestatemanager.data;

import android.arch.lifecycle.LiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
// TODO: 22/08/2018 Fetch network data from the repository!
public class DataRepository {

    private static final String TAG = DataRepository.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private LiveData<List<RealEstate>> listOfListingsLiveData;

    private LiveData<List<ImageRealEstate>> listOfImagesRealEstateLiveData;

    private LiveData<List<PlaceRealEstate>> listOfPlacesRealEstateLiveData;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //TEMPORARY CACHE (cache available for when creating or editing listings)
    private RealEstate realEstateCache;

    private List<ImageRealEstate> listOfImagesRealEstateCache;

    private List<PlaceRealEstate> listOfPlacesNearbyCache;

    //BITMAP RELATED CACHES (memory cache for loading images in recyclerViews, etc.)
    private Map<String, Bitmap> bitmapCache;

    private long bitmapCacheSize; // in MB

    private List<String> listOfBitmapCacheKeys;

    private List<String> listOfBitmapKeysInternalStorage; //keeps ids (keys) of internal storage
    //to retrieve the bitmaps (name of the files)

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //SEARCH CACHE (keeps track of different information to help displaying it in Search Activity:
    //checkboxes, autoCompleteTextViews...) Also keeps a list with the realEstates found with the
    //search engine

    private Set<String> setOfBuildingTypes;

    private Set<String> setOfLocalities;

    private Set<String> setOfCities;

    private Set<String> setOfTypesOfPointsOfInterest;

    private List<RealEstate> listOfFoundRealEstates;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private DataRepository(final AppDatabase database, long maxMemoryInMB) {
        mDatabase = database;
        bitmapCacheSize = maxMemoryInMB / 1024 / 1024 / 8;
        listOfListingsLiveData = getAllListingsLiveData();
        listOfImagesRealEstateLiveData = getAllImagesRealEstateLiveData();
        listOfPlacesRealEstateLiveData = getAllPlacesRealEstateLiveData();
        bitmapCache = getBitmapCache();
    }

    public static DataRepository getInstance(final AppDatabase database, long maxMemoryInMB) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database, maxMemoryInMB);
                }
            }
        }
        return sInstance;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //GENERAL CACHE

    /**
     * Cache for Real Estate
     */
    public RealEstate getRealEstateCache() {
        Log.d(TAG, "getRealEstateCache: called!");
        if (realEstateCache == null) {
            return realEstateCache = new RealEstate.Builder().setAddress(new AddressRealEstate()).build();
        }
        return realEstateCache;
    }

    public void cloneRealEstate(RealEstate realEstate) {
        Log.d(TAG, "cloneRealEstate: called!");
        this.realEstateCache = new RealEstate(realEstate);
    }

    public List<ImageRealEstate> getListOfImagesRealEstateCache() {
        Log.d(TAG, "getRealEstateCache: called!");
        if (listOfImagesRealEstateCache == null) {
            return listOfImagesRealEstateCache = new ArrayList<>();
        }
        return listOfImagesRealEstateCache;
    }

    /**
     * We fill a cache containing ImageRealEstate objects related to the realEstate object
     * with objects from the database with the same id as the ids we find in the list
     * of the real estate object
     */
    public void fillCacheWithImagesRelatedToRealEstate(List<ImageRealEstate> listOfImagesRealEstate) {
        Log.d(TAG, "fillCacheWithImagesRelatedToRealEstate: called!");
        if (listOfImagesRealEstate != null) {
            for (int i = 0; i < listOfImagesRealEstate.size(); i++) {
                if (getRealEstateCache().getListOfImagesIds().contains(listOfImagesRealEstate.get(i).getId())) {
                    getListOfImagesRealEstateCache().add(listOfImagesRealEstate.get(i));
                }
            }

        } else {
            Log.w(TAG, "fillCacheWithImagesRelatedToRealEstate: " + "the list is NULL");
        }
    }

    public List<PlaceRealEstate> getListOfPlacesRealEstateCache() {
        if (listOfPlacesNearbyCache == null) {
            return listOfPlacesNearbyCache = new ArrayList<>();
        }
        return listOfPlacesNearbyCache;
    }

    public void deleteCacheAndSets() {
        Log.d(TAG, "nullifyCache: called!");
        deleteCache();
        deleteSets();
    }

    private void deleteCache() {
        Log.d(TAG, "deleteCache: called!");

        realEstateCache = null;
        listOfImagesRealEstateCache = null;
        listOfPlacesNearbyCache = null;
    }

    private void deleteSets() {
        Log.d(TAG, "deleteSets: called!");
        setOfBuildingTypes = null;
        setOfLocalities = null;
        setOfCities = null;
        setOfTypesOfPointsOfInterest = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //BITMAPS CACHE

    public List<String> getListOfBitmapCacheKeys() {
        Log.d(TAG, "getListOfBitmapCacheKeys: called!");
        if (listOfBitmapCacheKeys == null) {
            listOfBitmapCacheKeys = new ArrayList<>();
            return updateListOfBitmapKeys();
        }
        return updateListOfBitmapKeys();
    }

    private List<String> updateListOfBitmapKeys() {
        Log.d(TAG, "updateListOfBitmapKeys: called!");

        if (bitmapCache != null) {
            for (Map.Entry<String, Bitmap> entry : bitmapCache.entrySet()) {
                if (!listOfBitmapCacheKeys.contains(entry.getKey())) {
                    listOfBitmapCacheKeys.add(entry.getKey());
                }
            }
        }
        return listOfBitmapCacheKeys;
    }

    public Map<String, Bitmap> getBitmapCache() {
        Log.d(TAG, "getBitmapCache: called!");
        if (bitmapCache == null) {
            return bitmapCache = new LinkedHashMap<>();
        }
        double currentSize = getCurrentSizeOfBitmapCache();
        Log.i(TAG, "getBitmapCache: getSizeOfBitmapCache = " + currentSize + " MB");
        return bitmapCache;

    }

    public double getCurrentSizeOfBitmapCache() {
        Log.d(TAG, "getCurrentSizeOfBitmapCache: called!");

        double size = 0;

        for (Map.Entry<String, Bitmap> entry : bitmapCache.entrySet()) {
            size += entry.getValue().getByteCount();
        }
        return size / 1024 / 1024;
    }

    public void addBitmapToBitmapCache(String key, Bitmap bitmap) {
        Log.d(TAG, "addBitmapToBitmapCache: called!");
        if (!getBitmapCache().containsKey(key)) {
            getBitmapCache().put(key, bitmap);
            checkBitmapCacheSize();
        } else {
            Log.w(TAG, "addBitmapToBitmapCache: key already in Cache");
            checkBitmapCacheSize();
        }
    }

    private void checkBitmapCacheSize() {
        Log.d(TAG, "checkBitmapCacheSize: called!");

        Log.w(TAG, "checkBitmapCacheSize: bitmapCacheSize = " + bitmapCacheSize);
        Log.w(TAG, "checkBitmapCacheSize: bitmapCacheUsed = " + getCurrentSizeOfBitmapCache());

        if (getCurrentSizeOfBitmapCache() > bitmapCacheSize) {
            removeFirstElementFromBitmapCache();
        }
    }

    private void removeFirstElementFromBitmapCache() {
        Log.d(TAG, "removeFirstElementFromBitmapCache: called!");

        String key = getBitmapCache().keySet().iterator().next();
        getBitmapCache().remove(key);

        Log.w(TAG, "removeFirstElementFromBitmapCache: item removed, ky = " + key);

        checkBitmapCacheSize();

    }

    public void addBitmapToBitmapCacheAndStorage(Storage internalStorage, String imagesDir, String key, Bitmap bitmap) {
        Log.d(TAG, "addBitmapToAllMemories: called!");

        addBitmapToBitmapCache(key, bitmap);
        addBitmapToInternalStorageInWorkerThread(internalStorage, imagesDir, key, bitmap);

    }

    public void addBitmapToInternalStorageInWorkerThread(final Storage internalStorage, final String imagesDir, final String key, final Bitmap bitmap) {
        Log.d(TAG, "addBitmapToInternalStorageInWorkerThread: called!");
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: called!");

                if (internalStorage.isDirectoryExists(imagesDir)) {
                    internalStorage.createFile(imagesDir + key, bitmap);

                } else {
                    internalStorage.createDirectory(imagesDir);
                    internalStorage.createFile(imagesDir + key, bitmap);
                }
            }
        });

    }

    /**
     * Gets a bitmap from the immediate cache
     * or from the internal storage
     */
    public Bitmap getBitmap(Storage storage, String imagesDir, String key) {
        Log.d(TAG, "getBitmap: called!");

        if (getBitmapCache().get(key) != null) {
            return getBitmapCache().get(key);
        }

        byte[] bytes = storage.readFile(imagesDir + key);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    public void deleteAndFillBitmapCache(List<String> listOfKeys, Storage internalStorage, String imagesDir) {
        Log.d(TAG, "fillBitmapCache: called!");

        deleteBitmapCache();

        for (int i = 0; i < listOfKeys.size(); i++) {
            getBitmapCache().put(listOfKeys.get(i), getBitmap(internalStorage, imagesDir, listOfKeys.get(i)));
        }
    }

    public void deleteBitmapCache() {
        Log.d(TAG, "deleteBitmapCache: called!");
        bitmapCache = null;
        listOfBitmapCacheKeys = null;
    }

    public List<String> getListOfBitmapKeysInternalStorage(String imagesDir) {
        Log.d(TAG, "getListOfBitmapKeysInternalStorage: called!");
        listOfBitmapKeysInternalStorage = new ArrayList<>();
        File[] files = new File(imagesDir).listFiles();
        if (files != null) {
            for (File file : files) {
                listOfBitmapKeysInternalStorage.add(file.getName());
            }
            return listOfBitmapKeysInternalStorage;
        } else {
            return listOfBitmapKeysInternalStorage;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //SEARCH CACHE

    public List<RealEstate> getListOfFoundRealEstates() {
        Log.d(TAG, "getListOfFoundRealEstates: called!");
        if (listOfFoundRealEstates == null) {
            return listOfFoundRealEstates = new ArrayList<>();
        }
        return listOfFoundRealEstates;
    }

    public Set<String> getSetOfBuildingTypes() {
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

    public Set<String> getSetOfLocalities() {
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

    public Set<String> getSetOfCities() {
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

    public Set<String> getSetOfTypesOfPointsOfInterest() {
        Log.d(TAG, "getSetOfTypesOfPointsOfInterest: called!");
        if (setOfTypesOfPointsOfInterest == null) {
            setOfTypesOfPointsOfInterest = new HashSet<>();
            List<PlaceRealEstate> temporaryList = getAllPlacesRealEstate();
            if (temporaryList != null) {
                for (int i = 0; i < temporaryList.size(); i++) {
                    setOfTypesOfPointsOfInterest.addAll(temporaryList.get(i).getTypesList());
                }
            }
            return setOfTypesOfPointsOfInterest;
        }
        return setOfTypesOfPointsOfInterest;
    }

    public void refreshSets() {
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

    /**
     * Get lists from the database
     * and get notified when the data changes.
     */
    public LiveData<List<RealEstate>> getLiveDataAllListings() {
        Log.d(TAG, "getLiveDataAllListings: called!");
        return listOfListingsLiveData;
    }

    public LiveData<List<ImageRealEstate>> getLiveDataAllImagesRealEstate() {
        Log.d(TAG, "getLiveDataAllImagesRealEstate: called!");
        return listOfImagesRealEstateLiveData;
    }

    public LiveData<List<PlaceRealEstate>> getLiveDataAllPlacesRealEstate() {
        Log.d(TAG, "getLiveDataAllPlacesRealEstate: called!");
        return listOfPlacesRealEstateLiveData;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private LiveData<List<RealEstate>> getAllListingsLiveData() {
        Log.d(TAG, "getAllListingsLiveData: called!");
        return mDatabase.realStateDao().getAllListingsOrderedByTypeLiveData();
    }

    private LiveData<List<ImageRealEstate>> getAllImagesRealEstateLiveData() {
        return mDatabase.imageRealEstateDao().getAllImagesRealEstateLiveData();
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

    private List<ImageRealEstate> getAllImagesRealEstate() {
        Log.d(TAG, "getAllImagesRealEstate: called!");
        return mDatabase.imageRealEstateDao().getAllImagesRealEstate();
    }

    private List<PlaceRealEstate> getAllPlacesRealEstate() {
        Log.d(TAG, "getAllPlacesRealEstate: called!");
        return mDatabase.placeRealEstateDao().getAllPlacesRealEstate();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Completable getAllListingsRealEstateCompletable() {
        Log.d(TAG, "getAllListingsRealEstateCompletable: called!");
        return Completable.fromCallable(new Callable<List<RealEstate>>() {
            @Override
            public List<RealEstate> call() throws Exception {
                return mDatabase.realStateDao().getAllListingsOrderedByType();
            }
        });
    }

    public Completable getAllImagesRealEstateCompletable() {
        Log.d(TAG, "getAllImagesRealEstateCompletable: called!");
        return Completable.fromCallable(new Callable<List<ImageRealEstate>>() {
            @Override
            public List<ImageRealEstate> call() throws Exception {
                return mDatabase.imageRealEstateDao().getAllImagesRealEstate();
            }
        });
    }

    public Completable getAllPlacesRealEstateCompletable() {
        Log.d(TAG, "getAllPlacesRealEstateCompletable: called!");
        return Completable.fromCallable(new Callable<List<PlaceRealEstate>>() {
            @Override
            public List<PlaceRealEstate> call() throws Exception {
                return mDatabase.placeRealEstateDao().getAllPlacesRealEstate();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Completable insertRealEstate(final RealEstate realEstate) {
        Log.d(TAG, "insertRealEstate: called!");
        return Completable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mDatabase.realStateDao().insertRealEstate(realEstate);
            }
        });
    }

    public Completable insertListImagesRealEstate(final List<ImageRealEstate> list) {
        Log.d(TAG, "insertImageRealEstate: called!");
        return Completable.fromCallable(new Callable<List<Long>>() {
            @Override
            public List<Long> call() throws Exception {
                return mDatabase.imageRealEstateDao().insertListOfImagesRealEstate(list);
            }
        });
    }

    public Completable insertListPlacesRealEstate(final List<PlaceRealEstate> list) {
        Log.d(TAG, "insertListPlacesRealEstate: called!");
        return Completable.fromCallable(new Callable<List<Long>>() {
            @Override
            public List<Long> call() throws Exception {
                return mDatabase.placeRealEstateDao().insertListOfPlaceRealEstate(list);
            }
        });
    }
    
    public Completable updateRealEstate (final RealEstate realEstate) {
        Log.d(TAG, "updateRealEstate: called!");
        return Completable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return mDatabase.realStateDao().updateRealEstate(realEstate);
            }
        });
    }
    
}
