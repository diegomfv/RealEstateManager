package com.diegomfv.android.realestatemanager.ui.base;

import android.support.v4.app.Fragment;
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
import com.diegomfv.android.realestatemanager.ui.activities.MainActivity;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.List;

/**
 * Created by Diego Fajardo on 27/08/2018.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = BaseFragment.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //SINGLETON GETTERS

    /**
     * Method to get a reference to the application.
     */
    protected RealEstateManagerApp getApp() {
        Log.d(TAG, "getApp: called");
        return (RealEstateManagerApp) getActivity().getApplication();
    }

    /**
     * Method to get a reference to the Database.
     */
    protected AppDatabase getAppDatabase() {
        Log.d(TAG, "getAppDatabase: called!");
        return getApp().getDatabase();
    }

    /**
     * Method to get a reference to the DataRepository.
     */
    protected DataRepository getRepository() {
        Log.d(TAG, "getRepository: called!");
        return getApp().getRepository();
    }

    /**
     * Method to get a reference to the Internal Storage.
     */
    protected Storage getInternalStorage() {
        Log.d(TAG, "getInternalStorage: called!");
        return getApp().getInternalStorage();
    }

    /**
     * Getter of the real estate cache object.
     */
    protected RealEstate getRealEstateCache() {
        Log.d(TAG, "getRealEstateCache: called!");
        return getRepository().getRealEstateCache();
    }

    /**
     * Getter of the list of images of the real estate cache object.
     */
    protected List<ImageRealEstate> getListOfImagesRealEstateCache() {
        Log.d(TAG, "getListOfImagesRealEstateCache: called!");
        return getRepository().getListOfImagesRealEstateCache();
    }

    /**
     * Getter of the list of places of the real estate cache object.
     */
    protected List<PlaceRealEstate> getListOfPlacesRealEstateCache() {
        Log.d(TAG, "getListOfPlacesRealEstateCache: called!");
        return getRepository().getListOfPlacesRealEstateCache();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that retrieves a reference to MainActivity
     */
    protected MainActivity getRootMainActivity() {
        Log.d(TAG, "getActivity: called!");
        return (MainActivity) getActivity();
    }

    /**
     * Getter of the Images Directory
     */
    protected String getImagesDir() {
        Log.d(TAG, "getImagesDir: called!");
        //mainPath = getInternalStorage().getInternalFilesDirectory() + File.separator;
        return getInternalStorage().getInternalFilesDirectory() + File.separator
                + Constants.IMAGES_DIRECTORY + File.separator;
    }

    /**
     * Getter of the Images Temporary Directory
     */
    protected String getTemporaryDir() {
        Log.d(TAG, "getTemporaryDir: called!");
        return getInternalStorage().getInternalFilesDirectory() + File.separator
                + Constants.TEMPORARY_DIRECTORY + File.separator;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for Glide.
     */
    protected RequestManager getGlide() {
        Log.d(TAG, "getGlide: called!");
        return Glide.with(getApp());
    }

}
