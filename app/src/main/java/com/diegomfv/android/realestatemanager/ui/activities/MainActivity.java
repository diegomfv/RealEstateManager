package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.rest.FragmentItemDescription;
import com.diegomfv.android.realestatemanager.ui.rest.FragmentListListings;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;
import com.diegomfv.android.realestatemanager.utils.Utils;

/** How crashes were solved:
 * 1. Modified the id of the view from activity_second_activity_text_view_main
 * to activity_main_activity_text_view_quantity
 * 2. Add String.valueOf() to convert int to String
 * */

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean accessInternalStorageGranted;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.accessInternalStorageGranted = false;

        /* We delete the cache in MainActivity
        * */
        getRepository().deleteCache();

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_main);

        this.loadFragmentOrFragments();

        this.checkInternalStoragePermissionGranted();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case R.id.menu_add_listing_button: {

                if (accessInternalStorageGranted) {
                    Utils.launchActivity(this, CreateNewListingActivity.class);

                } else {
                    ToastHelper.toastSomeAccessNotGranted(this);
                }


            } break;

            case R.id.menu_position_button: {
                Utils.launchActivity(this, PositionActivity.class);

            } break;

            case R.id.menu_change_currency_button: {

                // TODO: 23/08/2018 Code

            } break;

            case R.id.menu_edit_listing_button: {

                if (accessInternalStorageGranted) {
                    Utils.launchActivity(this, EditListingActivity.class);

                } else {
                    ToastHelper.toastSomeAccessNotGranted(this);
                }

            } break;

            case R.id.menu_search_button: {

                // TODO: 26/08/2018 If there are no listings, do not launch the activity

                Utils.launchActivity(this, SearchEngineActivity.class);


            } break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called!");

        switch (requestCode) {

            case Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] != -1) {
                    accessInternalStorageGranted = true;
                    createDirectories();
                }
            }
            break;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////



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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkInternalStoragePermissionGranted() {
        Log.d(TAG, "checkInternalStoragePermissionGranted: called!");

        if (Utils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            accessInternalStorageGranted = true;
            createDirectories();

        } else {
            Utils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void createDirectories () {
        Log.d(TAG, "createDirectories: called");

        if (accessInternalStorageGranted) {
            if (!getInternalStorage().isDirectoryExists(getImagesDir())) {
                getInternalStorage().createDirectory(getImagesDir());
            }

            if (!getInternalStorage().isDirectoryExists(getTemporaryDir())) {
                getInternalStorage().createDirectory(getTemporaryDir());
            }
        }
    }
}
