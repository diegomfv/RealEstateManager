package com.diegomfv.android.realestatemanager.data;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.Agent;
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
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */

/**
 * Repository to store the necessary information for the app to work.
 * It acts as a layer between the database
 * and the ViewModel.
 * The repository stores a real estate cache that is used to create or update items. When the item
 * is created or edited, the cache is cleared.
 */
public class DataRepository {

    private static final String TAG = DataRepository.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;

    private boolean databaseIsEmpty;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private LiveData<List<RealEstate>> listOfListingsLiveData;

    private LiveData<List<RealEstate>> listOfFoundListingsLiveData;

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

    //SEARCH CACHE
    //keeps a list with the realEstates found with the search engine

    private List<RealEstate> listOfFoundRealEstates;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private DataRepository(final AppDatabase database, long maxMemoryInMB) {
        mDatabase = database;
        bitmapCacheSize = maxMemoryInMB / 1024 / 1024 / 8;
        listOfListingsLiveData = getAllListingsLiveData();
        listOfFoundListingsLiveData = getAllFoundListingsLiveData();
        listOfImagesRealEstateLiveData = getAllImagesRealEstateLiveData();
        listOfPlacesRealEstateLiveData = getAllPlacesRealEstateLiveData();
        bitmapCache = getBitmapCache();
        checkIfDatabaseIsEmpty();
    }

    /**
     * Method to get an entry point to the Repository.
     */
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
     * Getter, cache real estate
     */
    public RealEstate getRealEstateCache() {
        Log.d(TAG, "getRealEstateCache: called!");
        if (realEstateCache == null) {
            return realEstateCache = new RealEstate.Builder().setAddress(new AddressRealEstate()).build();
        }
        return realEstateCache;
    }

    /**
     * Setter, cache real estate
     */
    public void setRealEstateCache(RealEstate realEstate) {
        Log.d(TAG, "setRealEstateCache: called!");
        realEstateCache = realEstate;
    }

    public List<ImageRealEstate> getListOfImagesRealEstateCache() {
        Log.d(TAG, "getRealEstateCache: called!");
        if (listOfImagesRealEstateCache == null) {
            return listOfImagesRealEstateCache = new ArrayList<>();
        }
        return listOfImagesRealEstateCache;
    }

    /**
     * We fill a cache containing ImageRealEstate objects
     * related to the realEstate cache object
     */
    @SuppressLint("CheckResult")
    public void fillCacheWithImagesRelatedToRealEstateCache() {
        Log.d(TAG, "fillCacheWithImagesRelatedToRealEstateCache: called!");
        getAllImagesRealEstateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new Observer<List<ImageRealEstate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onNext(List<ImageRealEstate> listOfImagesRealEstate) {
                        Log.d(TAG, "onNext: called!");
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

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");
                    }
                });
    }

    /**
     * Getter for listOfPlacesNearbyCache.
     */
    public List<PlaceRealEstate> getListOfPlacesRealEstateCache() {
        if (listOfPlacesNearbyCache == null) {
            return listOfPlacesNearbyCache = new ArrayList<>();
        }
        return listOfPlacesNearbyCache;
    }

    /**
     * Method to delete the cache.
     */
    public void deleteCache() {
        Log.d(TAG, "deleteCache: called!");

        realEstateCache = null;
        listOfImagesRealEstateCache = null;
        listOfPlacesNearbyCache = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //BITMAPS CACHE

    /**
     * Getter for listOfBitmapCacheKeys.
     */
    public List<String> getListOfBitmapCacheKeys() {
        Log.d(TAG, "getListOfBitmapCacheKeys: called!");
        if (listOfBitmapCacheKeys == null) {
            listOfBitmapCacheKeys = new ArrayList<>();
            return updateListOfBitmapKeys();
        }
        return updateListOfBitmapKeys();
    }

    /**
     * Method to update the ListOfBitmapKeys.
     */
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

    /**
     * Getter for bitmap cache.
     */
    public Map<String, Bitmap> getBitmapCache() {
        Log.d(TAG, "getBitmapCache: called!");
        if (bitmapCache == null) {
            return bitmapCache = new LinkedHashMap<>();
        }
        double currentSize = getCurrentSizeOfBitmapCache();
        Log.i(TAG, "getBitmapCache: getSizeOfBitmapCache = " + currentSize + " MB");
        return bitmapCache;
    }

    /**
     * Method that retrieves the current size of the bitmap cache.
     */
    public double getCurrentSizeOfBitmapCache() {
        Log.d(TAG, "getCurrentSizeOfBitmapCache: called!");

        double size = 0;

        for (Map.Entry<String, Bitmap> entry : bitmapCache.entrySet()) {
            size += entry.getValue().getByteCount();
        }
        return size / 1024 / 1024;
    }

    /**
     * Method that adds a new Bitmap to the Bitmap Cache.
     */
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

    /**
     * Method that checks if the Bitmap cache size is higher that possible. If it is, it removes
     * the first element of the cache
     * This system could be improved since at the moment is deleting the first element of the
     * cache and we may need that element soon. It'd be better to implement a LRU cache.
     */
    private void checkBitmapCacheSize() {
        Log.d(TAG, "checkBitmapCacheSize: called!");

        Log.w(TAG, "checkBitmapCacheSize: bitmapCacheSize = " + bitmapCacheSize);
        Log.w(TAG, "checkBitmapCacheSize: bitmapCacheUsed = " + getCurrentSizeOfBitmapCache());

        if (getCurrentSizeOfBitmapCache() > bitmapCacheSize) {
            removeFirstElementFromBitmapCache();
        }
    }

    /**
     * Method to remove the first element from the bitmap cache.
     */
    private void removeFirstElementFromBitmapCache() {
        Log.d(TAG, "removeFirstElementFromBitmapCache: called!");

        String key = getBitmapCache().keySet().iterator().next();
        getBitmapCache().remove(key);

        Log.w(TAG, "removeFirstElementFromBitmapCache: item removed, ky = " + key);

        checkBitmapCacheSize();
    }

    /**
     * Method to add a Bitmap to the bitmap cache and to the Internal Storage.
     */
    public void addBitmapToBitmapCacheAndStorage(Storage internalStorage, String imagesDir, String key, Bitmap bitmap) {
        Log.d(TAG, "addBitmapToAllMemories: called!");
        addBitmapToBitmapCache(key, bitmap);
        addBitmapToInternalStorageInWorkerThread(internalStorage, imagesDir, key, bitmap);
    }

    /**
     * Method to add a Bitmap to the Internal Storage using a Worker Thread.
     */
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

    /**
     * Method to delete the Bitmap Cache and immediately after fill it with Bitmaps
     */
    public void deleteAndFillBitmapCache(List<String> listOfKeys, Storage internalStorage, String imagesDir) {
        Log.d(TAG, "fillBitmapCache: called!");

        deleteBitmapCache();

        for (int i = 0; i < listOfKeys.size(); i++) {
            getBitmapCache().put(listOfKeys.get(i), getBitmap(internalStorage, imagesDir, listOfKeys.get(i)));
        }
    }

    /**
     * Method to delete the Bitmap Cache
     */
    public void deleteBitmapCache() {
        Log.d(TAG, "deleteBitmapCache: called!");
        bitmapCache = null;
        listOfBitmapCacheKeys = null;
    }

    /**
     * Method to get all the keys of all the Bitmaps that are in the Internal Storage.
     */
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

    /**
     * Getter for listOfFoundRealEstates.
     */
    public List<RealEstate> getListOfFoundRealEstates() {
        Log.d(TAG, "getListOfFoundRealEstates: called!");
        if (listOfFoundRealEstates == null) {
            return listOfFoundRealEstates = new ArrayList<>();
        }
        return listOfFoundRealEstates;
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

    public LiveData<List<RealEstate>> getLiveDataAllFoundListings() {
        Log.d(TAG, "getLiveDataAllFoundListings: called!");
        return listOfFoundListingsLiveData;
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

    /**
     * Use the DAOs to access the data in the database and fill the lists of the repository.
     */
    private LiveData<List<RealEstate>> getAllListingsLiveData() {
        Log.d(TAG, "getAllListingsLiveData: called!");
        return mDatabase.realStateDao().getAllListingsOrderedByTypeLiveData();
    }

    private LiveData<List<RealEstate>> getAllFoundListingsLiveData() {
        Log.d(TAG, "getAllFoundListingsLiveData: called!");
        return mDatabase.realStateDao().getAllFoundListingsLiveData(true);
    }

    private LiveData<List<ImageRealEstate>> getAllImagesRealEstateLiveData() {
        return mDatabase.imageRealEstateDao().getAllImagesRealEstateLiveData();
    }

    private LiveData<List<PlaceRealEstate>> getAllPlacesRealEstateLiveData() {
        Log.d(TAG, "getAllPlacesRealEstateLiveData: called!");
        return mDatabase.placeRealEstateDao().getAllPlacesRealEstateLiveData();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * RxJava methods that return Observables.
     * They allow access to the information of the Database.
     */
    public io.reactivex.Observable<List<RealEstate>> getAllListingsRealEstateObservable() {
        Log.d(TAG, "getAllListingsRealEstateObservable: called!");
        return io.reactivex.Observable.fromCallable(new Callable<List<RealEstate>>() {
            @Override
            public List<RealEstate> call() throws Exception {
                return mDatabase.realStateDao().getAllListingsOrderedByType();
            }
        });
    }

    public io.reactivex.Observable<List<RealEstate>> getAllFoundListingsRealEstateObservable() {
        Log.d(TAG, "getAllFoundListingsRealEstateObservable: called!");
        return io.reactivex.Observable.fromCallable(new Callable<List<RealEstate>>() {
            @Override
            public List<RealEstate> call() throws Exception {
                return mDatabase.realStateDao().getAllFoundListings(true);
            }
        });
    }

    public io.reactivex.Observable<List<ImageRealEstate>> getAllImagesRealEstateObservable() {
        Log.d(TAG, "getAllImagesRealEstateObservable: called!");
        return io.reactivex.Observable.fromCallable(new Callable<List<ImageRealEstate>>() {
            @Override
            public List<ImageRealEstate> call() throws Exception {
                return mDatabase.imageRealEstateDao().getAllImagesRealEstate();
            }
        });
    }

    public io.reactivex.Observable<List<PlaceRealEstate>> getAllPlacesRealEstateObservable() {
        Log.d(TAG, "getAllPlacesRealEstateObservable: called!");
        return io.reactivex.Observable.fromCallable(new Callable<List<PlaceRealEstate>>() {
            @Override
            public List<PlaceRealEstate> call() throws Exception {
                return mDatabase.placeRealEstateDao().getAllPlacesRealEstate();
            }
        });
    }

    public io.reactivex.Observable<List<Agent>> getAllAgents () {
        Log.d(TAG, "getAllAgents: called!");
        return io.reactivex.Observable.fromCallable(new Callable<List<Agent>>() {
            @Override
            public List<Agent> call() throws Exception {
                return mDatabase.agentDao().getAllAgents();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * RxJava methods that return Completables. They are used to insert and update information
     * in the database.
     */
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

    public Completable insertAgent(final Agent agent) {
        Log.d(TAG, "updateRealEstate: called!");
        return Completable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mDatabase.agentDao().insertAgent(agent);
            }
        });
    }

    public Completable updateRealEstate(final RealEstate realEstate) {
        Log.d(TAG, "updateRealEstate: called!");
        return Completable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return mDatabase.realStateDao().updateRealEstate(realEstate);
            }
        });
    }

    public Completable updateAgent(final Agent agent) {
        Log.d(TAG, "updateRealEstate: called!");
        return Completable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return mDatabase.agentDao().updateAgent(agent);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for databaseIsEmpty.
     */
    public boolean getDatabaseIsEmpty() {
        Log.d(TAG, "getDatabaseIsEmpty: called!");
        return databaseIsEmpty;
    }

    /**
     * Getter to check in a Worker Thread if the database is empty.
     */
    @SuppressLint("CheckResult")
    private void checkIfDatabaseIsEmpty() {
        Log.d(TAG, "checkIfDatabaseIsEmpty: called!");
        getAllListingsRealEstateObservable()
                .subscribeWith(new Observer<List<RealEstate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onNext(List<RealEstate> realEstates) {
                        Log.d(TAG, "onNext: called!");
                        databaseIsEmpty = realEstates == null || realEstates.size() <= 0;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");
                    }
                });
    }
}
