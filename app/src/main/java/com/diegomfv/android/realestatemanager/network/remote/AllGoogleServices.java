package com.diegomfv.android.realestatemanager.network.remote;

import android.util.Log;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
public class AllGoogleServices {
    private static final String TAG = AllGoogleServices.class.getSimpleName();

    public static GoogleService getPlaceDetails() {
        Log.d(TAG, "getPlaceDetails: called!");
        return RetrofitClient.getPlaceDetails().create(GoogleService.class);
    }

    public static GoogleService getPlaceFromText() {
        Log.d(TAG, "getPlaceFromText: called!");
        return RetrofitClient.getPlaceFromText().create(GoogleService.class);
    }
}
