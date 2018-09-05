package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.rest.fragments.handset.FragmentHandsetListListings;
import com.diegomfv.android.realestatemanager.ui.rest.fragments.tablet.FragmentTabletItemDescription;
import com.diegomfv.android.realestatemanager.ui.rest.fragments.tablet.FragmentTabletListListings;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * How crashes were solved:
 * 1. Modified the id of the view from activity_second_activity_text_view_main
 * to activity_main_activity_text_view_quantity
 * 2. Add String.valueOf() to convert int to String
 */

// TODO: 05/09/2018 Prices could be formatted with a price watcher
// TODO: 28/08/2018 Add a listener for when changing CURRENCY
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.textView_please_insert_data_id)
    TextView tvInsertData;

    @BindView(R.id.fragment1_container_id)
    FrameLayout fragment1Layout;

    private ActionBar actionBar;

    private boolean editModeActive;

    private boolean dataAvailable;

    private boolean accessInternalStorageGranted;

    private int currency;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.editModeActive = false;

        /* We delete the cache in MainActivity
         * */
        getRepository().deleteCacheAndSets();

        this.accessInternalStorageGranted = false;

        this.currency = Utils.readCurrentCurrencyShPref(this);

        dataAvailable = !getRepository().getSetOfBuildingTypes().isEmpty();

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        this.configureActionBar();

        if (dataAvailable) {
            this.loadFragmentOrFragments();
        }

        this.checkInternalStoragePermissionGranted();

        Log.w(TAG, "onCreate: getBitmapCache() =  " + getBitmapCache() + "+++++++++++++++");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Utils.updateCurrencyIconWhenMenuCreated(this, currency, menu, R.id.menu_change_currency_button);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case android.R.id.home: {
                Utils.launchActivity(this, AuthLoginActivity.class);

            }
            break;

            case R.id.menu_add_listing_button: {

                if (accessInternalStorageGranted) {
                    Utils.launchActivity(this, CreateNewListingActivity.class);

                } else {
                    ToastHelper.toastSomeAccessNotGranted(this);
                }
            }
            break;

            case R.id.menu_position_button: {
                Utils.launchActivity(this, PositionActivity.class);

            }
            break;

            case R.id.menu_change_currency_button: {

                changeCurrency();
                Utils.updateCurrencyIcon(this, currency, item);

            }
            break;

            case R.id.menu_edit_listing_button: {

                if (accessInternalStorageGranted) {
                    updateMode();

                } else {
                    ToastHelper.toastSomeAccessNotGranted(this);
                }

            }
            break;

            case R.id.menu_search_button: {

                // TODO: 26/08/2018 If there are no listings, do not launch the activity

                Utils.launchActivity(this, SearchEngineActivity.class);


            }
            break;
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

    private void configureActionBar() {
        Log.d(TAG, "configureActionBar: called!");

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeActionContentDescription(getResources().getString(R.string.go_back));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method used in the fragments
     */
    public boolean getEditModeActive() {
        Log.d(TAG, "getEditMode: called!");
        return editModeActive;
    }

    private void updateMode() {
        Log.d(TAG, "updateMode: called!");

        if (!editModeActive) {
            actionBar.setTitle("Edit mode");
            actionBar.setSubtitle("Click an element");
            editModeActive = true;
        } else {
            actionBar.setTitle("Real Estate Manager");
            actionBar.setSubtitle(null);
            editModeActive = false;
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void changeCurrency() {
        Log.d(TAG, "changeCurrency: called!");

        if (this.currency == 0) {
            this.currency = 1;
        } else {
            this.currency = 0;
        }
        Utils.writeCurrentCurrencyShPref(this, currency);
        loadFragmentOrFragments();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that loads one or two fragments depending on the device
     */
    private void loadFragmentOrFragments() {
        Log.d(TAG, "loadFragmentOrFragments: called!");

        hideTextViewShowFragments();

        if (findViewById(R.id.fragment2_container_id) == null) {
            /* Code for handsets
             * */

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment1_container_id, FragmentHandsetListListings.newInstance())
                    .commit();

        } else {
            /* Code for tablets
             * */
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment1_container_id, FragmentTabletListListings.newInstance())
                    .commit();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment2_container_id, FragmentTabletItemDescription.newInstance())
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

    private void createDirectories() {
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

    private void hideTextViewShowFragments() {
        Log.d(TAG, "hideTextViewData: called!");
        tvInsertData.setVisibility(View.GONE);
        fragment1Layout.setVisibility(View.VISIBLE);
        if (findViewById(R.id.fragment2_container_id) != null) {
            findViewById(R.id.fragment2_container_id).setVisibility(View.VISIBLE);
        }

    }
}
