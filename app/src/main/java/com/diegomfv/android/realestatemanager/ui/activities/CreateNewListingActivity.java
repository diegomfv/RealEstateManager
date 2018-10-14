package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaHorizontalCreate;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppExecutors;
import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.diegomfv.android.realestatemanager.data.datamodels.RoomsRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.network.models.placebynearby.LatLngForRetrofit;
import com.diegomfv.android.realestatemanager.network.models.placebynearby.PlacesByNearby;
import com.diegomfv.android.realestatemanager.network.models.placedetails.PlaceDetails;
import com.diegomfv.android.realestatemanager.network.models.placefindplacefromtext.PlaceFromText;
import com.diegomfv.android.realestatemanager.network.remote.GoogleServiceStreams;
import com.diegomfv.android.realestatemanager.receivers.InternetConnectionReceiver;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.dialogfragments.InsertAddressDialogFragment;
import com.diegomfv.android.realestatemanager.util.FirebasePushIdGenerator;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */

/**
 * This activity allows the user to create new listings and add them to the database. Once a listing
 * has been created it cannot be deleted. Photos cannot be deleted once the have been chosen. When
 * the address is inputted, the system automatically checks if it is valid (if it can be found using
 * Google Places services). If it is valid, the listing can be saved in the database (if not, it cannot
 * be saved). To save the listing internet connection is required (since the last part of the process
 * requires internet).
 */
public class CreateNewListingActivity extends BaseActivity implements Observer, InsertAddressDialogFragment.InsertAddressDialogListener {

    private static final String TAG = CreateNewListingActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.collapsing_toolbar_id)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.progress_bar_content_id)
    LinearLayout progressBarContent;

    @BindView(R.id.main_layout_id)
    ScrollView mainLayout;

    @BindView(R.id.toolbar_id)
    Toolbar toolbar;

    @BindView(R.id.card_view_type_id)
    CardView cardViewType;

    @BindView(R.id.card_view_price_id)
    CardView cardViewPrice;

    @BindView(R.id.card_view_surface_area_id)
    CardView cardViewSurfaceArea;

    @BindView(R.id.card_view_number_bedrooms_id)
    CardView cardViewNumberOfBedrooms;

    @BindView(R.id.card_view_number_bathrooms_id)
    CardView cardViewNumberOfBathrooms;

    @BindView(R.id.card_view_number_rooms_other_id)
    CardView cardViewNumberOfOtherRooms;

    @BindView(R.id.card_view_description_id)
    CardView cardViewDescription;

    @BindView(R.id.card_view_address_id)
    CardView cardViewAddress;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private TextInputAutoCompleteTextView tvTypeOfBuilding;

    private TextInputEditText tvPrice;

    private TextInputEditText tvSurfaceArea;

    private TextInputEditText tvNumberOfBedrooms;

    private TextInputEditText tvNumberOfBathrooms;

    private TextInputEditText tvNumberOfOtherRooms;

    private TextInputEditText tvDescription;

    private TextInputEditText tvAddress;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.button_card_view_with_button_id)
    Button buttonAddAddress;

    @BindView(R.id.card_view_recyclerView_media_id)
    RecyclerView recyclerView;

    @BindView(R.id.button_add_edit_photo_id)
    Button buttonAddPhoto;

    @BindView(R.id.button_insert_edit_listing_id)
    Button buttonInsertListing;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int currency;

    //RecyclerView Adapter
    private RVAdapterMediaHorizontalCreate adapter;

    private Unbinder unbinder;

    //InternetConnectionReceiver variables
    private InternetConnectionReceiver receiver;
    private IntentFilter intentFilter;
    private Snackbar snackbar;
    private boolean isInternetAvailable;

    /////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.currency = Utils.readCurrentCurrencyShPref(this);

        this.isInternetAvailable = false;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_create_new_listing);
        this.unbinder = ButterKnife.bind(this);

        /* Checks if the users comes from AddPhoto Activity.
        If that is the case, the cache is not deleted*/
        this.checkIntent();

        /* Configuring the layout*/
        this.configureLayout();

        /* Showing the info and hiding the progress bar
         * */
        Utils.showMainContent(progressBarContent, mainLayout);

        /* Updating the views according to cache information
         * */
        this.updateViews();

        /* Configuring the RecyclerView
         * */
        this.configureRecyclerView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");
        this.connectBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.disconnectBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        this.disconnectBroadcastReceiver();
        unbinder.unbind();
    }

    /**
     * If back pressed is clicked, the system launches a dialog asking the user if he/she
     * really wants to leave.
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called!");
        launchAreYouSureDialogIfNecessary();
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
                updatePriceHint();

            }
            break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This callback gets triggered when internet connection changes. If there is no internet,
     * a snackbar appears notifying the user.
     */
    @Override
    public void update(Observable o, Object internetAvailable) {
        Log.d(TAG, "update: called!");
        isInternetAvailable = Utils.setInternetAvailability(internetAvailable);
        snackBarConfiguration();
    }

    /**
     * This callback gets triggered when the user inputs information in the dialogFragment and
     * presses the positive button.
     */
    @Override
    public void onDialogPositiveClick(AddressRealEstate addressRealEstate) {
        Log.d(TAG, "onDatePickerDialogPositiveClick: called!");
        checkAddressIsValid(addressRealEstate);
    }

    /**
     * This callback gets triggered when the user presses the negative button in the dialogFragment.
     */
    @Override
    public void onDialogNegativeClick() {
        Log.d(TAG, "onDatePickerDialogNegativeClick: called!");
        ToastHelper.toastShort(this, "The address was not added");
    }

    @OnClick({R.id.button_card_view_with_button_id, R.id.button_add_edit_photo_id, R.id.button_insert_edit_listing_id})
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: " + ((Button) view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_card_view_with_button_id: {
                launchInsertAddressDialog();
            }
            break;

            case R.id.button_add_edit_photo_id: {
                launchPhotoGridActivity();
            }
            break;

            case R.id.button_insert_edit_listing_id: {
                if (allChecksCorrect()) {
                    insertListing();
                }
            }
            break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that checks if the activity was launched from PhotoGridActivity.
     * If it was not, the cache related to bitmap is deleted.
     */
    private void checkIntent() {
        Log.d(TAG, "checkIntent: called!");
        if (getIntent() != null
                && getIntent().getExtras() != null
                && getIntent().getExtras().getString(Constants.INTENT_FROM_PHOTO_GRID_ACTIVITY) != null
                && getIntent().getExtras().getString(Constants.INTENT_FROM_PHOTO_GRID_ACTIVITY).equals(Constants.STRING_FROM_PHOTO_GRID_ACTIVITY)) {
            /* If we come from PhotoGridActivity we do not delete the cache
             * */

        } else {
            /* If we come from another place, we delete the cache
             * */
            getRepository().deleteBitmapCache();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the toolbar.
     */
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

        /* Changing the font of the toolbar
         * */
        Typeface typeface = ResourcesCompat.getFont(this, R.font.arima_madurai);
        collapsingToolbar.setCollapsedTitleTypeface(typeface);
        collapsingToolbar.setExpandedTitleTypeface(typeface);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that modifies the currency variable and writes the new info to sharedPreferences.
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

    /**
     * Method to configure the layout.
     */
    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.configureToolBar();

        this.getAutocompleteTextView();
        this.getEditTexts();

        this.setAllHints();
        this.setTextButtons();
    }

    /**
     * Method to get a reference to the AutocompleteTextView
     */
    private void getAutocompleteTextView() {
        Log.d(TAG, "getAutocompleteTextView: called!");
        this.tvTypeOfBuilding = cardViewType.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
    }

    /**
     * Method to get references to the TextInputEditTexts.
     */
    private void getEditTexts() {
        Log.d(TAG, "getEditTexts: called!");
        this.tvPrice = cardViewPrice.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
        this.tvSurfaceArea = cardViewSurfaceArea.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
        this.tvNumberOfBedrooms = cardViewNumberOfBedrooms.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
        this.tvNumberOfBathrooms = cardViewNumberOfBathrooms.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
        this.tvNumberOfOtherRooms = cardViewNumberOfOtherRooms.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
        this.tvDescription = cardViewDescription.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
        this.tvAddress = cardViewAddress.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
    }

    /**
     * Method to set the hints of all the Views.
     */
    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");
        // TODO: 23/08/2018 Use Resources instead of hardcoded strings
        setHint(cardViewType, "Type");
        setHint(cardViewPrice, "Price (" + Utils.getCurrencySymbol(currency) + ")");
        setHint(cardViewSurfaceArea, "Surface Area (sqm)");
        setHint(cardViewNumberOfBedrooms, "Bedrooms");
        setHint(cardViewNumberOfBathrooms, "Bathrooms");
        setHint(cardViewNumberOfOtherRooms, "Other Rooms");
        setHint(cardViewDescription, "Description");
        setHint(cardViewAddress, "Address");
    }

    /**
     * Method that sets the hint in a TextInputLayout.
     */
    private void setHint(CardView cardView, String hint) {
        Log.d(TAG, "setHint: called!");
        TextInputLayout textInputLayout = cardView.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint(hint);
    }

    /**
     * Method to update the price hint.
     */
    private void updatePriceHint() {
        Log.d(TAG, "updatePriceHint: called!");
        setHint(cardViewPrice, "Price (" + Utils.getCurrencySymbol(currency) + ")");
    }

    /**
     * Method to set the text of the buttons.
     */
    private void setTextButtons() {
        Log.d(TAG, "setTextButtons: called!");
        buttonInsertListing.setText("Insert Listing");
        buttonAddAddress.setText("Add Address");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to update the views according to the real estate cache info.
     */
    private void updateViews() {
        Log.d(TAG, "updateViews: called!");
        this.tvTypeOfBuilding.setText(getRealEstateCache().getType());
        this.tvPrice.setText(String.valueOf(getRealEstateCache().getPrice()));
        this.tvSurfaceArea.setText(String.valueOf(getRealEstateCache().getSurfaceArea()));
        this.tvNumberOfBedrooms.setText(String.valueOf(getRealEstateCache().getRooms().getBedrooms()));
        this.tvNumberOfBathrooms.setText(String.valueOf(getRealEstateCache().getRooms().getBathrooms()));
        this.tvNumberOfOtherRooms.setText(String.valueOf(getRealEstateCache().getRooms().getOtherRooms()));
        this.tvDescription.setText(getRealEstateCache().getDescription());
        this.tvAddress.setText(Utils.getAddressAsString(getRealEstateCache()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //CACHE UPDATE

    /**
     * Method that generates an id using the FirebasePushIdGenerator and sets this id
     * to the real estare cache object.
     */
    private void updateRealEstateCacheId() {
        Log.d(TAG, "updateRealEstateCacheId: called!");
        this.getRealEstateCache().setId(FirebasePushIdGenerator.generate());
    }

    /**
     * Method to update the real estate cache information.
     */
    private void updateRealEstateCache() {
        Log.d(TAG, "updateRealEstateCache: called!");
        this.updateStringValues();
        this.updateFloatValues();
        this.updateIntegerValues();
        this.updateBooleanValues();
    }

    /**
     * Method that updates the real estate cache information (strings).
     */
    private void updateStringValues() {
        Log.d(TAG, "updateStringValues: called!");
        this.getRealEstateCache().setType(Utils.capitalize(tvTypeOfBuilding.getText().toString().trim()));
        this.getRealEstateCache().setDescription(Utils.capitalize(tvDescription.getText().toString().trim()));
        this.getRealEstateCache().setAgent(Utils.readCurrentAgentData(this)[0] + " " + Utils.readCurrentAgentData(this)[1]);
    }

    /**
     * Method that updates the real estate cache information (floats).
     */
    private void updateFloatValues() {
        Log.d(TAG, "updateFloatValues: called!");
        this.getRealEstateCache().setPrice(Utils.getFloatFromTextView(tvPrice));
        this.getRealEstateCache().setSurfaceArea(Utils.getFloatFromTextView(tvSurfaceArea));
    }

    /**
     * Method that updates the real estate cache information (integers).
     */
    private void updateIntegerValues() {
        Log.d(TAG, "updateIntegerValues: called!");
        this.setRooms();
    }

    /**
     * Method that updates the real estate cache information (rooms information).
     */
    private void setRooms() {
        Log.d(TAG, "setRooms: called!");
        getRealEstateCache().setRooms(new RoomsRealEstate(
                Utils.getIntegerFromTextView(tvNumberOfBedrooms),
                Utils.getIntegerFromTextView(tvNumberOfBathrooms),
                Utils.getIntegerFromTextView(tvNumberOfOtherRooms)));
    }

    /**
     * Method that updates the real estate cache information (booleans).
     */
    private void updateBooleanValues() {
        Log.d(TAG, "updateBooleanValues: called!");
        this.getRealEstateCache().setFound(false);
    }

    /**
     * Method that updates the real estate cache information (list of images).
     */
    private void updateImagesIdRealEstateCache() {
        Log.d(TAG, "updateImagesIdRealEstateCache: called!");

        List<String> listOfImagesIds = new ArrayList<>();

        for (int i = 0; i < getListOfImagesRealEstateCache().size(); i++) {
            listOfImagesIds.add(getListOfImagesRealEstateCache().get(i).getId());
        }
        this.getRealEstateCache().setListOfImagesIds(listOfImagesIds);
    }

    /**
     * Method that updates the real estate cache information (when the listing has been put in the list).
     */
    private void updateDatePutRealEstateCacheCache() {
        Log.d(TAG, "updateDatePutRealEstateCacheCache: called!");
        this.getRealEstateCache().setDatePut(Utils.getTodayDate());
    }

    /**
     * Method that updates the real estate cache price before inserting it in the database.
     * It transforms euros to dollars if the currency is euros (because the prices in the database
     * are in dollars)
     */
    private void updateRealEstateCachePrice(float price) {
        Log.d(TAG, "updateRealEstateCachePrice: called!");
        if (currency == 0) {
            getRealEstateCache().setPrice(price);

        } else {
            getRealEstateCache().setPrice(Utils.convertEuroToDollar(price));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //NETWORK

    /**
     * Method that checks if an address is valid.
     * If it is not, it displays a toast notifying the user.
     */
    private void checkAddressIsValid(AddressRealEstate addressRealEstate) {
        Log.d(TAG, "checkIfAddressIsValid: called!");

        if (isInternetAvailable) {
            getPlaceFromText(addressRealEstate);

        } else {
            ToastHelper.toastShort(this, "Internet is not available, AddressRealEstate cannot be saved");

        }
    }

    /**
     * Method that updates the information related to address of the real estate cache
     */
    private void updateRealEstateCacheWithAddress(AddressRealEstate addressRealEstate) {
        Log.d(TAG, "updateRealEstateCacheWithAddress: called!");
        getRealEstateCache().getAddress().setStreet(addressRealEstate.getStreet());
        getRealEstateCache().getAddress().setLocality(addressRealEstate.getLocality());
        getRealEstateCache().getAddress().setCity(addressRealEstate.getCity());
        getRealEstateCache().getAddress().setPostcode(addressRealEstate.getPostcode());
    }

    /**
     * Method that uses RxJava to check if a place is valid (if it is returned as a valid place).
     * If it is valid, we get details from the place (latitude, longitude) and nearby places.
     */
    @SuppressLint("CheckResult")
    private void getPlaceFromText(final AddressRealEstate addressRealEstate) {
        Log.d(TAG, "getPlaceFromText: called!");

        GoogleServiceStreams.streamFetchPlaceFromText(
                addressRealEstate.getStreet() + ","
                        + addressRealEstate.getLocality() + ","
                        + addressRealEstate.getCity() + ","
                        + addressRealEstate.getPostcode(),
                "textquery",
                getResources().getString(R.string.a_k_p))
                .subscribeWith(new DisposableObserver<PlaceFromText>() {
                    @Override
                    public void onNext(PlaceFromText placeFromText) {
                        Log.d(TAG, "onNext: called!");

                        if (Utils.checksPlaceFromText(placeFromText)) {

                            /* Notifying the user
                             * */
                            ToastHelper.toastShort(CreateNewListingActivity.this,
                                    "The address is valid");

                            /* Fill the address of the cache
                             * */
                            updateRealEstateCacheWithAddress(addressRealEstate);

                            /* Set the address in the textView
                             * */
                            tvAddress.setText(Utils.getAddressAsString(getRealEstateCache()));

                            /* Get place details
                             * */
                            getPlaceDetails(placeFromText.getCandidates().get(0).getPlaceId());

                        } else {
                            ToastHelper.toastLong(CreateNewListingActivity.this, "Address not valid");
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        ToastHelper.toastShort(CreateNewListingActivity.this,
                                "The address is not valid");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");
                    }
                });
    }

    /**
     * Method that uses RxJava to get details about a certain place. If it retrieves the information
     * we start getting the nearby places.
     */
    @SuppressLint("CheckResult")
    private void getPlaceDetails(String placeId) {
        Log.d(TAG, "getPlaceDetails: called!");

        GoogleServiceStreams.streamFetchPlaceDetails(
                placeId,
                getResources().getString(R.string.a_k_p))
                .subscribeWith(new DisposableObserver<PlaceDetails>() {
                    @Override
                    public void onNext(PlaceDetails placeDetails) {
                        Log.d(TAG, "onNext: called!");

                        if (Utils.checksPlaceDetails(placeDetails)) {

                            /* We set the latitude and longitude of the address
                             * */
                            getRealEstateCache().setLatitude(placeDetails.getResult().getGeometry().getLocation().getLat());
                            getRealEstateCache().setLongitude(placeDetails.getResult().getGeometry().getLocation().getLng());

                            /* We use the latitude and longitude to fetch nearby places
                             * */
                            getNearbyPlaces(
                                    placeDetails.getResult().getGeometry().getLocation().getLat(),
                                    placeDetails.getResult().getGeometry().getLocation().getLng());


                        } else {
                            ToastHelper.toastShort(CreateNewListingActivity.this,
                                    "There was a problem with the latitude and longitude");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        ToastHelper.toastShort(CreateNewListingActivity.this,
                                "There was a problem with the latitude and longitude");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");

                    }
                });
    }

    /**
     * Method that uses RxJava to get the nearby places related to a place.
     */
    @SuppressLint("CheckResult")
    private void getNearbyPlaces(double latitude, double longitude) {
        Log.d(TAG, "getNearbyPlaces: called!");

        /* Clear the cache
         * */
        getListOfPlacesRealEstateCache().clear();

        /* As an additional feature, we could constraint the search with types.
         * We could also show the places in the map in different colour TODO
         * */
        GoogleServiceStreams.streamFetchPlacesNearby(
                new LatLngForRetrofit(latitude, longitude),
                Constants.FETCH_NEARBY_RANKBY,
                getResources().getString(R.string.a_k_p))
                .subscribeWith(new DisposableObserver<PlacesByNearby>() {
                    @Override
                    public void onNext(PlacesByNearby placesByNearby) {
                        Log.d(TAG, "onNext: called!");

                        if (Utils.checkPlacesByNearbyResults(placesByNearby)) {

                            PlaceRealEstate placeRealEstate;
                            List<String> listPlaceRealEstateIds = new ArrayList<>();

                            for (int i = 0; i < placesByNearby.getResults().size(); i++) {

                                if (Utils.checkResultPlacesByNearby(placesByNearby.getResults().get(i))) {

                                    placeRealEstate = new PlaceRealEstate(
                                            FirebasePushIdGenerator.generate(),
                                            placesByNearby.getResults().get(i).getPlaceId(),
                                            placesByNearby.getResults().get(i).getName(),
                                            placesByNearby.getResults().get(i).getVicinity(),
                                            placesByNearby.getResults().get(i).getTypes(),
                                            placesByNearby.getResults().get(i).getGeometry().getLocation().getLat(),
                                            placesByNearby.getResults().get(i).getGeometry().getLocation().getLng());

                                    /* If the result passes all checks, we add the place to the list
                                     * */
                                    getListOfPlacesRealEstateCache().add(placeRealEstate);
                                    listPlaceRealEstateIds.add(placeRealEstate.getId());
                                }
                            }

                            /* We add the list to the cache
                             * */
                            getRealEstateCache().setListOfNearbyPointsOfInterestIds(listPlaceRealEstateIds);

                        } else {
                            ToastHelper.toastShort(CreateNewListingActivity.this,
                                    "There was a problem fetching nearby places");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        ToastHelper.toastShort(CreateNewListingActivity.this,
                                "There was a problem fetching nearby places");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");

                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //INTERNET CONNECTION RECEIVER

    /**
     * Method that connects a broadcastReceiver to the activity.
     * It allows to notify the user about the internet state.
     */
    private void connectBroadcastReceiver() {
        Log.d(TAG, "connectBroadcastReceiver: called!");

        receiver = new InternetConnectionReceiver();
        intentFilter = new IntentFilter(Constants.CONNECTIVITY_CHANGE_STATUS);
        Utils.connectReceiver(this, receiver, intentFilter, this);
    }

    /**
     * Method that disconnects the broadcastReceiver from the activity.
     */
    private void disconnectBroadcastReceiver() {
        Log.d(TAG, "disconnectBroadcastReceiver: called!");

        if (receiver != null) {
            Utils.disconnectReceiver(
                    this,
                    receiver,
                    this);
        }
        receiver = null;
        intentFilter = null;
        snackbar = null;
    }

    /**
     * Method to configure the snackBar. If there is internet it won't be displayed. If there
     * isn't it will be shown.
     */
    private void snackBarConfiguration() {
        Log.d(TAG, "snackBarConfiguration: called!");

        if (isInternetAvailable) {
            if (snackbar != null) {
                snackbar.dismiss();
            }

        } else {
            if (snackbar == null) {
                snackbar = Utils.createSnackbar(
                        this,
                        mainLayout,
                        getResources().getString(R.string.noInternet));

            } else {
                snackbar.show();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the RecyclerView.
     */
    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");

        Log.w(TAG, "configureRecyclerView: recyclerView = " + recyclerView);
        Log.w(TAG, "configureRecyclerView: adapter = " + adapter);
        Log.w(TAG, "configureRecyclerView: getListOfBitmapKeys() = " + getListOfBitmapKeys());
        Log.w(TAG, "configureRecyclerView: getBitmapCache() = " + getBitmapCache());
        Log.w(TAG, "configureRecyclerView: getImagesDir() = " + getImagesDir());
        Log.w(TAG, "configureRecyclerView: getGlide() = " + getGlide());

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        this.adapter = new RVAdapterMediaHorizontalCreate(
                this,
                getListOfBitmapKeys(),
                getBitmapCache(),
                getImagesDir(),
                getGlide());

        this.recyclerView.setAdapter(this.adapter);

        this.configureOnClickRecyclerView();

    }

    /**
     * Method to configure the onClick listeners of the RecyclerView.
     */
    private void configureOnClickRecyclerView() {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");
                        Log.w(TAG, "onItemClicked: " + getListOfImagesRealEstateCache().get(position).getDescription());

                        if (getListOfImagesRealEstateCache().get(position).getDescription().isEmpty()) {
                            ToastHelper.toastShort(CreateNewListingActivity.this, "No description available");

                        } else {
                            ToastHelper.toastShort(CreateNewListingActivity.this, getListOfImagesRealEstateCache().get(position).getDescription());
                        }
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that checks if the address information is correct or not.
     * It is used to check if we can continue inserting the listing into the database.
     */
    private boolean allChecksCorrect() {
        Log.d(TAG, "allChecksCorrect: called!");

        if (!Utils.textViewIsFilled(tvAddress)) {
            ToastHelper.toastLong(this, "Please, insert a valid address");
            return false;

        } else if (getRealEstateCache().getLatitude() == 0d
                || getRealEstateCache().getLongitude() == 0d) {
            ToastHelper.toastLong(this,
                    "There is a problem with the Nearby Places. Please, insert another address");
            return false;

        } else if (getListOfImagesRealEstateCache().size() == 0) {
            ToastHelper.toastLong(this,
                    "Please, insert at least one image");
            return false;

        } else {
            return true;
        }
    }

    /**
     * Method that starts the insertion process of the listing into the database.
     */
    private void insertListing() {
        Log.d(TAG, "insertListing: called!");

        Utils.hideMainContent(progressBarContent, mainLayout);

        /* Start insertion process
         * */
        insertRealEstateObject();

    }

    /**
     * Method that updates the real estate information and then inserts the object in the database.
     */
    @SuppressLint("CheckResult")
    private void insertRealEstateObject() {
        Log.d(TAG, "insertRealEstateObject: called!");

        /* We update the id of the real estate
         * */
        updateRealEstateCacheId();

        /* We update the real estate cache according to the information inputted in the views
         * */
        updateRealEstateCache();

        /* We update the price of the real estate cache if it is necessary (to insert it in dollars)
         * */
        updateRealEstateCachePrice(Utils.getFloatFromTextView(tvPrice));

        /* We update the images the real estate is related to
         * */
        updateImagesIdRealEstateCache();

        /* We update the date we put the real estate in the database
         * */
        updateDatePutRealEstateCacheCache();

        /* We use RxJava to insert the real estate object
         * */
        getRepository().insertRealEstate(getRealEstateCache())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");
                        insertListImageRealEstate();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    /**
     * Method to insert the list of images of the real estate object in the database.
     * The real estate object will keep a reference to these images in the list of images it
     * has as a field.
     */
    @SuppressLint("CheckResult")
    private void insertListImageRealEstate() {
        Log.d(TAG, "insertListImageRealEstate: called!");

        /* We use RxJava to proceed with the insertion
         * */
        getRepository().insertListImagesRealEstate(getListOfImagesRealEstateCache())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");
                        insertListPlacesRealEstate();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    /**
     * Method to insert the list of nearby places of the real estate object in the database.
     * The real estate object will keep a reference to these places in the list ofnaerby places it
     * has as a field.
     */
    @SuppressLint("CheckResult")
    public void insertListPlacesRealEstate() {
        Log.d(TAG, "insertListPlacesRealEstate: called");

        /* We use RxJava to proceed with the insertion
         * */
        getRepository().insertListPlacesRealEstate(getListOfPlacesRealEstateCache())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");

                        /* We also insert the images of the real estate cache object
                         * in the Images Directory
                         */
                        insertAllBitmapsInImagesDirectory();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    /**
     * This method inserts the images into the images directory.
     */
    public void insertAllBitmapsInImagesDirectory() {
        Log.d(TAG, "insertAllBitmapsInImagesDirectory: called!");

        /* This is already done in a worker thread.
         * We fill the cache and the internal storage
         */
        for (Map.Entry<String, Bitmap> entry : getBitmapCache().entrySet()) {
            getRepository().addBitmapToBitmapCacheAndStorage(getInternalStorage(), getImagesDir(), entry.getKey(), entry.getValue());
        }

        /* We create a notification and launch MainActivity
         * */
        Utils.launchActivity(this, MainActivity.class);
        createNotification();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to launch PhotoGridActivity.
     * It uses an intent with Extras.
     */
    private void launchPhotoGridActivity() {
        Log.d(TAG, "launchPhotoGridActivity: called!");

        updateRealEstateCache();

        Intent intent = new Intent(this, PhotoGridActivity.class);
        intent.putExtra(Constants.INTENT_FROM_ACTIVITY, Constants.INTENT_FROM_CREATE);
        startActivity(intent);

    }

    /**
     * Method to launch InsertAddressDialog.
     */
    private void launchInsertAddressDialog() {
        Log.d(TAG, "launchInsertAddressDialog: called!");
        InsertAddressDialogFragment.newInstance(getRealEstateCache().getAddress())
                .show(getSupportFragmentManager(), "InsertAddressDialogFragment");
    }

    /**
     * Method to check if information was inputted.
     */
    private boolean informationWasInputted() {
        Log.d(TAG, "informationWasInputted: called!");

        /* Utils.textViewIsFilled returns true if the textView IS NOT EMPTY
         * */
        if (Utils.textViewIsFilled(tvTypeOfBuilding)
                || Utils.textViewIsFilled(tvDescription)
                || Utils.textViewIsFilled(tvAddress)
                || Utils.getFloatFromTextView(tvPrice) != 0.0f
                || Utils.getFloatFromTextView(tvSurfaceArea) != 0.0f) {
            return true;
        }
        return false;
    }

    /**
     * Method to launch a dialog asking the user if he/she is sure to leave the activity.
     * The information won't be saved.
     */
    private void launchAreYouSureDialog() {
        Log.d(TAG, "launchAreYouSureDialog: called!");
        Utils.launchSimpleDialog(
                this,
                "The changes will not be saved",
                "Are you sure you want to proceed?",
                "Yes, I am sure",
                "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: called!");
                        Utils.launchActivity(CreateNewListingActivity.this, MainActivity.class);
                    }
                }
        );
    }

    /**
     * Method that checks if it is necessary to launch a dialog asking the user if he/she is
     * sure to leave. It depends on if there is information inputted.
     */
    private void launchAreYouSureDialogIfNecessary() {
        Log.d(TAG, "launchAreYouSureDialogIfNecessary: called!");
        if (informationWasInputted()) {
            launchAreYouSureDialog();

        } else {
            Utils.launchActivity(CreateNewListingActivity.this, MainActivity.class);
        }
    }

    /**
     * Method that creates a notification.
     */
    private void createNotification() {
        Log.d(TAG, "createNotification: called!");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID,
                    getString(R.string.notif_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        //The request code must be the same as the same we pass to .notify later
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.real_estate_logo)
                        .setContentTitle(getResources().getString(R.string.notification_title_create))
                        .setContentText(getResources().getString(R.string.notification_text, Utils.getAddressAsString(getRealEstateCache())))
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setAutoCancel(true);
        //SetAutoCancel(true) makes the notification dismissible when the user swipes it away

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (notificationManager != null) {
            notificationManager.notify(100, notificationBuilder.build());
        }
    }
}
