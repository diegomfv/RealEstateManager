package com.diegomfv.android.realestatemanager.network.remote;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
/** https://stackoverflow.com/questions/36628399/should-i-use-retrofit-with-a-singleton?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 * Q: If it is a singleton, can it handle two or more api requests in parallel?
 * A: Yes, it can handle many parallel requests - I'm not sure what the limit is,
 * but when you exceed that, it will queue the request (assuming that you are using
 * it asynchronously, which you should). I've successfully thrown a dozen or more async
 * requests at it in quick succession, without worrying about how many outstanding requests
 * there are. */

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/** Singletons of Retrofit. Keeping them as singleton will increase performance,
 * because you will not create each time costful objects like Gson, RestAdapter and ApiService.
 * */
public class RetrofitClient {

    private static final String TAG = RetrofitClient.class.getSimpleName();

    private static final String GOOGLE_API_PLACE_DETAILS = "https://maps.googleapis.com/maps/api/place/details/";
    private static final String GOOGLE_API_FIND_PLACE_FROM_TEXT = "https://maps.googleapis.com/maps/api/place/findplacefromtext/";

    private static Retrofit retrofitDetails = null;
    private static Retrofit retrofitPlaceFromText = null;

    public static Retrofit getPlaceDetails () {
        Log.d(TAG, "getPlaceDetails: called!");

        if (retrofitDetails == null) {
            retrofitDetails = new Retrofit.Builder()
                    .baseUrl(GOOGLE_API_PLACE_DETAILS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofitDetails;
    }

    public static Retrofit getPlaceFromText () {
        Log.d(TAG, "getPlaceFromText: called!");

        if (retrofitPlaceFromText == null) {
            retrofitPlaceFromText = new Retrofit.Builder()
                    .baseUrl(GOOGLE_API_FIND_PLACE_FROM_TEXT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofitPlaceFromText;
    }
}