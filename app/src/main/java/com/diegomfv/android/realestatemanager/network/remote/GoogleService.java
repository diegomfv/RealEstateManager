package com.diegomfv.android.realestatemanager.network.remote;

import com.diegomfv.android.realestatemanager.network.models.placedetails.PlaceDetails;
import com.diegomfv.android.realestatemanager.network.models.placefindplacefromtext.PlaceFromText;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
public interface GoogleService {

    @GET("json")
    Observable<PlaceFromText> fetchPlaceFromText(
            @Query("input") String input,
            @Query("inputtype") String inputType,
            @Query("key") String key
    );

    @GET("json")
    Observable<PlaceDetails> fetchPlaceId(
            @Query("placeid") String placeId,
            @Query("key") String key
    );


}