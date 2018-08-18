package com.diegomfv.android.realestatemanager.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.diegomfv.android.realestatemanager.R;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */

// TODO: 18/08/2018 Add a notification insertion completes!
public class CreateNewListingActivity extends AppCompatActivity {

    private static final String TAG = CreateNewListingActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        //////////////////////////////////////////////////////////
        setContentView(R.layout.activity_create_new_listing);
        setTitle("Create a new Listing");



    }





}
