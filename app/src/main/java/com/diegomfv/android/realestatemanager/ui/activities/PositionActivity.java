package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppDatabase;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;
import com.diegomfv.android.realestatemanager.utils.Utils;
import com.diegomfv.android.realestatemanager.viewmodel.ListingsSharedViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.snatik.storage.Storage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 23/08/2018.
 */

public class PositionActivity extends AppCompatActivity {

    private static final String TAG = PositionActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.progress_bar_content_id)
    LinearLayout progressBarContent;

    @BindView(R.id.main_layout_id)
    FrameLayout mainLayout;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private FusedLocationProviderClient mFusedLocationProviderClient; //To get the location of the current user

    private GoogleMap mMap;

    private double myLatitude;
    private double myLongitude;

    private List<Marker> listOfMarkers;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBar actionBar;

    private boolean deviceLocationPermissionGranted;

    private List<RealEstate> listOfListings;

    private int currency;

    //ViewModel
    private ListingsSharedViewModel positionViewModel;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.deviceLocationPermissionGranted = false;

        this.myLatitude = 0d;
        this.myLongitude = 0d;

        this.currency = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_position);
        this.unbinder = ButterKnife.bind(this);

        this.configureActionBar();

        this.checkDeviceLocationPermissionGranted();

        this.createModel();

        this.subscribeToModel(positionViewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        this.unbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called!");

        switch (requestCode) {

            case Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] != -1) {
                    deviceLocationPermissionGranted = true;

                    if (isGooglePlayServicesOK()) {
                        Utils.showMainContent(progressBarContent, mainLayout);
                        initMap();
                    }
                }
            }
            break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //SINGLETON GETTERS

    private RealEstateManagerApp getApp () {
        Log.d(TAG, "getApp: called");
        return (RealEstateManagerApp) getApplication();
    }

    private AppDatabase getAppDatabase () {
        Log.d(TAG, "getAppDatabase: called!");
        return getApp().getDatabase();
    }

    private Storage getInternalStorage() {
        Log.d(TAG, "getInternalStorage: called!");
        return getApp().getInternalStorage();
    }

    private RealEstate getRealEstateCache () {
        Log.d(TAG, "getRealEstateCache: called!");
        return getApp().getRepository().getRealEstateCache();
    }

    private List<ImageRealEstate> getListOfImagesRealEstateCache () {
        Log.d(TAG, "getListOfImagesRealEstateCache: called!");
        return getApp().getRepository().getListOfImagesRealEstateCache();
    }

    private List<PlaceRealEstate> getListOfPlacesRealEstateCache() {
        Log.d(TAG, "getListOfImagesRealEstateCache: called!");
        return getApp().getRepository().getListOfPlacesRealEstateCache();
    }

    private List<RealEstate> getListOfListings () {
        if (listOfListings == null) {
            return listOfListings = new ArrayList<>();
        }
        return listOfListings;
    }

    private List<Marker> getListOfMarkers () {
        if (listOfMarkers == null) {
            return listOfMarkers = new ArrayList<>();
        }
        return listOfMarkers;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.position_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case R.id.menu_change_currency_button: {

                changeCurrencyIcon(item);
                changeCurrency();

                updateMapWithPins();

            } break;

            case android.R.id.home: {
                Utils.launchActivity(this, MainActivity.class);

            } break;

        }
        return super.onOptionsItemSelected(item);
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

    private void changeCurrencyIcon (MenuItem item) {
        Log.d(TAG, "changeCurrencyIcon: called!");

        if (this.currency == 0) {
           item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_euro_symbol_white_24dp));

        } else {
            item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_dollar_symbol_white_24dp));
        }
    }

    private void changeCurrency() {
        Log.d(TAG, "changeCurrency: called!");

        if (this.currency == 0) {
            this.currency = 1;
        } else {
            this.currency = 0;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkDeviceLocationPermissionGranted() {
        Log.d(TAG, "checkInternalStoragePermissionGranted: called!");

        if (Utils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (Utils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                deviceLocationPermissionGranted = true;

                if (isGooglePlayServicesOK()) {
                    Utils.showMainContent(progressBarContent, mainLayout);
                    initMap();
                }
            }

        } else {
            Utils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //VIEWMODEL

    private void createModel () {
        Log.d(TAG, "createModel: called!");

        ListingsSharedViewModel.Factory factory = new ListingsSharedViewModel.Factory(getApp());
        this.positionViewModel = ViewModelProviders
                .of(this, factory)
                .get(ListingsSharedViewModel.class);


    }

    private void subscribeToModel (ListingsSharedViewModel listingsViewModel) {
        Log.d(TAG, "subscribeToModel: called!");

        if (listingsViewModel != null) {

            this.positionViewModel.getObservableListOfListings().observe(this, new Observer<List<RealEstate>>() {
                @Override
                public void onChanged(@Nullable List<RealEstate> realEstates) {
                    Log.d(TAG, "onChanged: called!");

                    listOfListings = realEstates;
                    updateMapWithPins();

                }
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //MAP

    /**
     * Checks if the user has the correct
     * Google Play Services Version
     */
    public boolean isGooglePlayServicesOK() {
        Log.d(TAG, "isGooglePlayServicesOK: called!");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            //Everything is fine and the user can make map requests
            Log.d(TAG, "isGooglePlayServicesOK: Google Play Services is working");
            return true;

        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //There is an error but we can resolve it
            Log.d(TAG, "isGooglePlayServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, available, Constants.REQUEST_ERROR_DIALOG);
            dialog.show();

        } else {
            Log.d(TAG, "isGooglePlayServicesOK: an error occurred; you cannot make map requests");
            ToastHelper.toastLong(this, this.getResources().getString(R.string.cant_make_map_requests));

        }
        return false;
    }

    /**
     * Method used to initialise the map
     */
    private void initMap() {
        Log.d(TAG, "initMap: called!");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            //This SuppressLint removes the compiler request asking for a explicit permission for mMap.setMyLocationEnabled(true)
            // which is not needed because "if (Utils.hasPermissions(this, Repo.PERMISSIONS))" already checks
            // the permission
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                Log.d(TAG, "onMapReady: map is ready");
                mMap = googleMap;

                if (deviceLocationPermissionGranted) {
                    /* We get the device's location
                     * */
                    getDeviceLocation();
                }

                /*Listener for when clicking the info window in a map
                 * */
                if (mMap != null) {

                    mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
                    mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickedListener);

                }
            }
        });
    }

    /**
     * Method used to get the user's location
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: called!");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {

            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful() && task.getResult() != null) {
                        //&& task.getResult() != null -- allows you to avoid crash if the app
                        // did not get the location from the device (= currentLocation = null)
                        Log.d(TAG, "onComplete: found location!");
                        Location currentLocation = (Location) task.getResult();

                        Log.d(TAG, "onComplete: current location: getLatitude(), getLongitude() " + (currentLocation.getLatitude()) + ", " + (currentLocation.getLongitude()));

                        myLatitude = currentLocation.getLatitude();
                        myLongitude = currentLocation.getLongitude();

                        mMap.setMyLocationEnabled(true); //displays the blue marker at your location
                        mMap.getUiSettings().setMyLocationButtonEnabled(true); //displays the button that allows you to center your position

                        moveCamera(
                                new LatLng(myLatitude, myLongitude),
                                Constants.MAPS_DEFAULT_ZOOM);

                        updateMapWithPins();

                    } else {
                        Log.d(TAG, "onComplete: current location is null");
                    }

                }
            });

        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }
    }

    /**
     * Method used to move the camera in the map
     */
    private void moveCamera(LatLng latLng, float zoom) {

        Log.d(TAG, "moveCamera: moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    private void updateMapWithPins() {
        Log.d(TAG, "updateMapWithPins: called!");

        if (mMap != null) {

            if (!getListOfListings().isEmpty()) {
                Log.i(TAG, "displayPinsInMap: listOfRestaurants IS NOT EMPTY");

                /* We delete all the elements of the listOfMarkers and clear the map
                 * */
                getListOfMarkers().clear();
                mMap.clear();

                for (int i = 0; i < getListOfListings().size(); i++) {
                    addMarkerToMap(getListOfListings().get(i));
                }

            } else {
                Log.d(TAG, "updateMapWithPins: list is EMPTY");
            }

        } else {
            Log.d(TAG, "updateMapWithPins: myMap is null");
        }
    }

    private void addMarkerToMap(RealEstate realEstate) {
        Log.d(TAG, "addMarkerToMap: called!");

        MarkerOptions options;

        LatLng latLng = new LatLng(
                realEstate.getLatitude(),
                realEstate.getLongitude());

        /* We make a difference between the listings
         * that have been already sold and those which has not
         * */

        boolean alreadySold = realEstateAlreadySold(realEstate.getDateSale());

        options = new MarkerOptions()
                .position(latLng)
                .title(realEstate.getType()
                        + " - "
                        + getPriceInProperCurrency(realEstate.getPrice(), currency)
                        + Utils.getCurrencySymbol(currency))
                .snippet(Utils.getAddressAsString(realEstate))
                .icon(getIconAccordingToAlreadySold(alreadySold));


        /* We fill the listOfMarkers and the map with the markers
         * */
        listOfMarkers.add(mMap.addMarker(options));
    }

    private String getPriceInProperCurrency (int price, int currency) {
        Log.d(TAG, "getPriceInProperCurrency: called!");

        switch (currency) {

            case 0: {
                return Utils.formatToDecimals(price, currency);
            }

            case 1: {
                int priceInEuros = (int) Utils.convertDollarToEuro((float) price);
                return Utils.formatToDecimals(priceInEuros, currency);
            }

            default: {
                return Utils.formatToDecimals(price, currency);
            }
        }
    }

    private boolean realEstateAlreadySold(String dateSale) {
        Log.d(TAG, "realEstateAlreadySold: called!");
        if (dateSale == null || dateSale.equals("")) {
            return false;
        }
        return true;

    }

    private BitmapDescriptor getIconAccordingToAlreadySold(boolean alreadySold) {
        Log.d(TAG, "getIconAccordingToAlreadySold: called!");
        if (alreadySold) {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        }
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);

    }

    //MAP LISTENERS

    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Log.d(TAG, "onInfoWindowClick: called!");

            for (int i = 0; i < listOfListings.size(); i++) {

                if (marker.getSnippet().equals(listOfListings.get(i).getAddress())) {
                    launchDetailActivity(listOfListings.get(i));
                    break;
                }
            }
        }
    };

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickedListener = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            Log.d(TAG, "onMyLocationButtonClick: called!");

            getDeviceLocation();

            return true; // TODO: 23/08/2018 Check what this does
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Launches detail activity
     * with a Parcelable (item clicked) carried by the intent
     * */
    private void launchDetailActivity (RealEstate realEstate) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.SEND_PARCELABLE, realEstate);
        startActivity(intent);
    }
}
