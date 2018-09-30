package com.diegomfv.android.realestatemanager.network.remote;

import com.diegomfv.android.realestatemanager.network.models.placebynearby.LatLngForRetrofit;
import com.diegomfv.android.realestatemanager.network.models.placebynearby.PlacesByNearby;
import com.diegomfv.android.realestatemanager.network.models.placedetails.PlaceDetails;
import com.diegomfv.android.realestatemanager.network.models.placefindplacefromtext.PlaceFromText;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
public interface GoogleService {

    /**
     * Method to get Places using Text (It uses a URL).
     */
    @GET("json")
    Observable<PlaceFromText> fetchPlaceFromText(
            @Query("input") String input,
            @Query("inputtype") String inputType,
            @Query("key") String key
    );

    /**
     * Method to get Details from a Place (It uses a URL).
     */
    @GET("json")
    Observable<PlaceDetails> fetchPlaceId(
            @Query("placeid") String placeId,
            @Query("key") String key
    );

    /**
     * Method to get Nearby Places (It uses a URL).
     */
    @GET("json")
    Observable<PlacesByNearby> fetchNearbyPlaces(
            @Query("location") LatLngForRetrofit latLngForRetrofit,
            @Query("rankby") String rankby,
            @Query("key") String key
    );


}