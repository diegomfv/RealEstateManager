package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.ui.rest.FragmentItemDescription;
import com.diegomfv.android.realestatemanager.ui.rest.FragmentListListings;
import com.diegomfv.android.realestatemanager.utils.Utils;

import java.io.File;

/** How crashes were solved:
 * 1. Modified the id of the view from activity_second_activity_text_view_main
 * to activity_main_activity_text_view_quantity
 * 2. Add String.valueOf() to convert int to String
 * */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RealEstateManagerApp app;

    private boolean accessInternalStorageGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        app = (RealEstateManagerApp) getApplication();

        accessInternalStorageGranted = false;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_main);

        this.loadFragmentOrFragments();

        this.checkInternalStoragePermissionGranted();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called!");

        switch (requestCode) {

            case Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] != -1) {
                    accessInternalStorageGranted = true;
                    deleteTemporaryStorage();
                }
            }
            break;
        }

    }

    /** Method that loads one or two fragments depending on the device
     * */
    private void loadFragmentOrFragments() {
        Log.d(TAG, "loadFragmentOrFragments: called!");

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment1_container_id, FragmentListListings.newInstance())
                .commit();

        /* Only load the fragment if we are in a tablet
        * */
        if (findViewById(R.id.fragment2_container_id) != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment2_container_id, FragmentItemDescription.newInstance())
                    .commit();

        }
    }

    private void checkInternalStoragePermissionGranted() {
        Log.d(TAG, "checkInternalStoragePermissionGranted: called!");

        if (Utils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            accessInternalStorageGranted = true;
            deleteTemporaryStorage();
        } else {
            Utils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void deleteTemporaryStorage () {
        Log.d(TAG, "deleteTemporaryStorage: called!");

        if (accessInternalStorageGranted) {

            String mainPath = app.getInternalStorage().getInternalFilesDirectory() + File.separator;
            String temporaryDir = mainPath + File.separator + Constants.TEMPORARY_DIRECTORY + File.separator;

            app.getInternalStorage().deleteDirectory(temporaryDir);

        }
    }
}
