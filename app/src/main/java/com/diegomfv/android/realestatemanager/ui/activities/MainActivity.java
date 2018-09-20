package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.fragments.handset.main.FragmentHandsetListListingsMain;
import com.diegomfv.android.realestatemanager.ui.fragments.tablet.main.FragmentTabletItemDescription;
import com.diegomfv.android.realestatemanager.ui.fragments.tablet.main.FragmentTabletListListings;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;
import com.diegomfv.android.realestatemanager.viewmodel.ListingsSharedViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * How crashes were solved:
 * 1. Modified the id of the view from activity_second_activity_text_view_main
 * to activity_main_activity_text_view_quantity
 * 2. Add String.valueOf() to convert int to String
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.toolbar_id)
    Toolbar toolbar;

    @BindView(R.id.textView_please_insert_data_id)
    TextView tvInsertData;

    @BindView(R.id.fragment1_container_id)
    FrameLayout fragment1Layout;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean editModeActive;

    private boolean accessInternalStorageGranted;

    private int currency;

    private List<RealEstate> listOfRealEstates;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.editModeActive = false;

        /* We delete the cache in MainActivity
         * */
        getRepository().deleteCache();

        this.accessInternalStorageGranted = false;

        this.currency = Utils.readCurrentCurrencyShPref(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        this.configureToolBar();

        this.checkInternalStoragePermissionGranted();

        // TODO: 17/09/2018 Differentiate here if
        // we come from any activity or SearchEngineActivity

        // TODO: 17/09/2018 Create a method for this
        if (searchEngineActivityLaunchedThisActivity()) {
            this.getAllFoundArticlesAndLoadFragmentOrFragments();
        } else {
            this.getAllListingsAndLoadFragmentOrFragments();
        }
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
                if (!getRepository().getDatabaseIsEmpty()) {
                    Utils.launchActivity(this, SearchEngineActivity.class);

                } else {
                    ToastHelper.toastShort(this, "There are no listing to search for...");
                }
            }
            break;

            case R.id.menu_loan_simulator: {
                Utils.launchActivity(this, LoanSimulatorActivity.class);
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

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called!");
        launchAreYouSureDialog();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean searchEngineActivityLaunchedThisActivity() {
        Log.d(TAG, "searchEngineActivityLaunchedThisActivity: called!");
        if (getIntent() != null
                && getIntent().getExtras() != null) {
            return getIntent().getExtras().getBoolean(Constants.INTENT_FROM_SEARCH_ENGINE);
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that returns the list of Real Estates in the database.
     * It is accessible for the fragment
     */
    public List<RealEstate> getListOfRealEstates() {
        Log.d(TAG, "getListOfRealEstates: called!");
        if (listOfRealEstates == null) {
            return listOfRealEstates = new ArrayList<>();
        }
        return listOfRealEstates;
    }

    private void setListOfRealEstates(List<RealEstate> list) {
        Log.d(TAG, "setListOfRealEstates: called!");
        listOfRealEstates = list;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureToolBar() {
        Log.d(TAG, "configureToolBar: called!");

        setSupportActionBar(toolbar);
        setOverflowButtonColor(toolbar, Color.WHITE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");
                onBackPressed();
            }
        });
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
            toolbar.setTitle("Edit mode");
            toolbar.setSubtitle("Click an element");
            editModeActive = true;
        } else {
            toolbar.setTitle("Real Estate Manager");
            toolbar.setSubtitle(null);
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

    @SuppressLint("CheckResult")
    private void getAllFoundArticlesAndLoadFragmentOrFragments() {
        Log.d(TAG, "getAllFoundArticlesAndLoadFragmentOrFragments: called!");
        getRepository().getAllFoundListingsRealEstateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<RealEstate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");

                    }

                    @Override
                    public void onNext(List<RealEstate> realEstates) {
                        Log.d(TAG, "onNext: " + realEstates);
                        setListOfRealEstates(realEstates);
                        loadFragmentOrFragments();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");

                    }
                });
    }


    @SuppressLint("CheckResult")
    private void getAllListingsAndLoadFragmentOrFragments() {
        Log.d(TAG, "getAllListingsAndLoadFragmentOrFragments: called!");
        getRepository().getAllListingsRealEstateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new io.reactivex.Observer<List<RealEstate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");

                    }

                    @Override
                    public void onNext(List<RealEstate> realEstates) {
                        Log.d(TAG, "onNext: " + realEstates);
                        setListOfRealEstates(realEstates);
                        loadFragmentOrFragments();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");

                    }
                });
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
                    .replace(R.id.fragment1_container_id, FragmentHandsetListListingsMain.newInstance())
                    .commit();

        } else {

            /* Code for tablets
             * */
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment1_container_id, FragmentTabletListListings.newInstance())
                    .commit();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment2_container_id, FragmentTabletItemDescription.newInstance())
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

    private void launchAreYouSureDialog() {
        Log.d(TAG, "launchAreYouSureDialog: called!");
        Utils.launchSimpleDialog(this,
                "Are you sure you want to leave?",
                "Closing the session",
                "YES, I AM SURE",
                "NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: called!");
                        Utils.launchActivityClearStack(MainActivity.this, AuthLoginActivity.class);
                    }
                });
    }
}
