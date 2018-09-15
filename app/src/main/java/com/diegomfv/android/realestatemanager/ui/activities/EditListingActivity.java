package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
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

import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaHorizontalCreate;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.datamodels.RoomsRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.dialogfragments.DatePickerFragment;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;
import com.diegomfv.android.realestatemanager.viewmodel.EditViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * Created by Diego Fajardo on 23/08/2018.
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

    @BindView(R.id.checkbox_sold_id)
    CheckBox cbSold;

    private TextView tvSold;

    private String dateSold;

    @BindView(R.id.button_add_edit_address_id)
    Button buttonEditAddress;

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerView;

    @BindView(R.id.button_add_edit_photo_id)
    Button buttonAddPhoto;

    @BindView(R.id.button_insert_edit_listing_id)
    Button buttonEditListing;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //RecyclerView Adapter
    private RVAdapterMediaHorizontalCreate adapter;

    private RealEstate realEstate;

    private int currency;

    private EditViewModel editViewModel;

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
            realEstate = bundle.getParcelable(Constants.GET_PARCELABLE);
            Log.i(TAG, "onCreateView: bundle = " + bundle);

            /* We only delete the cache when we come from MainActivity (we do not do it when we come
             * from PhotoGridActivity
             * */

            /* Here, we clone the realEstate object in the cache and from that moment on, we use
            * the cache object. We also use the model to access real estate images. We fill the cache
            * of images with those that are related to the real estate object
            * */
            this.prepareCache();
            this.createModel();

        } else {
            /* When we come from PhotoGridActivity, we keep using the object in the cache
            * */
            realEstate = getRealEstateCache();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_edit_listing);
        setTitle("Edit");
        unbinder = ButterKnife.bind(this);

        this.configureLayout();

        Utils.showMainContent(progressBarContent, mainLayout);

        this.configureRecyclerView();

        Log.i(TAG, "onCreate: " + getImagesDir());

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

            } break;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick ({R.id.button_add_edit_address_id, R.id.button_add_edit_photo_id, R.id.button_insert_edit_listing_id, R.id.checkbox_sold_id})
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: " + ((Button) view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_add_edit_address_id: {
                ToastHelper.toastShort(this, "Sorry, the address cannot be modified");
            }
            break;

            case R.id.button_add_edit_photo_id: {
                launchEditPhotoActivity();
            }
            break;

            case R.id.button_insert_edit_listing_id: {
                editListing();

            }break;

            case R.id.checkbox_sold_id: {
                if (cbSold.isChecked()) {
                    launchDatePickerDialog();

                } else {
                    dateSold = "";
                    tvSold.setText(dateSold);
                }
            } break;

        }
    }

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

    private boolean dateIsValid(Date date) {
        Log.d(TAG, "dateIsValid: called!");
        return !date.after(new Date());
    }

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

    private void changeCurrency() {
        Log.d(TAG, "changeCurrency: called!");

        if (this.currency == 0) {
            this.currency = 1;
        } else {
            this.currency = 0;
        }
        Utils.writeCurrentCurrencyShPref(this,currency);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void prepareCache () {
        Log.d(TAG, "prepareCache: called!");

        /* Firts*/
        getRepository().deleteCacheAndSets();

        /* We firstly clone the real estate object in the RealEstateCache
        * */
        getRepository().cloneRealEstate(realEstate);
        Log.i(TAG, "prepareCache: clone = " + getRealEstateCache().toString());

        /* We delete the bitmapCache and fill it with the bitmaps related to the
        * real estate that is loaded
        * */
        getRepository().deleteAndFillBitmapCache(getRealEstateCache().getListOfImagesIds(), getInternalStorage(), getImagesDir());

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void createModel() {
        Log.d(TAG, "createModel: called!");

        EditViewModel.Factory factory = new EditViewModel.Factory(getApp());
        this.editViewModel = ViewModelProviders
                .of(this, factory)
                .get(EditViewModel.class);

        this.subscribeToModel();

    }

    private void subscribeToModel() {
        Log.d(TAG, "subscribeToModel: called!");

        if (editViewModel != null) {
            this.editViewModel.getObservableListOfImagesRealEstate().observe(this, new Observer<List<ImageRealEstate>>() {
                @Override
                public void onChanged(@Nullable List<ImageRealEstate> listOfImagesRealEstate) {
                    Log.d(TAG, "onChanged: called!");
                    getRepository().fillCacheWithImagesRelatedToRealEstate(listOfImagesRealEstate);
                }
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.configureToolBar();

        this.getAutocompleteTextViews();
        this.getTextViews();
        this.getSeekBars();
        this.getCheckbox();

        this.setAllHints();
        this.setTextButtons();
        this.setCrystalSeekBarsMinMaxValues();
        this.setCrystalSeekBarsListeners();
        this.setListeners();
        this.setAllInformation();
    }

    private void getAutocompleteTextViews () {
        Log.d(TAG, "getAutocompleteTextViews: called!");
        this.tvTypeOfBuilding = cardViewType.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvPrice = cardViewPrice.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvSurfaceArea = cardViewSurfaceArea.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvDescription = cardViewDescription.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvAddress = cardViewAddress.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
    }

    private void getTextViews () {
        Log.d(TAG, "getTextViews: called!");
        this.tvNumberOfBedrooms = cardViewNumberOfBedrooms.findViewById(R.id.textView_title_id);
        this.tvNumberOfBathrooms = cardViewNumberOfBathrooms.findViewById(R.id.textView_title_id);
        this.tvNumberOfOtherRooms = cardViewNumberOfOtherRooms.findViewById(R.id.textView_title_id);
        this.tvSold = cardViewSold.findViewById(R.id.relative_layout_id).findViewById(R.id.textView_date_id);
    }

    private void getSeekBars() {
        Log.d(TAG, "getSeekBars: called!");
        this.seekBarBedrooms = cardViewNumberOfBedrooms.findViewById(R.id.single_seek_bar_id);
        this.seekBarBathrooms = cardViewNumberOfBathrooms.findViewById(R.id.single_seek_bar_id);
        this.seekBarOtherRooms = cardViewNumberOfOtherRooms.findViewById(R.id.single_seek_bar_id);
    }

    private void getCheckbox () {
        Log.d(TAG, "getCheckbox: called!");
        this.cbSold = cardViewSold.findViewById(R.id.relative_layout_id).findViewById(R.id.checkbox_sold_id);
    }

    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");

        // TODO: 23/08/2018 Use Resources instead of hardcoded text

        setHint(cardViewType, "Type");
        setHint(cardViewPrice, "Price (" + Utils.getCurrencySymbol(currency).substring(1) + ")");
        setHint(cardViewSurfaceArea, "Surface Area (sqm)");
        setHint(cardViewDescription, "Description");
        setHint(cardViewAddress, "Address");
    }

    private void setHint (CardView cardView, String hint) {
        Log.d(TAG, "setHint: called!");
        TextInputLayout textInputLayout = cardView.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint(hint);
    }

    private void setListeners () {
        Log.d(TAG, "setListeners: called!");
        tvSold.setOnClickListener(tvSoldListener);
    }

    private void updatePriceHint() {
        Log.d(TAG, "updatePriceHint: called!");
        TextInputLayout textInputLayout = cardViewPrice.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint("Price (" + Utils.getCurrencySymbol(currency).substring(1) + ")");
    }

    private void setTextButtons() {
        Log.d(TAG, "setTextButtons: called!");
        buttonEditListing.setText("Edit Listing");
        buttonEditAddress.setText("Edit Address");
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

    private void setTextDependingOnSeekBar (CrystalSeekbar seekBar, Number value) {
        Log.d(TAG, "updateSeekBarTextView: called!");
        if (seekBar == seekBarBedrooms) {
            tvNumberOfBedrooms.setText("Bedrooms (" + value + ")");
        } else if (seekBar == seekBarBathrooms) {
            tvNumberOfBathrooms.setText("Bathrooms (" + value + ")");
        } else if (seekBar == seekBarOtherRooms) {
            tvNumberOfOtherRooms.setText("Other Rooms (" + value + ")");
        }
    }

    private void setAllInformation() {
        Log.d(TAG, "setAllInformation: called!");
        tvTypeOfBuilding.setText(getRealEstateCache().getType());
        tvPrice.setText(String.valueOf((int)Utils.getPriceAccordingToCurrency(currency, getRealEstateCache().getPrice())));
        tvSurfaceArea.setText(String.valueOf(getRealEstateCache().getSurfaceArea()));
        tvNumberOfBedrooms.setText("Bedrooms (" + getRealEstateCache().getRooms().getBedrooms() + ")");
        tvNumberOfBathrooms.setText("Bathrooms (" + getRealEstateCache().getRooms().getBedrooms() + ")");
        tvNumberOfOtherRooms.setText("Other Rooms (" + getRealEstateCache().getRooms().getBedrooms() + ")");
        tvDescription.setText(getRealEstateCache().getDescription());
        tvAddress.setText(Utils.getAddressAsString(getRealEstateCache()));
        setSoldInfo();
    }

    private void setSoldInfo () {
        Log.d(TAG, "setSoldInfo: called!");
        if(realEstate.getDateSale() == null || realEstate.getDateSale().isEmpty()) {
            dateSold = "";
            tvSold.setText(dateSold);
            cbSold.setChecked(false);

        } else {
            dateSold = realEstate.getDateSale();
            tvSold.setText(realEstate.getDateSale());
            cbSold.setChecked(true);
        }
    }

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
                        ToastHelper.toastShort(EditListingActivity.this, getListOfImagesRealEstateCache().get(position).getDescription());
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //CACHE
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
        this.getRealEstateCache().setDateSale(getDateSold());
    }

    private String getDateSold () {
        Log.d(TAG, "getDateSold: called!");
        if (dateSold == null) {
            return "";

        } else {
            return dateSold;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void launchEditPhotoActivity() {
        Log.d(TAG, "launchEditPhotoActivity: called!");

        updateRealEstateCache();

        Intent intent = new Intent(this, PhotoGridActivity.class);
        intent.putExtra(Constants.INTENT_FROM_ACTIVITY, Constants.INTENT_FROM_EDIT);
        startActivity(intent);

    }

    private void launchDatePickerDialog () {
        Log.d(TAG, "launchDatePickerDialog: called!");

        DatePickerFragment.newInstance()
                .show(getSupportFragmentManager(), "DatePickerDialogFragment");

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean allChecksCorrect () {
        Log.d(TAG, "allChecksCorrect: called!");

        /* Left like this for readibility purposes
        * */

        if (!Utils.isNumeric(Utils.getStringFromTextView(tvPrice))) {
            return false;
        }

        if (!Utils.isNumeric(Utils.getStringFromTextView(tvSurfaceArea))) {
            return false;
        }

        if (dateSold != null
            && getRealEstateCache().getDatePut() != null
            && Utils.stringToDate(dateSold).after(Utils.stringToDate(getRealEstateCache().getDatePut()))) {
            return true;
        }

        return true;
    }

    private void editListing () {
        Log.d(TAG, "editListing: called!");

        if (allChecksCorrect()) {

            Utils.hideMainContent(progressBarContent, mainLayout);

            priceCorrectionIfNecessary();

            Log.w(TAG, "editListing: " + getRealEstateCache().toString());
            Log.w(TAG, "editListing: " + getListOfImagesRealEstateCache().toString());

            /* We update the images the real estate is related to
            * */
            updateImagesIdRealEstateCache();

            Log.w(TAG, "editListing: afterUpdate = " + getRealEstateCache().toString());
            Log.w(TAG, "editListing: afterUpdate = " + getListOfImagesRealEstateCache().toString());

            /* We update the date sold. If the checkbox is not checked, it might be because
            * the real estate has not been sold yet or because it was sold but now it is
            * on sale again. We leave this option open
            * */
            updateDateSold();

            Log.w(TAG, "editListing: bitmapCache = " + getBitmapCache() + " +++++++++++++++");
            Log.w(TAG, "editListing: listOfBitmapKeys() = " + getListOfBitmapKeys() + " +++++++++++++++");

            /* From now on, we can update the real estate and the imagesRealEstate in the database
            * and insert the bitmaps in the internal storage
            * */
            updateRealEstateInTheDatabase();

        } else {
            ToastHelper.toastShort(this, "Sorry, there is a problem with some data");
        }
    }

    /** This method updates real estate price info if the currency used is euros (the database
     * stores the information in dollars)
     * */
    private void priceCorrectionIfNecessary () {
        Log.d(TAG, "priceCorrectionIfNecessary: called!");

        //1: euros
        if (currency == 1) {
            float price = Utils.getIntegerFromTextView(tvPrice);
            getRealEstateCache().setPrice((int)Utils.convertEuroToDollar(price));
        }
    }

    private void updateImagesIdRealEstateCache() {
        Log.d(TAG, "updateImagesIdRealEstateCache: called!");

        List<String> listOfImagesIds = new ArrayList<>();

        for (int i = 0; i < getListOfImagesRealEstateCache().size(); i++) {
            listOfImagesIds.add(getListOfImagesRealEstateCache().get(i).getId());
        }
        this.getRealEstateCache().setListOfImagesIds(listOfImagesIds);
    }

    private void updateDateSold () {
        Log.d(TAG, "updateDateSold: called!");

        if (!cbSold.isChecked()) {
            getRealEstateCache().setDateSale("");

        } else {
            if (dateSold != null && !dateSold.equals("") )
                getRealEstateCache().setDateSale(dateSold);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("CheckResult")
    private void updateRealEstateInTheDatabase () {
        Log.d(TAG, "updateRealEstateInTheDatabase: called!");

        Single.just(getAppDatabase().realStateDao().updateRealEstate(getRealEstateCache()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d(TAG, "onSuccess: called!");

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

    @SuppressLint("CheckResult")
    private void updateImagesRealEstateInTheDatabase () {
        Log.d(TAG, "updateImagesRealEstateInTheDatabase: called!");

        Single.just(getAppDatabase().imageRealEstateDao().insertListOfImagesRealEstate(getListOfImagesRealEstateCache()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<long[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onSuccess(long[] longs) {
                        Log.d(TAG, "onSuccess: called!");

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

    /** Method that inserts the Bitmaps in the database, creates a notification,
     * deletes the cache and launches MainActivity
     * */
    public void insertAllBitmapsInImagesDirectory () {
        Log.d(TAG, "insertAllBitmapsInImagesDirectory: called!");

        for (Map.Entry<String, Bitmap> entry : getBitmapCache().entrySet()) {
            getRepository().addBitmapToBitmapCacheAndStorage(getInternalStorage(), getImagesDir(), entry.getKey(), entry.getValue());
        }

        createNotification();
        getRepository().deleteCacheAndSets();
        Utils.launchActivity(this, MainActivity.class);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

}
