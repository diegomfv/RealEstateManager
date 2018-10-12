package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.fragments.FragmentItemDescription;
import com.diegomfv.android.realestatemanager.ui.fragments.FragmentListListings;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

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
 * MainActivity displays a different layout depending on the size of the screen (handsets or
 * tablets). Additionally, it behaves different according to what activity launched it. If the activity
 * was launched from AuthLoginActivity, it will display all the listings in the database and
 * will behaves as "a main menu" with practically all the functionality available. If the activity
 * was launched from SearchEngineActivity, it will display the found articles (using the engine) and
 * almost all the functionality (except "change currency") will be off. The variable "mainMenu"
 * is responsible for carrying this information:
 * mainMenu = true --> AuthLoginActivity launched the activity (behave as MAIN MENU)
 * mainMenu = false --> SearchEngineActivity launched the activity (do not behave as MAIN MENU).
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

    /* This variable becomes true when edit mode is active. When this mode is active, clicking an
     * item of the recycler view will launche EditListingActivity instead of loading the information
     * of the listing
     * */
    private boolean editModeActive;

    /* Variable to differentiate when we are in the normal menu
     * and when we come from SearchEngineActivity (true: main menu).
     * This is done for code reuse
     */
    private boolean mainMenu;

    /* Variable to differentiate if the device is a handset or a tablet
     * true = is a handset
     * false = is a tablet
     * */
    private boolean deviceIsHandset;

    private int currency;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: called!");

        this.editModeActive = false;

        this.updateMainMenu();

        /* We delete the cache if mainMenu = true
         * */
        if (mainMenu) {
            getRepository().deleteCache();
        }

        this.currency = Utils.readCurrentCurrencyShPref(this);

        /* If the device is a tablet, we make it take a landscape configuration
         * */
        ifTabletObligateLandscape();

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        deviceIsHandset = updateDeviceIsHandset();

        this.configureToolBar();

        this.loadFragmentOrFragments();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");

        /* Depending on when we come from, we load a menu with all different
         * options or just a menu where you can change the currency only
         * */
        if (mainMenu) {
            getMenuInflater().inflate(R.menu.main_menu, menu);

        } else {
            getMenuInflater().inflate(R.menu.currency_menu, menu);
        }

        Utils.updateCurrencyIconWhenMenuCreated(this, currency, menu, R.id.menu_change_currency_button);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case R.id.menu_add_listing_button: {
                Utils.launchActivity(this, CreateNewListingActivity.class);
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
                updateMode();
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
                    createDirectories();
                }
            }
            break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for mainMenu field.
     * It will be used by the fragment that displays a list of items.
     */
    public boolean getMainMenu() {
        Log.d(TAG, "getMainMenu: called!");
        return mainMenu;
    }

    /**
     * Getter for deviceIsHandsetField
     * It will be used by the fragment that displays a list of items.
     */
    public boolean getDeviceIsHandset() {
        Log.d(TAG, "getDeviceIsHandset: called!");
        return deviceIsHandset;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to update mainMenu field. If we come from SearchEngineActivity, mainMenu will be
     * false. Otherwise, it will be true
     */
    private void updateMainMenu() {
        Log.d(TAG, "updateMainMenu: called!");
        Log.w(TAG, "updateMainMenu: getIntent.getExtras() = " + getIntent().getExtras());
        mainMenu = getIntent().getExtras() == null;
    }

    /**
     * Method to get if the device is a handset or a tablet.
     * "deviceIsHandset" variable stores
     * this information
     */
    private boolean updateDeviceIsHandset() {
        Log.d(TAG, "updateDeviceIsHandset: called!");
        return findViewById(R.id.fragment2_container_id) == null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the toolbar.
     * Depending on mainMenu, on the button behaves one way or another. With mainMenu = true,
     * user can return to AuthLoginAtivity via a dialog that will pop-up. With mainMenu = false,
     * the user will go to SearchEngineActivity
     */
    private void configureToolBar() {
        Log.d(TAG, "configureToolBar: called!");
        setSupportActionBar(toolbar);
        setOverflowButtonColor(toolbar, Color.WHITE);

        setToolbarTitle();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");
                if (mainMenu) {
                    launchAreYouSureDialog();
                } else {
                    Utils.launchActivity(MainActivity.this, SearchEngineActivity.class);
                }
            }
        });
    }

    /**
     * Method to set the toolbar title.
     */
    private void setToolbarTitle() {
        Log.d(TAG, "setToolbarTitle: called!");
        if (mainMenu) {
            toolbar.setTitle("Main Menu");
        } else {
            toolbar.setTitle("Found Listings");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method used in the fragments.
     */
    public boolean getEditModeActive() {
        Log.d(TAG, "getEditMode: called!");
        return editModeActive;
    }

    /**
     * Method that updates EditMode and the toolbar according to this state.
     */
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

    /**
     * Method that modifies the currency variable and writes the new info to sharedPreferences.
     * It also loads the fragment (or fragments).
     */
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
     * Method that checks if the database is empty. If it is not,
     * it loads one or two fragments depending on the device.
     */
    @SuppressLint("CheckResult")
    private void loadFragmentOrFragments() {
        Log.d(TAG, "loadFragmentOrFragments: called!");

        /* We check if the database is empty. If it is, we do not load the fragments.
         * If it is not empty, we load them.
         * */
        getRepository().getAllListingsRealEstateObservable()
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

                        if (!realEstates.isEmpty()) {

                            /* We hide the TextView that is displayed whent there are no listings
                             * yet in the database
                             * */
                            hideTextViewShowFragments();

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment1_container_id, FragmentListListings.newInstance())
                                    .commit();

                            if (!deviceIsHandset) {

                                /* If the device is a tablet, we load the second fragment
                                 * */
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment2_container_id, FragmentItemDescription.newInstance())
                                        .commit();
                            }
                        }
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
     * Method that creates the necessary
     * directories if they didn't exist yet.
     */
    private void createDirectories() {
        Log.d(TAG, "createDirectories: called");
        if (!getInternalStorage().isDirectoryExists(getImagesDir())) {
            getInternalStorage().createDirectory(getImagesDir());
        }

        if (!getInternalStorage().isDirectoryExists(getTemporaryDir())) {
            getInternalStorage().createDirectory(getTemporaryDir());
        }
    }

    /**
     * Method to hides the TextView that tells the user to insert new information and displays
     * the fragments
     */
    private void hideTextViewShowFragments() {
        Log.d(TAG, "hideTextViewData: called!");
        tvInsertData.setVisibility(View.GONE);
        fragment1Layout.setVisibility(View.VISIBLE);
        if (findViewById(R.id.fragment2_container_id) != null) {
            findViewById(R.id.fragment2_container_id).setVisibility(View.VISIBLE);
        }

    }

    /**
     * Method to launch a dialog asking the user if he/she is sure to leave the activity.
     * The information won't be saved.
     */
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

    /**
     * Method that checks if the device is a tablet (using the screen size) and, if it is,
     * it sets the orientation to landscape
     */
    private void ifTabletObligateLandscape() {
        Log.d(TAG, "ifTabletObligateLandscape: called!");
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
