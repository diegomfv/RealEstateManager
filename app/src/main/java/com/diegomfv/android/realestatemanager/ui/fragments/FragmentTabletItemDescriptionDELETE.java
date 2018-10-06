package com.diegomfv.android.realestatemanager.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaHorizontalDescr;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseFragment;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;
import com.diegomfv.android.realestatemanager.viewmodel.ListingsSharedViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */

// TODO: 16/09/2018 Does not show Rooms correctly
// TODO: 16/09/2018 Does not display the map correctly
// TODO: 11/09/2018 Add "NOT AVAILABLE"

/**
 * This fragment is only used in tablets. The ViewModel retrieves the information of
 * the MutableLiveData object. This information is used to fill the layout.
 */
public class FragmentTabletItemDescriptionDELETE extends BaseFragment {

    private static final String TAG = FragmentHandsetItemDescription.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.card_view_recycler_view_media_id)
    CardView cardViewRecyclerView;

    @BindView(R.id.card_view_recyclerView_media_id)
    RecyclerView recyclerViewMedia;

    @BindView(R.id.card_view_description_id)
    CardView cardViewDescription;

    @BindView(R.id.textView_description_id)
    TextView tvDescription;

    @BindView(R.id.textView_surface_area_id)
    TextView tvSurfaceArea;

    @BindView(R.id.textView_numberOfRooms_id)
    TextView tvNumberOtherRooms;

    @BindView(R.id.textView_numberOfBathrooms_id)
    TextView tvNumberBathrooms;

    @BindView(R.id.textView_numberOfBedrooms_id)
    TextView tvNumberBedrooms;

    @BindView(R.id.textView_street_id)
    TextView tvStreet;

    @BindView(R.id.textView_locality_id)
    TextView tvLocality;

    @BindView(R.id.textView_city_id)
    TextView tvCity;

    @BindView(R.id.textView_postcode_id)
    TextView tvPostCode;

    @BindView(R.id.textView_price_id)
    TextView tvPrice;

    @BindView(R.id.textView_sold_id)
    TextView tvSold;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private RealEstate realEstate;

    private List<ImageRealEstate> listOfImagesRealEstate;

    private List<PlaceRealEstate> listOfPlacesRealEstate;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int currency;

    //RecyclerView Adapter
    private RVAdapterMediaHorizontalDescr adapter;

    private ListingsSharedViewModel listingsSharedViewModel;

    private boolean deviceLocationPermissionGranted;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private FusedLocationProviderClient mFusedLocationProviderClient; //To get the location of the current user

    private GoogleMap mMap;

    private List<Marker> listOfMarkers;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that returns
     * an instance of the Fragment
     */
    public static FragmentTabletItemDescriptionDELETE newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentTabletItemDescriptionDELETE();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called!");

        this.deviceLocationPermissionGranted = false;

        if (getRootMainActivity() != null) {
            this.currency = Utils.readCurrentCurrencyShPref(getRootMainActivity());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        View view = inflater.inflate(R.layout.fragment_item_description, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        /* We load the map
         * */
        if (isGooglePlayServicesOK()) {
            initMap();
        }

        this.createModel();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        this.unbinder.unbind();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter of
     * the real estate object
     */
    private RealEstate getRealEstate() {
        Log.d(TAG, "getRealEstate: called!");
        return realEstate;
    }

    /**
     * Setter of
     * the real estate object
     */
    private void setRealEstate(RealEstate realEstate) {
        Log.d(TAG, "setRealEstate: called!");
        this.realEstate = realEstate;
    }

    /**
     * Getter for the list
     * of images of the real estate object
     */
    private List<ImageRealEstate> getListOfImagesRealEstate() {
        Log.d(TAG, "getRelatedImages: called!");
        if (listOfImagesRealEstate == null) {
            return listOfImagesRealEstate = new ArrayList<>();
        }
        return listOfImagesRealEstate;
    }

    /**
     * Getter of
     * the real estate object
     */
    private List<PlaceRealEstate> getListOfPlacesRealEstate() {
        Log.d(TAG, "getListOfPlacesRealEstate: called!");
        if (listOfPlacesRealEstate == null) {
            return listOfPlacesRealEstate = new ArrayList<>();
        }
        return listOfPlacesRealEstate;
    }

    /**
     * Getter of
     * the real estate object
     */
    public List<Marker> getListOfMarkers() {
        if (listOfMarkers == null) {
            return listOfMarkers = new ArrayList<>();
        }
        return listOfMarkers;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to fill the layour with the information from the real estate object.
     */
    private void fillLayoutWithRealEstateInfo() {
        Log.d(TAG, "fillLayoutWithRealEstateInfo: called!");
        setDescription();
        setSurfaceArea();
        setRooms();
        setAddress();
        setPrice();
        setSoldState();
    }

    /**
     * Method to set the text of the view related to the description using the information
     * of the real estate object.
     */
    private void setDescription() {
        Log.d(TAG, "setDescription: called!");

        if (getRealEstate().getDescription().isEmpty()) {
            cardViewDescription.setVisibility(View.GONE);

        } else {
            tvDescription.setText(getRealEstate().getDescription());
        }
    }

    /**
     * Method to set the text of the view related to the surface area using the information
     * of the real estate object.
     */
    private void setSurfaceArea() {
        Log.d(TAG, "setSurfaceArea: called!");
        if (getRealEstate().getSurfaceArea() == 0) {
            tvSurfaceArea.setText(R.string.information_not_available);

        } else {
            tvSurfaceArea.setText(String.valueOf(getRealEstate().getSurfaceArea()));
        }
    }

    /**
     * Method to set the texts of the views related to the rooms using the information
     * of the real estate object.
     */
    public void setRooms() {
        tvNumberBedrooms.setText(String.valueOf("Bedrooms -- " + getRealEstate().getRooms().getBedrooms()));
        tvNumberBathrooms.setText(String.valueOf("Bathrooms -- " + getRealEstate().getRooms().getBathrooms()));
        tvNumberOtherRooms.setText(String.valueOf("Other Rooms -- " + getRealEstate().getRooms().getOtherRooms()));
    }

    /**
     * Method to set the text of the views related to the addresses using the information
     * of the real estate object.
     */
    private void setAddress() {
        Log.d(TAG, "setLocation: called!");
        tvStreet.setText(getRealEstate().getAddress().getStreet());
        tvLocality.setText(getRealEstate().getAddress().getLocality());
        tvCity.setText(getRealEstate().getAddress().getCity());
        tvPostCode.setText(getRealEstate().getAddress().getPostcode());
    }

    // TODO: 28/08/2018 Use Placeholders

    /**
     * Method to set the text of the view related to the price using the information
     * of the real estate object.
     */
    @SuppressLint("SetTextI18n")
    private void setPrice() {
        Log.d(TAG, "setPrice: called!");
        if (getRealEstate().getPrice() == 0.0f) {
            tvPrice.setText(R.string.information_not_available);

        } else {
            tvPrice.setText(
                    Utils.getCurrencySymbol(currency)
                            + " "
                            + Utils.getValueFormattedAccordingToCurrency(realEstate.getPrice(), currency));
        }
    }

    /**
     * Method to set the text of the view related to the "sale state" using the information
     * of the real estate object.
     */
    private void setSoldState() {
        Log.d(TAG, "setSoldState: called!");
        if (getRealEstate().getDateSale() != null) {
            tvSold.setText("SOLD on " + getRealEstate().getDateSale());
            tvSold.setTextColor(getResources().getColor(android.R.color.white));
            tvSold.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            tvSold.setText("On Sale");
            tvSold.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvSold.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that creates the model to display the necessary information
     */
    private void createModel() {
        Log.d(TAG, "createModel: called!");

        if (getRootMainActivity() != null) {

            ListingsSharedViewModel.Factory factory = new ListingsSharedViewModel.Factory(getApp());
            this.listingsSharedViewModel = ViewModelProviders
                    .of(getRootMainActivity(), factory)
                    .get(ListingsSharedViewModel.class);

            subscribeToModel();
        }
    }

    /**
     * Method to subscribe to model. Depending on mainMenu variable (see MainActivity) we show
     * some information or another ("mainMenu = true" displays all the listings in the database
     * whereas "mainMenu = false" displays all the found articles).
     */
    private void subscribeToModel() {
        Log.d(TAG, "subscribeToModel: called!");

        if (listingsSharedViewModel != null) {

            listingsSharedViewModel.getItemSelected().observe(this, new Observer<RealEstate>() {
                @Override
                public void onChanged(@Nullable RealEstate realEstate) {
                    Log.d(TAG, "onChanged: called! --> real estate = " + realEstate);
                    setRealEstate(realEstate);

                    /* We get all the related info to the real estate and immediately configure
                     * the layout with all that information
                     * */
                    setInfoRelatedToRealEstate();
                }
            });
        }
    }

    /**
     * Method that uses RxJava to retrieve all the information related to the real estate (already
     * retrieved by the ViewModel). When all the information is obtained, the layout is filled with
     * that information.
     */
    @SuppressLint("CheckResult")
    private void setInfoRelatedToRealEstate() {
        Log.d(TAG, "setInfoRelatedToRealEstate: called!");

        getRepository().getAllImagesRealEstateObservable()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new io.reactivex.Observer<List<ImageRealEstate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe images: called!");

                    }

                    @Override
                    public void onNext(List<ImageRealEstate> imageRealEstates) {
                        Log.d(TAG, "onNext images: called!");

                        /* Firstly, we clear the listOfImages related to the real estate */
                        getListOfImagesRealEstate().clear();

                        /* Then we fill the listOfImagesRealEstate with those images
                         * related to the realEstate
                         * */
                        for (int i = 0; i < getRealEstate().getListOfImagesIds().size(); i++) {

                            for (int j = 0; j < imageRealEstates.size(); j++) {

                                if (getRealEstate().getListOfImagesIds().get(i).equals(imageRealEstates.get(j).getId())) {
                                    getListOfImagesRealEstate().add(imageRealEstates.get(j));
                                }
                            }
                        }

                        /* Once done,
                        we do the same with the places
                        * */
                        getRepository().getAllPlacesRealEstateObservable()
                                .observeOn(Schedulers.io())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new io.reactivex.Observer<List<PlaceRealEstate>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Log.d(TAG, "onSubscribe places: called!");

                                    }

                                    @Override
                                    public void onNext(List<PlaceRealEstate> placeRealEstates) {
                                        Log.d(TAG, "onNext places: called!");

                                        /* Firstly, we clear the listOfPlaces related to the real estate */
                                        getListOfPlacesRealEstate().clear();

                                        /* Then we fill the listOfPlacesRealEstate with those places
                                         * related to the realEstate
                                         * */
                                        for (int i = 0; i < getRealEstate().getListOfNearbyPointsOfInterestIds().size(); i++) {

                                            for (int j = 0; j < placeRealEstates.size(); j++) {

                                                if (getRealEstate().getListOfNearbyPointsOfInterestIds().get(i)
                                                        .equals(placeRealEstates.get(j).getId())) {
                                                    getListOfPlacesRealEstate().add(placeRealEstates.get(j));
                                                }
                                            }
                                        }

                                        /* Once done,
                                         * we can set the layout
                                         * */
                                        configureRecyclerView();
                                        fillLayoutWithRealEstateInfo();
                                        updateMapWithPins();

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "onError places: " + e.getMessage());

                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d(TAG, "onComplete places: called!");

                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError places: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete places: called!");

                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the RecyclerView.
     */
    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");

        if (getRealEstate() != null) {

            if (getRealEstate().getListOfImagesIds() == null
                    || realEstate.getListOfImagesIds().size() == 0) {
                cardViewRecyclerView.setVisibility(View.GONE);

            } else {

                if (getRootMainActivity() != null) {

                    this.recyclerViewMedia.setHasFixedSize(true);
                    this.recyclerViewMedia.setLayoutManager(new LinearLayoutManager(
                            getRootMainActivity(), LinearLayoutManager.HORIZONTAL, false));
                    this.adapter = new RVAdapterMediaHorizontalDescr(
                            getRootMainActivity(),
                            getRepository(),
                            getInternalStorage(),
                            getImagesDir(),
                            getRealEstate(),
                            getGlide(),
                            currency);

                    this.recyclerViewMedia.setAdapter(this.adapter);

                    this.configureOnClickRecyclerView();

                }
            }
        }
    }

    /**
     * Method to configure the onClick listeners of the RecyclerView.
     */
    private void configureOnClickRecyclerView() {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerViewMedia)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerViewMedia, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");
                        Log.w(TAG, "onItemClicked: key = " + adapter.getKey(position));
                        String key = adapter.getKey(position);

                        for (int i = 0; i < getListOfImagesRealEstate().size(); i++) {
                            if (getListOfImagesRealEstate().get(i).getId().equals(key)) {
                                ToastHelper.toastShort(getRootMainActivity(), getListOfImagesRealEstate().get(i).getDescription());
                            }
                        }
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that checks if the user has the correct
     * Google Play Services Version in order
     * to load the map
     */
    public boolean isGooglePlayServicesOK() {
        Log.d(TAG, "isGooglePlayServicesOK: called!");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getRootMainActivity());

        if (available == ConnectionResult.SUCCESS) {
            //Everything is fine and the user can make map requests
            Log.d(TAG, "isGooglePlayServicesOK: Google Play Services is working");
            return true;

        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //There is an error but we can resolve it
            Log.d(TAG, "isGooglePlayServicesOK: an error occurred but we can fix it");
            if (getRootMainActivity() != null) {
                Dialog dialog = GoogleApiAvailability.getInstance()
                        .getErrorDialog(getRootMainActivity(), available, Constants.REQUEST_ERROR_DIALOG);
                dialog.show();
            }

        } else {
            Log.d(TAG, "isGooglePlayServicesOK: an error occurred; you cannot make map requests");
            if (getRootMainActivity() != null) {
                ToastHelper.toastLong(getRootMainActivity(), this.getResources().getString(R.string.cant_make_map_requests));
            }
        }
        return false;
    }

    /**
     * Method that initialises the map
     */
    private void initMap() {
        Log.d(TAG, "initMap: called!");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);

        Log.i(TAG, "initMap: " + mapFragment);

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
                    mMap.setOnMarkerClickListener(onMarkerClickListener);
                }
            }
        });

    }

    /**
     * Method that retrieves the user's location
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: called!");

        if (getRootMainActivity() != null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getRootMainActivity());
        }

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

                        mMap.setMyLocationEnabled(false); //displays the blue marker at your location
                        mMap.getUiSettings().setMyLocationButtonEnabled(false); //displays the button that allows you to center your position

                        moveCamera(
                                new LatLng(realEstate.getLatitude(), realEstate.getLongitude()),
                                Constants.MAPS_POSITION_DEFAULT_ZOOM);

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

    /**
     * Fills the map with pins:
     * real estate location
     * nearby points of interest location
     */
    private void updateMapWithPins() {
        Log.d(TAG, "updateMapWithPins: called!");

        if (mMap != null && getRealEstate() != null) {

            if (!getListOfPlacesRealEstate().isEmpty()) {
                Log.i(TAG, "displayPinsInMap: listOfPlaces is NOT EMPTY");

                /* We delete all the elements of the listOfMarkers and clear the map
                 * */
                getListOfMarkers().clear();
                mMap.clear();

                /* We add a pin with the real estate
                 * */
                addMarkerToMapRealEstate(getRealEstate());

                /* We get all those places that are related to the real estate (if there are any)
                 * */
                if (getRealEstate().getListOfNearbyPointsOfInterestIds() != null
                        && getRealEstate().getListOfNearbyPointsOfInterestIds().size() > 0) {

                    List<String> listOfPlacesKeys = getRealEstate().getListOfNearbyPointsOfInterestIds();

                    for (int i = 0; i < listOfPlacesKeys.size(); i++) {
                        for (int j = 0; j < getListOfPlacesRealEstate().size(); j++) {
                            if (listOfPlacesKeys.get(i).equals(getListOfPlacesRealEstate().get(j).getId())) {
                                addMarkerToMapPlaceRealEstate(getListOfPlacesRealEstate().get(j));
                            }
                        }
                    }
                }


            } else {
                Log.d(TAG, "updateMapWithPins: list is EMPTY");
            }

        } else {
            Log.d(TAG, "updateMapWithPins: myMap is null");
        }
    }

    /**
     * Adds the real estate pin to the map
     */
    private void addMarkerToMapRealEstate(RealEstate realEstate) {
        Log.d(TAG, "addMarketToMapRealEstate: called!");

        MarkerOptions options;

        LatLng latLng = new LatLng(
                realEstate.getLatitude(),
                realEstate.getLongitude());

        options = new MarkerOptions()
                .position(latLng)
                .title(realEstate.getType())
                .snippet(Utils.getAddressAsString(realEstate))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


        /* We fill the listOfMarkers and the map with the markers
         * */
        getListOfMarkers().add(mMap.addMarker(options));

    }

    /**
     * Adds the point of interest to the map
     */
    private void addMarkerToMapPlaceRealEstate(PlaceRealEstate placeRealEstate) {
        Log.d(TAG, "addMarkerToMapPlaceRealEstate: called!");

        MarkerOptions options;

        LatLng latLng = new LatLng(
                placeRealEstate.getLatitude(),
                placeRealEstate.getLongitude());

        options = new MarkerOptions()
                .position(latLng)
                .title(placeRealEstate.getName())
                .snippet(Utils.capitalize(Utils.replaceUnderscore(placeRealEstate.getTypesList().get(0))))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));


        /* We fill the listOfMarkers and the map with the markers
         * */
        getListOfMarkers().add(mMap.addMarker(options));
    }

    /**
     * Map Listeners
     */
    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Log.d(TAG, "onInfoWindowClick: called!");
            if (getRootMainActivity() != null) {
                ToastHelper.toastNotImplemented(getRootMainActivity());
            }
        }
    };

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickedListener = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            Log.d(TAG, "onMyLocationButtonClick: called!");
            getDeviceLocation();
            return true;
        }
    };

    private GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.showInfoWindow();
            return false;
        }
    };
}