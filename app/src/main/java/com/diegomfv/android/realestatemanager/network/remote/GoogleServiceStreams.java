package com.diegomfv.android.realestatemanager.network.remote;

import android.util.Log;

import com.diegomfv.android.realestatemanager.network.models.placedetails.PlaceDetails;
import com.diegomfv.android.realestatemanager.network.models.placefindplacefromtext.PlaceFromText;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 19/06/2018.
 */
public class GoogleServiceStreams {

    private static final String TAG = GoogleServiceStreams.class.getSimpleName();

    public static Observable<PlaceDetails> streamFetchPlaceDetails(String placeId, String key) {
        Log.d(TAG, "streamFetchPlaceDetails: called!");

        GoogleService googleService = AllGoogleServices.getPlaceDetails();
        return googleService.fetchPlaceId(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceFromText> streamFetchPlaceFromText(String input, String inputType, String key) {
        Log.d(TAG, "streamFetchPlaceFromText: called!");

        GoogleService googleService = AllGoogleServices.getPlaceFromText();
        return googleService.fetchPlaceFromText(input, inputType, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
