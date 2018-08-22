package com.diegomfv.android.realestatemanager.constants;

import android.Manifest;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public interface Constants {

    String SEND_PARCELABLE = "send_parcelable";
    String GET_PARCELABLE = "parcelable_object";

    String DESCRIPTIONS_SERIALIZABLE = "descriptions_bundle";
    String IMAGES_DIRECTORY = "images_directory";
    String TEMPORARY_DIRECTORY = "temporary_directory";

    String REQUEST_STATUS_PLACE_FROM_TEXT_IS_OK = "OK";
    String REQUEST_STATUS_PLACE_FROM_TEXT_IS_ZERO_RESULTS = "ZERO_RESULTS";

    int REQUEST_CODE_GALLERY = 0;
    int REQUEST_CODE_ALL_PERMISSIONS = 1;
    int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;

    String CONNECTIVITY_CHANGE_STATUS = "android.net.conn.CONNECTIVITY_CHANGE";

    String[] ALL_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    String NOTIFICATION_CHANNEL_ID = "notification_channel";

    String FETCH_NEARBY_RANKBY = "distance";
}
