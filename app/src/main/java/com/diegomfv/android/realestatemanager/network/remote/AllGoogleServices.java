package com.diegomfv.android.realestatemanager.network.remote;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
public class AllGoogleServices {

    public static GoogleService getPlaceDetails() {
        return RetrofitClient.getPlaceDetails().create(GoogleService.class);
    }

    public static GoogleService getPlaceFromText() {
        return RetrofitClient.getPlaceFromText().create(GoogleService.class);
    }
}
