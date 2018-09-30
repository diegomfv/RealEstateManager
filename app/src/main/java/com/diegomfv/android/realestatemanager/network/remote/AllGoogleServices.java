package com.diegomfv.android.realestatemanager.network.remote;

import android.util.Log;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */

/**
 * This class allows access to Google Services using Retrofit.
 */
public class AllGoogleServices {

    private static final String TAG = AllGoogleServices.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to access a Google Service that will retrieve Details of a place.
     */
    public static GoogleService getPlaceDetails() {
        Log.d(TAG, "getPlaceDetails: called!");
        return RetrofitClient.getPlaceDetails().create(GoogleService.class);
    }

    /**
     * Method to access a Google Service that will retrieve places using some text.
     */
    public static GoogleService getPlaceFromText() {
        Log.d(TAG, "getPlaceFromText: called!");
        return RetrofitClient.getPlaceFromText().create(GoogleService.class);
    }

    /**
     * Method to access a Google Service that will retrieve Nearby Places.
     */
    public static GoogleService getGoogleNearbyService() {
        Log.d(TAG, "getGoogleNearbyService: called!");
        return RetrofitClient.getNearbyPlaces().create(GoogleService.class);
    }
}
