package com.diegomfv.android.realestatemanager.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.diegomfv.android.realestatemanager.R;

/**
 * Created by Diego Fajardo on 23/08/2018.
 */
public class EditListingActivity extends AppCompatActivity {

    private static final String TAG = EditListingActivity.class.getSimpleName();

    private boolean accessInternalStorageGranted;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.accessInternalStorageGranted = false;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_edit_listing);



    }



}
