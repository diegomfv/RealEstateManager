package com.diegomfv.android.realestatemanager.constants;

import android.Manifest;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public interface Constants {

    String CONNECTIVITY_CHANGE_STATUS = "android.net.conn.CONNECTIVITY_CHANGE";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    String SEND_PARCELABLE = "send_parcelable";
    String GET_PARCELABLE = "parcelable_object";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /* Directories for internal storage
    * */
    String IMAGES_DIRECTORY = "images_directory";
    String TEMPORARY_DIRECTORY = "temporary_directory";

    /* Requests
    * */
    String REQUEST_STATUS_PLACE_FROM_TEXT_IS_OK = "OK";
    String REQUEST_STATUS_PLACE_FROM_TEXT_IS_ZERO_RESULTS = "ZERO_RESULTS";
    int REQUEST_ERROR_DIALOG = 9001; //Google Play Services

    //Permissions
    int REQUEST_CODE_GALLERY = 0;
    int REQUEST_CODE_ALL_PERMISSIONS = 1;
    int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;

    String[] ALL_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    String NOTIFICATION_CHANNEL_ID = "notification_channel";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /* Value for retrofit request
    * */
    String FETCH_NEARBY_RANKBY = "distance";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Google Maps
    float MAPS_DEFAULT_ZOOM = 17f;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    String CURRENCY = "CURRENCY";
    String SH_PREF_CURRENCY_SETTINGS = "currency_settings";

}
