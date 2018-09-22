package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.fragments.FragmentHandsetItemDescription;
import com.diegomfv.android.realestatemanager.ui.fragments.FragmentListListings;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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

    private boolean editModeActive;

    /* Variable to differentiate when we are in the normal menu
    and when we come from SearchEngineActivity (true: main menu).
    * This is done for code reuse
    */
    private boolean mainMenu;

    /* Variable to differentiate if the device is a handset or a tablet
     * true = is a handset
     * false = is a tablet
     * */
    private boolean deviceIsHandset;

    private boolean accessInternalStorageGranted;

    private int currency;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.editModeActive = false;

        this.updateMainMenu();

        /* We delete the cache if mainMenu = true
         * */
        if (mainMenu) {
            getRepository().deleteCache();
        }

        this.accessInternalStorageGranted = false;

        this.currency = Utils.readCurrentCurrencyShPref(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        deviceIsHandset = updateDeviceIsHandset();

        this.configureToolBar();

        this.checkInternalStoragePermissionGranted();

        this.loadFragmentOrFragments();

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
    public boolean getDeviceIsHanset() {
        Log.d(TAG, "getDeviceIsHanset: called!");
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
     * Method to set the toolbar title
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

    /**
     * Method that loads one or two fragments depending on the device
     */
    private void loadFragmentOrFragments() {
        Log.d(TAG, "loadFragmentOrFragments: called!");

        hideTextViewShowFragments();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment1_container_id, FragmentListListings.newInstance())
                .commit();

        if (!deviceIsHandset) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment2_container_id, FragmentHandsetItemDescription.newInstance())
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
