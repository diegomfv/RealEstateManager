package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaHorizontalCreate;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.datamodels.RoomsRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.dialogfragments.DatePickerFragment;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * Created by Diego Fajardo on 23/08/2018.
 */

/**
 * This activity allows to modify an already existing listing. It does not allow to modify the
 * address and does not allow to delete images that were added before to the listing.
 */
public class EditListingActivity extends BaseActivity implements DatePickerFragment.DatePickerFragmentListener {

    private static final String TAG = EditListingActivity.class.getSimpleName();

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

    @BindView(R.id.card_view_sold_id)
    CardView cardViewSold;

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

    @BindView(R.id.checkbox_sold_id)
    CheckBox cbSold;

    private TextView tvSold;

    private String dateSold;

    @BindView(R.id.button_card_view_with_button_id)
    Button buttonEditAddress;

    @BindView(R.id.card_view_recyclerView_media_id)
    RecyclerView recyclerView;

    @BindView(R.id.button_add_edit_photo_id)
    Button buttonAddPhoto;

    @BindView(R.id.button_insert_edit_listing_id)
    Button buttonEditListing;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //RecyclerView Adapter
    private RVAdapterMediaHorizontalCreate adapter;

    private int currency;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.currency = Utils.readCurrentCurrencyShPref(this);

        Intent intent = getIntent();
        Bundle bundle = new Bundle();

        if (intent.getExtras() != null) {
            bundle.putParcelable(Constants.GET_PARCELABLE, intent.getExtras().getParcelable(Constants.SEND_PARCELABLE));
            RealEstate realEstate = bundle.getParcelable(Constants.GET_PARCELABLE);
            setRealEstateCache(realEstate);
            Log.i(TAG, "onCreateView: bundle = " + bundle);

            /* We update the price of the real estate cache in order to use it in Edit Activity.
             * From this moment on, this will be the price of the real estate cache. This is done
             * to allow passing the price between this activity and PhotoGridActivity without
             * manipulation.
             * */
            if (realEstate != null) {
                getRealEstateCache().setPrice(Utils.getValueAccordingToCurrency(currency, realEstate.getPrice()));
            }

            /* We only delete the cache when we come from MainActivity (we do not do it when we come
             * from PhotoGridActivity
             * */

            /* Here, we clone the realEstate object in the cache and from that moment on, we use
             * the cache object. We also use the model to access real estate images. We fill the cache
             * of images with those that are related to the real estate object
             * */
            this.prepareCache();

        } else {
            /* When we come from PhotoGridActivity,
            we keep using the object in the cache
            * */
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_edit_listing);
        unbinder = ButterKnife.bind(this);

        /* Configuring the layout
         * */
        this.configureLayout();

        Utils.showMainContent(progressBarContent, mainLayout);

        /* Configuring the RecyclerView
         * */
        this.configureRecyclerView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: called!");
        launchAreYouSureDialog();
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

    @OnClick({R.id.button_card_view_with_button_id, R.id.button_add_edit_photo_id, R.id.button_insert_edit_listing_id, R.id.checkbox_sold_id})
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: " + ((Button) view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_card_view_with_button_id: {
                ToastHelper.toastShort(this, "Sorry, the address cannot be modified");
            }
            break;

            case R.id.button_add_edit_photo_id: {
                launchEditPhotoActivity();
            }
            break;

            case R.id.button_insert_edit_listing_id: {
                editListing();

            }
            break;

            case R.id.checkbox_sold_id: {
                if (cbSold.isChecked()) {
                    launchDatePickerDialog();

                } else {
                    dateSold = "";
                    tvSold.setText(dateSold);
                }
            }
            break;

        }
    }

    /**
     * This callback gets triggered when the user inputs information in the DatePicker fragment and
     * clicks the positive button.
     */
    @Override
    public void onDateSet(Date date) {
        Log.d(TAG, "onDateSet: called!");

        if (dateIsValid(date)) {
            dateSold = Utils.dateToString(date);
            cbSold.setChecked(true);
            tvSold.setText(getDateSold());

        } else {
            ToastHelper.toastShort(this, "The date must be today or before today");
            dateSold = "";
            cbSold.setChecked(false);
            tvSold.setText(getDateSold());
        }
    }

    /**
     * This callback gets triggered when the user clicks the negative button in the DatePicker Fragment.
     */
    @Override
    public void onNegativeButtonClicked() {
        Log.d(TAG, "onNegativeButtonClicked: called!");
        dateSold = "";
        cbSold.setChecked(false);
        tvSold.setText(getDateSold());
    }

    /**
     * Checks if a date is valid (is not after today)
     */
    private boolean dateIsValid(Date date) {
        Log.d(TAG, "dateIsValid: called!");
        return !date.after(new Date());
    }

    /**
     * Listener for the "sold" checkbox
     */
    private View.OnClickListener tvSoldListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: called!");

            if (cbSold.isChecked()) {
                launchDatePickerDialog();
            }
        }
    };

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
     * Method that gets the cache ready with the necessary images
     */
    private void prepareCache() {
        Log.d(TAG, "prepareCache: called!");

        /* We delete the bitmapCache and fill it with the bitmaps related to the
         * real estate that is loaded
         * */
        getRepository().deleteAndFillBitmapCache(getRealEstateCache().getListOfImagesIds(), getInternalStorage(), getImagesDir());

        /* We fill the cache of Images Real Estate
        with those images related to the Real Estate Cache object
        * */
        getRepository().fillCacheWithImagesRelatedToRealEstateCache();

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
        this.getTextViews();
        this.getCheckbox();

        this.setAllHints();
        this.setTextButtons();
        this.setListeners();
        this.setAllInformation();
    }

    /**
     * Method to get a reference to the AutocompleteTextView.
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
     * Method to get reference to the TextView
     */
    private void getTextViews() {
        Log.d(TAG, "getTextViews: called!");
        this.tvSold = cardViewSold.findViewById(R.id.relative_layout_id).findViewById(R.id.textView_date_id);
    }

    /**
     * Method to get reference to the CheckBox
     */
    private void getCheckbox() {
        Log.d(TAG, "getCheckbox: called!");
        this.cbSold = cardViewSold.findViewById(R.id.relative_layout_id).findViewById(R.id.checkbox_sold_id);
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
     * Method to set the listener on the TextView
     */
    private void setListeners() {
        Log.d(TAG, "setListeners: called!");
        this.tvSold.setOnClickListener(tvSoldListener);
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
        this.buttonEditListing.setText("Edit Listing");
        this.buttonEditAddress.setText("Edit Address");
    }

    /**
     * Method to set the views with the information from the real estate cache object.
     */
    private void setAllInformation() {
        Log.d(TAG, "setAllInformation: called!");
        this.tvTypeOfBuilding.setText(getRealEstateCache().getType());
        this.tvPrice.setText(String.valueOf(getRealEstateCache().getPrice()));
        this.tvSurfaceArea.setText(String.valueOf(getRealEstateCache().getSurfaceArea()));
        this.tvNumberOfBedrooms.setText(String.valueOf(getRealEstateCache().getRooms().getBedrooms()));
        this.tvNumberOfBathrooms.setText(String.valueOf(getRealEstateCache().getRooms().getBathrooms()));
        this.tvNumberOfOtherRooms.setText(String.valueOf(getRealEstateCache().getRooms().getOtherRooms()));
        this.tvDescription.setText(getRealEstateCache().getDescription());
        this.tvAddress.setText(Utils.getAddressAsString(getRealEstateCache()));
        setSoldInfo();
    }

    /**
     * Method to set the information related to the sold state in the TextView and the Checkbox.
     */
    private void setSoldInfo() {
        Log.d(TAG, "setSoldInfo: called!");
        if (getRealEstateCache().getDateSale() == null || getRealEstateCache().getDateSale().isEmpty()) {
            dateSold = "";
            tvSold.setText(dateSold);
            cbSold.setChecked(false);

        } else {
            dateSold = getRealEstateCache().getDateSale();
            tvSold.setText(getRealEstateCache().getDateSale());
            cbSold.setChecked(true);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the RecyclerView.
     */
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
                        if (getListOfImagesRealEstateCache().get(position).getDescription().isEmpty()) {
                            ToastHelper.toastShort(EditListingActivity.this, "No description available");

                        } else {
                            ToastHelper.toastShort(EditListingActivity.this, getListOfImagesRealEstateCache().get(position).getDescription());
                        }

                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //CACHE UPDATE

    /**
     * Method to update the real estate cache information.
     */
    private void updateRealEstateCache() {
        Log.d(TAG, "updateRealEstateCache: called!");
        this.updateStringValues();
        this.updateFloatValues();
        this.updateIntegerValues();
    }

    /**
     * Method that updates the real estate cache information (strings).
     */
    private void updateStringValues() {
        Log.d(TAG, "updateStringValues: called!");
        this.getRealEstateCache().setType(Utils.capitalize(tvTypeOfBuilding.getText().toString().trim()));
        this.getRealEstateCache().setDescription(Utils.capitalize(tvDescription.getText().toString().trim()));
        this.getRealEstateCache().setDateSale(getDateSold());
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
     * Method that retrieves information about the date the listing was sold.
     */
    private String getDateSold() {
        Log.d(TAG, "getDateSold: called!");
        if (dateSold == null) {
            return "";

        } else {
            return dateSold;
        }
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

    /**
     * Method to check if the inputted information is correct. If it is, we proceed updating the
     * listing in the database.
     */
    private boolean allChecksCorrect() {
        Log.d(TAG, "allChecksCorrect: called!");

        /* Left like this for readability purposes
         * */

        if (!Utils.isNumeric(Utils.getStringFromTextView(tvPrice))) {
            return false;
        }

        if (!Utils.isNumeric(Utils.getStringFromTextView(tvSurfaceArea))) {
            return false;
        }

        /* If dateSold is different than null
         * and dateSold (which is a String) is parcelable to Date...
         * */
        if (dateSold != null && Utils.stringToDate(dateSold) != null) {
            if (getRealEstateCache().getDatePut() != null
                    && Utils.stringToDate(dateSold).after(Utils.stringToDate(getRealEstateCache().getDatePut()))) {
                return true;
            }
        }

        return true;
    }

    /**
     * Method that starts the update (edit) process
     */
    private void editListing() {
        Log.d(TAG, "editListing: called!");

        if (allChecksCorrect()) {

            Utils.hideMainContent(progressBarContent, mainLayout);

            /* Editing process starts
             * */
            updateRealEstate();

        } else {
            ToastHelper.toastShort(this,
                    "Sorry, there is a problem with some data");
        }
    }

    /**
     * Method that updates the images list of the real estate cache.
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
     * Method that updates the date sold of listing.
     */
    private void updateDateSold() {
        Log.d(TAG, "updateDateSold: called!");

        if (!cbSold.isChecked()) {
            getRealEstateCache().setDateSale(null);

        } else if (dateSold != null && dateSold.equals("")) {
            getRealEstateCache().setDateSale(null);

        } else {
            getRealEstateCache().setDateSale(dateSold);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to update the real estate cache object in the database
     */
    private void updateRealEstate() {
        Log.d(TAG, "updateRealEstate: called!");

        /* We update the real estate cache according to the information inputted in the views
         * */
        updateRealEstateCache();

        /* We update the price of the real estate cache if it is necessary (to insert it in dollars)
         * */
        updateRealEstateCachePrice(Utils.getFloatFromTextView(tvPrice));

        /* We update the images the real estate is related to
         * */
        updateImagesIdRealEstateCache();

        /* We update the date sold. If the checkbox is not checked, it might be because
         * the real estate has not been sold yet or because it was sold but now it is
         * on sale again. We leave this option open
         * */
        updateDateSold();

        /* From now on, we can update the real estate and the imagesRealEstate in the database
         * and insert the bitmaps in the internal storage
         * */
        updateRealEstateInTheDatabase();

    }

    /**
     * Method that uses RxJava to update the real estate in the database.
     */
    @SuppressLint("CheckResult")
    private void updateRealEstateInTheDatabase() {
        Log.d(TAG, "updateRealEstateInTheDatabase: called!");
        getRepository().updateRealEstate(getRealEstateCache())
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

                        /* After updating the RealEstate object, we update
                         * the list of ImagesRealEstate (basically,
                         * we add new ones to the database if they were entered)
                         * */
                        updateImagesRealEstateInTheDatabase();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    /**
     * Method that uses RxJava to update the images of listing in the database.
     */
    @SuppressLint("CheckResult")
    private void updateImagesRealEstateInTheDatabase() {
        Log.d(TAG, "updateImagesRealEstateInTheDatabase: called!");

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

                        /* After updating ImageRealEstate objects in the database,
                         * we insert the related bitmaps in the Images Directory. When this
                         * process finishes, we create the notification signaling that everything
                         * has gone correctly, we delete the cache and launch Main Activity
                         * */
                        insertAllBitmapsInImagesDirectory();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    /**
     * Method that inserts the Bitmaps in the database, creates a notification,
     * deletes the cache and launches MainActivity
     */
    public void insertAllBitmapsInImagesDirectory() {
        Log.d(TAG, "insertAllBitmapsInImagesDirectory: called!");

        /* Already done in a background thread
         * */
        for (Map.Entry<String, Bitmap> entry : getBitmapCache().entrySet()) {
            getRepository().addBitmapToBitmapCacheAndStorage(getInternalStorage(), getImagesDir(), entry.getKey(), entry.getValue());
        }

        createNotification();
        Utils.launchActivity(this, MainActivity.class);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that checks if it is necessary to launch a dialog asking the user if he/she is
     * sure to leave. It depends on if there is information inputted.
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
                        Utils.launchActivity(EditListingActivity.this, MainActivity.class);
                    }
                }
        );
    }

    /**
     * Method to launch EditPhotoActivity.
     * It uses an intent with Extras.
     */
    private void launchEditPhotoActivity() {
        Log.d(TAG, "launchEditPhotoActivity: called!");

        updateRealEstateCache();

        Intent intent = new Intent(this, PhotoGridActivity.class);
        intent.putExtra(Constants.INTENT_FROM_ACTIVITY, Constants.INTENT_FROM_EDIT);
        startActivity(intent);

    }

    /**
     * Method to launch DatePickerDialog.
     */
    private void launchDatePickerDialog() {
        Log.d(TAG, "launchDatePickerDialog: called!");

        DatePickerFragment.newInstance()
                .show(getSupportFragmentManager(), "DatePickerDialogFragment");

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
                        .setContentTitle(getResources().getString(R.string.notification_title_edit))
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
