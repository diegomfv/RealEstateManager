package com.diegomfv.android.realestatemanager.ui.base;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppDatabase;
import com.diegomfv.android.realestatemanager.data.DataRepository;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.util.GlideApp;
import com.diegomfv.android.realestatemanager.util.GlideRequests;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.List;

/**
 * Created by Diego Fajardo on 27/08/2018.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();

    protected RealEstateManagerApp getApp() {
        Log.d(TAG, "getApp: called");
        return (RealEstateManagerApp) getActivity().getApplication();
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected BaseActivity getBaseActivity () {
        Log.d(TAG, "getActivity: called!");
        return (BaseActivity) getActivity();
    }


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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected GlideRequests getGlide () {
        Log.d(TAG, "getGlide: called!");
        return GlideApp.with(getApp());
    }

}
