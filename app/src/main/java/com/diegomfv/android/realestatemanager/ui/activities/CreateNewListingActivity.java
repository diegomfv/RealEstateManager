package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaHorizontalCreate;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.FakeDataGenerator;
import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.diegomfv.android.realestatemanager.data.datamodels.RoomsRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
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
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */

// TODO: 02/09/2018 Don't check if the external storage is available, it always should be here!
// TODO: 02/09/2018 Request permission in MainActivity and in AuthLoginActivity
// TODO: 02/09/2018 If permissions are not granted, don't let the user to continue
public class CreateNewListingActivity extends BaseActivity implements Observer, InsertAddressDialogFragment.InsertAddressDialogListener {

    private static final String TAG = CreateNewListingActivity.class.getSimpleName();

    /////////////////////////////////

    @BindView(R.id.progress_bar_content_id)
    LinearLayout progressBarContent;

    @BindView(R.id.main_layout_id)
    ScrollView mainLayout;

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

    private TextInputAutoCompleteTextView tvTypeOfBuilding;

    private TextInputAutoCompleteTextView tvPrice;

    private TextInputAutoCompleteTextView tvSurfaceArea;

    private TextView tvNumberOfBedrooms;
    private CrystalSeekbar seekBarBedrooms;

    private TextView tvNumberOfBathrooms;
    private CrystalSeekbar seekBarBathrooms;

    private TextView tvNumberOfOtherRooms;
    private CrystalSeekbar seekBarOtherRooms;

    private TextInputAutoCompleteTextView tvDescription;

    private TextInputEditText tvAddress;

    @BindView(R.id.button_add_edit_address_id)
    Button buttonAddAddress;

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerView;

    @BindView(R.id.button_add_edit_photo_id)
    Button buttonAddPhoto;

    @BindView(R.id.button_insert_edit_listing_id)
    Button buttonInsertListing;

    /////////////////////////////////

    private int currency;

    private ActionBar actionBar;

    //RecyclerView Adapter
    private RVAdapterMediaHorizontalCreate adapter;

    private int counter;

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

        this.counter = 0;
        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.insert_information_layout);
        setTitle("Create a New Listing");
        this.unbinder = ButterKnife.bind(this);

        this.checkIntent();

        this.configureActionBar();

        this.configureLayout();

        Utils.showMainContent(progressBarContent, mainLayout);

        this.updateViews();

        this.configureRecyclerView();

        // TODO: 26/08/2018 Delete
        generateFakeData();
    }

    // TODO: 26/08/2018 Delete!
    private void generateFakeData() {
        Log.d(TAG, "generateFakeData: called!");

        FakeDataGenerator fakeDataGenerator = new FakeDataGenerator();
        RealEstate realEstate = fakeDataGenerator.generateFakeData();
        tvTypeOfBuilding.setText(realEstate.getType());
        tvSurfaceArea.setText(String.valueOf(realEstate.getSurfaceArea()));
        tvPrice.setText(String.valueOf(realEstate.getPrice()));
        seekBarBedrooms.setMinValue(realEstate.getRooms().getBedrooms());
        seekBarBathrooms.setMinValue(realEstate.getRooms().getBathrooms());
        seekBarOtherRooms.setMinValue(realEstate.getRooms().getOtherRooms());
        tvDescription.setText(realEstate.getDescription());
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

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called!");
        // TODO: 19/08/2018 Might need to change this

        // TODO: 19/08/2018 Add a fragment saying, would you like to delete the media?
        // TODO: 19/08/2018 If yes, clean the list and the HashMap
        //do nothing
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

            case android.R.id.home: {
                Utils.launchActivity(this, MainActivity.class);

            }
            break;

            case R.id.menu_change_currency_button: {

                changeCurrency();
                Utils.updateCurrencyIcon(this, currency, item);
                updatePriceHint();

            }
            break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable o, Object internetAvailable) {
        Log.d(TAG, "update: called!");
        isInternetAvailable = Utils.setInternetAvailability(internetAvailable);
        snackBarConfiguration();
    }

    @OnClick({R.id.button_add_edit_address_id, R.id.button_add_edit_photo_id, R.id.button_insert_edit_listing_id})
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: " + ((Button) view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_add_edit_address_id: {
                launchInsertAddressDialog();

            }
            break;

            case R.id.button_add_edit_photo_id: {
                launchAddPhotoActivity();

            }
            break;

            case R.id.button_insert_edit_listing_id: {

                // TODO: 24/08/2018 Check that we have all the necessary information!
                // TODO: 24/08/2018 If there was no internet, we might not have all!
                // TODO: 24/08/2018 Use the broadcastreceiver to check the repository caches
                // TODO: 24/08/2018 If they are empty, cache information with request
                // TODO: 24/08/2018 Check also that types are correct
                // TODO: 24/08/2018 NOTIFY the user in ALLCHECKSARECORRECT()
                if (allChecksCorrect()) {
                    insertListing();
                }

            }
            break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkIntent () {
        Log.d(TAG, "checkIntent: called!");
        if (getIntent() != null
                && getIntent().getExtras() != null
                && getIntent().getExtras().getString(Constants.INTENT_FROM_ADD_PHOTO) != null
                && getIntent().getExtras().getString(Constants.INTENT_FROM_ADD_PHOTO).equals(Constants.STRING_FROM_ADD_PHOTO)) {
            /* If we come from ADD PHOTO we do not delete the cache
             * */

        } else {
            /* If we come from other place, we delete the cache
             * */
            getRepository().deleteBitmapCache();
            getRepository().deleteCacheAndSets();
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

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.getAutocompleteTextViews();
        this.getTextViews();
        this.getSeekBars();

        this.setCrystalSeekBarsMinMaxValues();
        this.setCrystalSeekBarsListeners();
        this.setAllHints();
        this.setTextLastButton();
    }

    private void getAutocompleteTextViews() {
        Log.d(TAG, "getAutocompleteTextViews: called!");
        this.tvTypeOfBuilding = cardViewType.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvPrice = cardViewPrice.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvSurfaceArea = cardViewSurfaceArea.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvDescription = cardViewDescription.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvAddress = cardViewAddress.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
    }

    private void getTextViews() {
        Log.d(TAG, "getTextViews: called!");
        this.tvNumberOfBedrooms = cardViewNumberOfBedrooms.findViewById(R.id.textView_title_id);
        this.tvNumberOfBathrooms = cardViewNumberOfBathrooms.findViewById(R.id.textView_title_id);
        this.tvNumberOfOtherRooms = cardViewNumberOfOtherRooms.findViewById(R.id.textView_title_id);
    }

    private void getSeekBars() {
        Log.d(TAG, "getSeekBars: called!");
        this.seekBarBedrooms = cardViewNumberOfBedrooms.findViewById(R.id.single_seek_bar_id);
        this.seekBarBathrooms = cardViewNumberOfBathrooms.findViewById(R.id.single_seek_bar_id);
        this.seekBarOtherRooms = cardViewNumberOfOtherRooms.findViewById(R.id.single_seek_bar_id);
    }

    private void setCrystalSeekBarsMinMaxValues() {
        Log.d(TAG, "setCrystalSeekBarsMinMaxValues: called!");
        setMinMaxValues(seekBarBedrooms);
        setMinMaxValues(seekBarBathrooms);
        setMinMaxValues(seekBarOtherRooms);
    }

    // TODO: 29/08/2018 Check!
    private void setMinMaxValues(CrystalSeekbar seekBar) {
        Log.d(TAG, "setMinMaxValues: called!");
        seekBar.setMinValue(0);
        seekBar.setMaxValue(9);
    }

    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");

        // TODO: 23/08/2018 Use Resources instead of hardcoded
        setAcTvHint(cardViewType, "Type");
        setAcTvHint(cardViewPrice, "Price (" + Utils.getCurrencySymbol(currency).substring(1) + ")");
        setAcTvHint(cardViewSurfaceArea, "Surface Area (sqm)");
        setAcTvHint(cardViewDescription, "Description");
        setAcTvHint(cardViewAddress, "Address");
    }

    private void setAcTvHint(CardView cardView, String hint) {
        Log.d(TAG, "setAcTvHint: called!");
        TextInputLayout textInputLayout = cardView.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint(hint);
    }

    private void updatePriceHint() {
        Log.d(TAG, "updatePriceHint: called!");
        TextInputLayout textInputLayout = cardViewPrice.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint("Price (" + Utils.getCurrencySymbol(currency).substring(1) + ")");
    }

    private void setTextLastButton() {
        Log.d(TAG, "setTextLastButton: called!");
        buttonInsertListing.setText("Insert Listing");
    }

    private void setCrystalSeekBarsListeners() {
        Log.d(TAG, "setCrystalSeekBarsListeners: called!");
        setListeners(seekBarBedrooms);
        setListeners(seekBarBathrooms);
        setListeners(seekBarOtherRooms);
    }

    private void setListeners(final CrystalSeekbar seekBar) {
        Log.d(TAG, "setListeners: called!");

        seekBar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                Log.d(TAG, "valueChanged: called! --> " + value);
                setTextDependingOnSeekBar(seekBar, value);
            }
        });

        seekBar.setOnSeekbarFinalValueListener(new OnSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number value) {
                Log.d(TAG, "finalValue: called! --> " + value);
                setTextDependingOnSeekBar(seekBar, value);
            }
        });
    }

    private void setTextDependingOnSeekBar(CrystalSeekbar seekBar, Number value) {
        Log.d(TAG, "setTextDependingOnTextView: called!");
        if (seekBar == seekBarBedrooms) {
            tvNumberOfBedrooms.setText("Bedrooms (" + value + ")");
        } else if (seekBar == seekBarBathrooms) {
            tvNumberOfBathrooms.setText("Bathrooms (" + value + ")");
        } else if (seekBar == seekBarOtherRooms) {
            tvNumberOfOtherRooms.setText("Other Rooms (" + value + ")");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //DIALOG FRAGMENT

    private void launchInsertAddressDialog() {
        Log.d(TAG, "launchInsertAddressDialog: called!");

        InsertAddressDialogFragment.newInstance(getRealEstateCache().getAddress())
                .show(getSupportFragmentManager(), "InsertAddressDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(AddressRealEstate addressRealEstate) {
        Log.d(TAG, "onDialogPositiveClick: called!");
        checkAddressIsValid(addressRealEstate);
    }

    @Override
    public void onDialogNegativeClick() {
        Log.d(TAG, "onDialogNegativeClick: called!");

        ToastHelper.toastShort(this, "The address was not added");
    }

    private boolean allChecksCorrect() {
        Log.d(TAG, "allChecksCorrect: called!");

        // TODO: 24/08/2018 DO THIS!

        return true;
    }

    private void insertListing() {
        Log.d(TAG, "insertListing: called!");

        if (Utils.getStringFromTextView(tvAddress).length() > 0
                && getRealEstateCache().getLatitude() != 0d
                && getRealEstateCache().getLongitude() != 0d) {

            Utils.hideMainContent(progressBarContent, mainLayout);

            /* Start insertion process
             * */
            insertRealEstateObject();

        } else {
            ToastHelper.toastLong(this, "Please, insert a valid address");

        }
    }

    @SuppressLint("CheckResult")
    private void insertRealEstateObject() {
        Log.d(TAG, "insertRealEstateObject: called!");

        updateRealEstateCacheId();
        updateRealEstateCache();
        updateImagesIdRealEstateCache();
        updateDatePutRealEstateCacheCache();

        Single.just(getAppDatabase().realStateDao().insertRealEstate(getRealEstateCache()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        Log.d(TAG, "onSuccess: called!");
                        insertListImageRealEstate();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void insertListImageRealEstate() {
        Log.d(TAG, "insertListImageRealEstate: called!");

        Single.just(getAppDatabase().imageRealEstateDao().insertListOfImagesRealEstate(getListOfImagesRealEstateCache()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<long[]>() {
                    @Override
                    public void onSuccess(long[] longs) {
                        Log.d(TAG, "onSuccess: called!");

                        insertListPlacesRealEstate();

                        // TODO: 02/09/2018 Check this!
                        insertAllBitmapsInImagesDirectory();
                        // TODO: 02/09/2018 Check this!

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());

                    }
                });
    }

    @SuppressLint("CheckResult")
    public void insertListPlacesRealEstate() {
        Log.d(TAG, "insertListPlacesRealEstate: called");

        // TODO: 02/09/2018 Could be done just with app executors
        Single.just(getAppDatabase().placeRealEstateDao().insertListOfPlaceRealEstate(getListOfPlacesRealEstateCache()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<long[]>() {
                    @Override
                    public void onSuccess(long[] longs) {
                        Log.d(TAG, "onSuccess: called!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    public void insertAllBitmapsInImagesDirectory () {
        Log.d(TAG, "insertAllBitmapsInImagesDirectory: called!");

        for (Map.Entry<String, Bitmap> entry : getBitmapCache().entrySet()) {
            getRepository().addBitmapToBitmapCacheAndStorage(getInternalStorage(), getImagesDir(), entry.getKey(), entry.getValue());
        }
        Utils.launchActivity(this, MainActivity.class);
        createNotification();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //CACHE

    private void updateViews() {
        Log.d(TAG, "updateViews: called!");
        this.tvTypeOfBuilding.setText(getRealEstateCache().getType());
        this.tvPrice.setText(String.valueOf(getRealEstateCache().getPrice()));
        this.tvSurfaceArea.setText(String.valueOf(getRealEstateCache().getSurfaceArea()));
        this.tvNumberOfBedrooms.setText("Bedrooms (" + getRealEstateCache().getRooms().getBedrooms() + ")");
        this.tvNumberOfBathrooms.setText("Bathrooms (" + getRealEstateCache().getRooms().getBathrooms() + ")");
        this.tvNumberOfOtherRooms.setText("Other Rooms (" + getRealEstateCache().getRooms().getOtherRooms() + ")");
        this.tvDescription.setText(getRealEstateCache().getDescription());
        this.tvAddress.setText(Utils.getAddressAsString(getRealEstateCache()));
    }

    private void updateRealEstateCache() {
        Log.d(TAG, "updateRealEstateCache: called!");
        this.updateStringValues();
        this.updateIntegerValues();
    }

    private void updateIntegerValues() {
        Log.d(TAG, "updateIntegerValues: called!");
        this.getRealEstateCache().setPrice((int) Utils.getPriceAccordingToCurrency(currency, Utils.getIntegerFromTextView(tvPrice)));
        this.getRealEstateCache().setSurfaceArea(Utils.getIntegerFromTextView(tvSurfaceArea));
        this.setRooms(this.getRealEstateCache());
    }

    private void setRooms(RealEstate realEstate) {
        Log.d(TAG, "setRooms: called!");
        realEstate.setRooms(new RoomsRealEstate(
                seekBarBedrooms.getSelectedMinValue().intValue(),
                seekBarBathrooms.getSelectedMinValue().intValue(),
                seekBarOtherRooms.getSelectedMinValue().intValue()));
    }

    private void updateStringValues() {
        Log.d(TAG, "updateStringValues: called!");
        this.getRealEstateCache().setType(Utils.capitalize(tvTypeOfBuilding.getText().toString().trim()));
        this.getRealEstateCache().setDescription(Utils.capitalize(tvDescription.getText().toString().trim()));
    }

    private void updateRealEstateCacheId() {
        Log.d(TAG, "updateRealEstateCacheId: called!");
        getRealEstateCache().setId(FirebasePushIdGenerator.generate());
    }

    private void updateImagesIdRealEstateCache() {
        Log.d(TAG, "updateImagesIdRealEstateCache: called!");

        List<String> listOfImagesIds = new ArrayList<>();

        for (int i = 0; i < getListOfImagesRealEstateCache().size(); i++) {
            listOfImagesIds.add(getListOfImagesRealEstateCache().get(i).getId());
        }
        getRealEstateCache().setListOfImagesIds(listOfImagesIds);
    }

    private void updateDatePutRealEstateCacheCache() {
        Log.d(TAG, "updateDatePutRealEstateCacheCache: called!");
        getRealEstateCache().setDatePut(Utils.getTodayDate());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //NETWORK

    private void checkAddressIsValid(AddressRealEstate addressRealEstate) {
        Log.d(TAG, "checkIfAddressIsValid: called!");

        if (isInternetAvailable) {
            getPlaceFromText(addressRealEstate);

        } else {
            ToastHelper.toastShort(this, "Internet is not available, AddressRealEstate cannot be saved");

        }
    }


    private void updateRealEstateCacheWithAddress(AddressRealEstate addressRealEstate) {
        Log.d(TAG, "updateRealEstateCacheWithAddress: called!");
        getRealEstateCache().getAddress().setStreet(addressRealEstate.getStreet());
        getRealEstateCache().getAddress().setLocality(addressRealEstate.getLocality());
        getRealEstateCache().getAddress().setCity(addressRealEstate.getCity());
        getRealEstateCache().getAddress().setPostcode(addressRealEstate.getPostcode());
    }

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
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: called!");
                    }
                });
    }

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
                            getRealEstateCache().setLatitude(placeDetails.getResult().getGeometry().getLocation().getLat());
                            getRealEstateCache().setLongitude(placeDetails.getResult().getGeometry().getLocation().getLng());

                            /* We use the latitude and longitude to fetch nearby places
                             * */
                            getNearbyPlaces(
                                    placeDetails.getResult().getGeometry().getLocation().getLat(),
                                    placeDetails.getResult().getGeometry().getLocation().getLng());


                        } else {
                            ToastHelper.toastShort(CreateNewListingActivity.this,
                                    "There is a problem with the latitude and longitude");
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

    @SuppressLint("CheckResult")
    private void getNearbyPlaces(double latitude, double longitude) {
        Log.d(TAG, "getNearbyPlaces: called!");

        /* Clear the cache
         * */
        getListOfPlacesRealEstateCache().clear();

        // TODO: 22/08/2018 Constraint the search with types!

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

                                    /* If the result passes all checks, add the place to the cache
                                     * */
                                    getListOfPlacesRealEstateCache().add(placeRealEstate);
                                    listPlaceRealEstateIds.add(placeRealEstate.getId());

                                }
                            }
                            getRealEstateCache().setListOfNearbyPointsOfInterestIds(listPlaceRealEstateIds);
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

    //INTERNET CONNECTION RECEIVER

    /**
     * Method that connects a broadcastReceiver to the activity.
     * It allows to notify the user about the internet state
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

    //INTERNAL STORAGE

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");

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

    private void configureOnClickRecyclerView() {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");
                        ToastHelper.toastShort(CreateNewListingActivity.this, getListOfImagesRealEstateCache().get(position).getDescription());
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void launchAddPhotoActivity() {
        Log.d(TAG, "launchAddPhotoActivity: called!");

        updateRealEstateCache();
        Utils.launchActivity(this, AddPhotoActivity.class);

    }

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
