package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;
import com.diegomfv.android.realestatemanager.viewmodel.PositionViewModel;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * Created by Diego Fajardo on 23/08/2018.
 */

public class PositionActivity extends BaseActivity {

    private static final String TAG = PositionActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.toolbar_id)
    Toolbar toolbar;

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

    private List<RealEstate> listOfListings;

    private int currency;

    //ViewModel
    private PositionViewModel positionViewModel;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.myLatitude = 0d;
        this.myLongitude = 0d;

        this.currency = Utils.readCurrentCurrencyShPref(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_position);
        this.unbinder = ButterKnife.bind(this);

        /* Configuring the toolbar
         * */
        this.configureToolbarBar();

        /* Loads the map
         * */
        if (isGooglePlayServicesOK()) {
            Utils.showMainContent(progressBarContent, mainLayout);
            initMap();
        }

        /* Creating and subscribing to the ViewModel
         * */
        this.createViewModel();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        this.unbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.currency_menu, menu);
        Utils.updateCurrencyIconWhenMenuCreated(this, currency, menu, R.id.menu_change_currency_button);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case R.id.menu_change_currency_button: {

                changeCurrency();
                Utils.updateCurrencyIcon(this, currency, item);
                updateMapWithPins();

            }
            break;

        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for listOfListings.
     */
    private List<RealEstate> getListOfListings() {
        if (listOfListings == null) {
            return listOfListings = new ArrayList<>();
        }
        return listOfListings;
    }

    /**
     * Getter for listOfMarkers.
     */
    private List<Marker> getListOfMarkers() {
        if (listOfMarkers == null) {
            return listOfMarkers = new ArrayList<>();
        }
        return listOfMarkers;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the toolbar.
     */
    private void configureToolbarBar() {
        Log.d(TAG, "configureToolbarBar: called!");

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
     * Method that modifies the currency variable
     * and writes the new info to sharedPreferences.
     */
    private void changeCurrency() {
        Log.d(TAG, "changeCurrency: called!");

        if (this.currency == 0) {
            this.currency = 1;
        } else {
            this.currency = 0;
        }
        Utils.writeCurrentCurrencyShPref(this, currency);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //VIEWMODEL

    /**
     * Method that creates the ViewModel.
     */
    private void createViewModel() {
        Log.d(TAG, "createViewModel: called!");

        PositionViewModel.Factory factory = new PositionViewModel.Factory(getApp());
        this.positionViewModel = ViewModelProviders
                .of(this, factory)
                .get(PositionViewModel.class);

        subscribeToModel(positionViewModel);
    }

    /**
     * Method to subscribe to the ViewModel.
     */
    private void subscribeToModel(PositionViewModel positionViewModel) {
        Log.d(TAG, "subscribeToModel: called!");

        if (positionViewModel != null) {

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
     * Google Play Services Version.
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
     * Method used to initialise the map.
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

                getDeviceLocation();

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
     * Method used to get the user's location.
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
                                Constants.MAPS_POSITION_DEFAULT_ZOOM);

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
     * Method used to move the camera in the map.
     */
    private void moveCamera(LatLng latLng, float zoom) {

        Log.d(TAG, "moveCamera: moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    /**
     * Method to update the map with pins.
     */
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

    /**
     * Method to add markers to the map.
     */
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
                .title(displayType(realEstate.getType())
                        + displayPrice(realEstate.getPrice()))
                .snippet(Utils.getAddressAsString(realEstate))
                .icon(getIconAccordingToAlreadySold(alreadySold));


        /* We fill the listOfMarkers and the map with the markers
         * */
        listOfMarkers.add(mMap.addMarker(options));
    }

    /**
     * Method that checks id a real estate has already been sold
     */
    private boolean realEstateAlreadySold(String dateSale) {
        Log.d(TAG, "realEstateAlreadySold: called!");
        if (dateSale == null || dateSale.equals("")) {
            return false;
        }
        return true;

    }

    /**
     * Method to that retrieves an icon depending on the "sale" state of the listing
     */
    private BitmapDescriptor getIconAccordingToAlreadySold(boolean alreadySold) {
        Log.d(TAG, "getIconAccordingToAlreadySold: called!");
        if (alreadySold) {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        }
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);

    }

    //MAP LISTENERS
    /**
     * Listener for when an item in the map is clicked
     */
    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Log.d(TAG, "onInfoWindowClick: called!");

            for (int i = 0; i < listOfListings.size(); i++) {

                if (marker.getSnippet().equals(Utils.getAddressAsString(listOfListings.get(i)))) {
                    launchDetailActivity(listOfListings.get(i));
                    break;
                }
            }
        }
    };

    /**
     * Listener for when the "myLocationButton" is clicked
     */
    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickedListener = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            Log.d(TAG, "onMyLocationButtonClick: called!");
            getDeviceLocation();
            return true;
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that displays the type according to the information in the database.
     * If the String is null or empty, it will return that the type is not available.
     * If not, it will return the type.
     */
    /* TODO could add " - " to Constants */
    private String displayType(String type) {
        Log.d(TAG, "displayType: called!");

        if (type == null || type.isEmpty()) {
            return "Type not available - ";

        } else {
            return type + " - ";
        }
    }

    /**
     * Method that displays the price according to the information in the database.
     * If the price is equal to 0, it will return that the price is not available.
     * If not, it will return the price.
     */
    private String displayPrice(float price) {
        Log.d(TAG, "displayPrice: called!");

        if (price == 0.0f) {
            return "Price not available";

        } else {
            return Utils.getCurrencySymbol(currency)
                    + " "
                    + Utils.getValueFormattedAccordingToCurrency(price, currency);
        }
    }

    /**
     * Launches detail activity
     * with a Parcelable (item clicked) carried by the intent
     */
    private void launchDetailActivity(RealEstate realEstate) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.SEND_PARCELABLE, realEstate);
        startActivity(intent);
    }
}
