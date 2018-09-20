package com.diegomfv.android.realestatemanager.ui.base;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppDatabase;
import com.diegomfv.android.realestatemanager.data.DataRepository;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Diego Fajardo on 27/08/2018.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //SINGLETON GETTERS

    protected RealEstateManagerApp getApp() {
        Log.d(TAG, "getApp: called");
        return (RealEstateManagerApp) getApplication();
    }

    protected AppDatabase getAppDatabase() {
        Log.d(TAG, "getAppDatabase: called!");
        return getApp().getDatabase();
    }

    protected DataRepository getRepository() {
        Log.d(TAG, "getRepository: called!");
        return getApp().getRepository();
    }

    protected Storage getInternalStorage() {
        Log.d(TAG, "getInternalStorage: called!");
        return getApp().getInternalStorage();
    }

    protected RealEstate getRealEstateCache() {
        Log.d(TAG, "getRealEstateCache: called!");
        return getRepository().getRealEstateCache();
    }

    protected void setRealEstateCache(RealEstate realEstate) {
        Log.d(TAG, "setRealEstateCache: called!");
        getRepository().setRealEstateCache(realEstate);
    }

    protected List<ImageRealEstate> getListOfImagesRealEstateCache() {
        Log.d(TAG, "getListOfImagesRealEstateCache: called!");
        return getRepository().getListOfImagesRealEstateCache();
    }

    protected List<PlaceRealEstate> getListOfPlacesRealEstateCache() {
        Log.d(TAG, "getListOfPlacesRealEstateCache: called!");
        return getRepository().getListOfPlacesRealEstateCache();
    }

    protected List<PlaceRealEstate> getListOfPlacesByNearbyCache() {
        Log.d(TAG, "getListOfImagesRealEstateCache: called!");
        return getRepository().getListOfPlacesRealEstateCache();
    }

    protected List<String> getListOfBitmapKeys () {
        Log.d(TAG, "getListOfBitmapCacheKeys: called!");
        return getRepository().getListOfBitmapCacheKeys();
    }

    protected Map<String,Bitmap> getBitmapCache () {
        Log.d(TAG, "getBitmapCache: called!");
        return getRepository().getBitmapCache();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected String getImagesDir () {
        Log.d(TAG, "getImagesDir: called!");
        //mainPath = getInternalStorage().getInternalFilesDirectory() + File.separator;
        return getInternalStorage().getInternalFilesDirectory() + File.separator
                + Constants.IMAGES_DIRECTORY + File.separator;
    }

    protected String getTemporaryDir () {
        Log.d(TAG, "getTemporaryDir: called!");
        return getInternalStorage().getInternalFilesDirectory() + File.separator
                + Constants.TEMPORARY_DIRECTORY + File.separator;
    }

    protected String getCacheDirectory () {
        Log.d(TAG, "getCacheDir: called!");
        return getInternalStorage().getInternalCacheDirectory() + File.separator;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected RequestManager getGlide () {
        Log.d(TAG, "getGlide: called!");
        return Glide.with(getApp());
    }

}
