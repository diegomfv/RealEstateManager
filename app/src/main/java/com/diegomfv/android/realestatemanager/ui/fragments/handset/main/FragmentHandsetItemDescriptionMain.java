package com.diegomfv.android.realestatemanager.ui.fragments.handset.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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
import com.diegomfv.android.realestatemanager.viewmodel.ItemDescriptionViewModel;
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

/**
 * Created by Diego Fajardo on 16/08/2018.
 */

// TODO: 23/08/2018 Retain the fragment!
public class FragmentHandsetItemDescriptionMain extends BaseFragment {

    private static final String TAG = FragmentHandsetItemDescriptionMain.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerViewMedia;

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

    private int currency;

    private List<Bitmap> listOfBitmaps;

    private int imagesCounter;

    //RecyclerView Adapter
    private RVAdapterMediaHorizontalDescr adapter;

    //Glide
    private RequestManager glide;

    private RealEstate realEstate;

    private List<ImageRealEstate> listOfImagesRealEstate;

    //ViewModel
    private ItemDescriptionViewModel itemDescriptionViewModel;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private FusedLocationProviderClient mFusedLocationProviderClient; //To get the location of the current user

    private GoogleMap mMap;

    private double myLatitude;
    private double myLongitude;

    private List<PlaceRealEstate> listOfPlacesRealEstate;

    private List<Marker> listOfMarkers;

    private boolean deviceLocationPermissionGranted;
    private RealEstate rooms;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static FragmentHandsetItemDescriptionMain newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentHandsetItemDescriptionMain();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called!");

        this.deviceLocationPermissionGranted = false;

        this.myLatitude = 0d;
        this.myLongitude = 0d;

        if (getActivity() != null) {
            this.currency = Utils.readCurrentCurrencyShPref(getActivity());
        }

        this.imagesCounter = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////
        View view = inflater.inflate(R.layout.fragment_item_description, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        /* Glide configuration*/
        if (getActivity() != null) {
            this.glide = Glide.with(getActivity());
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            realEstate = bundle.getParcelable(Constants.GET_PARCELABLE);
            Log.i(TAG, "onCreateView: bundle = " + bundle);
        }

        this.configureRecyclerView();

        if (realEstate != null) {
            fillLayoutWithRealEstateInfo(realEstate);
        }

        Log.i(TAG, "onCreateView: tvSurfaceArea = " + tvSurfaceArea);

        this.checkDeviceLocationPermissionGranted();

        this.createViewModel();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        this.unbinder.unbind();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void fillLayoutWithRealEstateInfo(RealEstate realEstate) {
        Log.d(TAG, "fillLayoutWithRealEstateInfo: called!");
        setDescription(realEstate);
        setSurfaceArea(realEstate);
        setRooms(realEstate);
        setAddress(realEstate);
        setPrice(realEstate);
        setSoldState(realEstate);
    }

    private void setDescription(RealEstate realEstate) {
        Log.d(TAG, "setDescription: called!");
        tvDescription.setText(realEstate.getDescription());
    }

    private void setSurfaceArea(RealEstate realEstate) {
        Log.d(TAG, "setSurfaceArea: called!");
        tvSurfaceArea.setText(String.valueOf(realEstate.getSurfaceArea()));
    }

    public void setRooms(RealEstate rooms) {
        tvNumberBedrooms.setText(String.valueOf("Bedrooms -- " + realEstate.getRooms().getBedrooms()));
        tvNumberBathrooms.setText(String.valueOf("Bathrooms -- " + realEstate.getRooms().getBathrooms()));
        tvNumberOtherRooms.setText(String.valueOf("Other Rooms -- " + realEstate.getRooms().getOtherRooms()));
    }

    private void setAddress(RealEstate realEstate) {
        Log.d(TAG, "setLocation: called!");
        tvStreet.setText(realEstate.getAddress().getStreet());
        tvLocality.setText(realEstate.getAddress().getLocality());
        tvCity.setText(realEstate.getAddress().getCity());
        tvPostCode.setText(realEstate.getAddress().getPostcode());
    }

    // TODO: 28/08/2018 Use Placeholders
    @SuppressLint("SetTextI18n")
    private void setPrice(RealEstate realEstate) {
        Log.d(TAG, "setPrice: called!");
        tvPrice.setText(
                Utils.getCurrencySymbol(currency)
                        + " "
                        + (Utils.formatToDecimals((int) Utils.getPriceAccordingToCurrency(
                        currency, realEstate.getPrice()), currency)));
    }

    private void setSoldState (RealEstate realEstate) {
        Log.d(TAG, "setSoldState: called!");
        if (realEstate.getDateSale() != null) {
            tvSold.setText("SOLD on " + realEstate.getDateSale());
            tvSold.setTextColor(getResources().getColor(android.R.color.white));
            tvSold.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            tvSold.setText("On Sale");
            tvSold.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvSold.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //VIEWMODEL

    private void createViewModel() {
        Log.d(TAG, "createViewModel: called!");

        ItemDescriptionViewModel.Factory factory = new ItemDescriptionViewModel.Factory(getApp());
        this.itemDescriptionViewModel = ViewModelProviders
                .of(this, factory)
                .get(ItemDescriptionViewModel.class);

        subscribeToModel(itemDescriptionViewModel);
    }

    private void subscribeToModel(ItemDescriptionViewModel itemDescriptionViewModel) {
        Log.d(TAG, "subscribeToModel: called!");

        if (itemDescriptionViewModel != null) {

            this.itemDescriptionViewModel.getObservablePlacesRealEstate().observe(this, new Observer<List<PlaceRealEstate>>() {
                @Override
                public void onChanged(@Nullable List<PlaceRealEstate> placeRealEstates) {
                    Log.d(TAG, "onChanged: called!");
                    listOfPlacesRealEstate = placeRealEstates;
                    updateMapWithPins();

                }
            });

            this.itemDescriptionViewModel.getObservableImagesRealEstate().observe(this, new Observer<List<ImageRealEstate>>() {
                @Override
                public void onChanged(@Nullable List<ImageRealEstate> imageRealEstates) {
                    Log.d(TAG, "onChanged: called!");
                    listOfImagesRealEstate = imageRealEstates;
                    Log.w(TAG, "onChanged: imageRealEstates.size(): " + imageRealEstates.size());
                }
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");
        this.recyclerViewMedia.setHasFixedSize(true);
        this.recyclerViewMedia.setLayoutManager(new LinearLayoutManager(
                getActivity(), LinearLayoutManager.HORIZONTAL, false));
        this.adapter = new RVAdapterMediaHorizontalDescr(
                getActivity(),
                getRepository(),
                getInternalStorage(),
                getImagesDir(),
                realEstate,
                glide,
                currency);
        this.recyclerViewMedia.setAdapter(this.adapter);

        this.configureOnClickRecyclerView();
    }

    private void configureOnClickRecyclerView() {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerViewMedia)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerViewMedia, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");
                        Log.w(TAG, "onItemClicked: key = " + adapter.getKey(position) );
                        String key = adapter.getKey(position);

                        for (int i = 0; i < listOfImagesRealEstate.size(); i++) {
                            if (listOfImagesRealEstate.get(i).getId().equals(key)){
                                ToastHelper.toastShort(getActivity(), listOfImagesRealEstate.get(i).getDescription());
                            }
                        }
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkDeviceLocationPermissionGranted() {
        Log.d(TAG, "checkInternalStoragePermissionGranted: called!");

        if (getActivity() != null) {
            if (Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    deviceLocationPermissionGranted = true;

                    if (isGooglePlayServicesOK()) {
                        initMap();
                    }
                }

            } else {
                Utils.requestPermission((AppCompatActivity) getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public List<PlaceRealEstate> getListOfPlacesRealEstate() {
        if (listOfPlacesRealEstate == null) {
            return listOfPlacesRealEstate = new ArrayList<>();
        }
        return listOfPlacesRealEstate;
    }

    public List<Marker> getListOfMarkers() {
        if (listOfMarkers == null) {
            return listOfMarkers = new ArrayList<>();
        }
        return listOfMarkers;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //MAP

    /**
     * Checks if the user has the correct
     * Google Play Services Version
     */
    public boolean isGooglePlayServicesOK() {
        Log.d(TAG, "isGooglePlayServicesOK: called!");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            //Everything is fine and the user can make map requests
            Log.d(TAG, "isGooglePlayServicesOK: Google Play Services is working");
            return true;

        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //There is an error but we can resolve it
            Log.d(TAG, "isGooglePlayServicesOK: an error occurred but we can fix it");
            if (getActivity() != null) {
                Dialog dialog = GoogleApiAvailability.getInstance()
                        .getErrorDialog(getActivity(), available, Constants.REQUEST_ERROR_DIALOG);
                dialog.show();
            }

        } else {
            Log.d(TAG, "isGooglePlayServicesOK: an error occurred; you cannot make map requests");
            if (getActivity() != null) {
                ToastHelper.toastLong(getActivity(), this.getResources().getString(R.string.cant_make_map_requests));
            }
        }
        return false;
    }

    /**
     * Method used to initialise the map
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

                }
            }
        });

    }

    /**
     * Method used to get the user's location
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: called!");

        if (getActivity() != null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
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

                        myLatitude = currentLocation.getLatitude();
                        myLongitude = currentLocation.getLongitude();

                        mMap.setMyLocationEnabled(false); //displays the blue marker at your location
                        mMap.getUiSettings().setMyLocationButtonEnabled(false); //displays the button that allows you to center your position

                        moveCamera(
                                new LatLng(realEstate.getLatitude(), realEstate.getLongitude()),
                                Constants.MAPS_STATIC_DEFAULT_ZOOM);

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

            if (!getListOfPlacesRealEstate().isEmpty()) {
                Log.i(TAG, "displayPinsInMap: listOfPlaces is NOT EMPTY");

                /* We delete all the elements of the listOfMarkers and clear the map
                 * */
                getListOfMarkers().clear();
                mMap.clear();

                /* We add a pin with the real estate
                * */
                addMarkerToMapRealEstate(realEstate);

                /* We get all those places that are related to the real estate
                 * */
                List<String> listOfPlacesKeys = realEstate.getListOfNearbyPointsOfInterestIds();

                for (int i = 0; i < listOfPlacesKeys.size(); i++) {
                    for (int j = 0; j < getListOfPlacesRealEstate().size(); j++) {
                        if (listOfPlacesKeys.get(i).equals(getListOfPlacesRealEstate().get(j).getId())) {
                            addMarkerToMapPlaceRealEstate(getListOfPlacesRealEstate().get(j));
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

    private void addMarkerToMapRealEstate (RealEstate realEstate) {
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
        listOfMarkers.add(mMap.addMarker(options));

    }

    private void addMarkerToMapPlaceRealEstate(PlaceRealEstate placeRealEstate) {
        Log.d(TAG, "addMarkerToMapPlaceRealEstate: called!");

        MarkerOptions options;

        LatLng latLng = new LatLng(
                placeRealEstate.getLatitude(),
                placeRealEstate.getLongitude());

        options = new MarkerOptions()
                .position(latLng)
                .title(placeRealEstate.getName())
                .snippet(placeRealEstate.getTypesList().get(0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));


        /* We fill the listOfMarkers and the map with the markers
         * */
        listOfMarkers.add(mMap.addMarker(options));
    }

    //MAP LISTENERS

    private GoogleMap.OnInfoWindowClickListener onInfoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Log.d(TAG, "onInfoWindowClick: called!");
            if (getActivity() != null) {
                ToastHelper.toastNotImplemented(getActivity());
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

}
