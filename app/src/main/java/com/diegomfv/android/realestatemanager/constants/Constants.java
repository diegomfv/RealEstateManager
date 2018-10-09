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
    String CACHE_DIRECTORY = "cache_directory";

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
    float MAPS_POSITION_DEFAULT_ZOOM = 18f;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    String BUNDLE_EMAIL = "email";
    String BUNDLE_PASSWORD = "password";

    String REAL_ESTATE = "real_estate";

    String SH_PREF_CURRENCY_SETTINGS = "currency_settings";
    String CURRENCY = "currency";

    String SH_PREF_AGENT_SETTINGS = "agent_settings";
    String SH_PREF_AGENT_FIRST_NAME = "first_name";
    String SH_PREF_AGENT_LAST_NAME = "last_name";
    String SH_PREF_AGENT_EMAIL = "email";
    String SH_PREF_AGENT_PASSWORD = "password";
    String SH_PREF_AGENT_MEMORABLE_DATA_QUESTION = "memorable_data_question";
    String SH_PREF_AGENT_MEMORABLE_DATA_ANSWER = "memorable_data_answer";

    /* Determines how much memory will be used by the Bitmap Cache
    * */
    int CACHE_PARTITION = 8; // MaxMemory / 8

    String INTENT_FROM_PHOTO_GRID_ACTIVITY = "intent_from_add_photo";
    String STRING_FROM_PHOTO_GRID_ACTIVITY = "string_from_add_photo";

    String INTENT_FROM_ACTIVITY = "intent_from_activity";
    String INTENT_FROM_CREATE = "intent_from_create";
    String INTENT_FROM_EDIT = "intent_from_edit";

    String INTENT_FROM_SEARCH_ENGINE = "intent_from_search_engine";
    String STRING_FROM_SEARCH_ENGINE = "string_from_search_engine";

    String DIALOG_DESCRIPTION = "dialog_description";
    String DIALOG_ADDRESS = "dialog_address";


    String DIALOG_LOAN_AMOUNT = "dialog_loan_amount";
    String DIALOG_ANNUAL_INTEREST_RATE = "dialog_annual_interest_rate";
    String DIALOG_LOAN_PERIOD_YEARS = "dialgo_loan_period_years";
    String DIALOG_PAYMENT_FREQ = "dialog_payment_freq";
    String DIALOG_CURRENCY = "dialog_currency";
}
