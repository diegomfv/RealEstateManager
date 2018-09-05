package com.diegomfv.android.realestatemanager.ui.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.diegomfv.android.realestatemanager.data.datamodels.RoomsRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 23/08/2018.
 */
public class EditListingActivity extends BaseActivity {

    private static final String TAG = EditListingActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    @BindView(R.id.button_add_edit_address_id)
    Button buttonEditAddress;

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerView;

    @BindView(R.id.button_add_edit_photo_id)
    Button buttonAddPhoto;

    @BindView(R.id.button_insert_edit_listing_id)
    Button buttonEditListing;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBar actionBar;

    //RecyclerView Adapter
    private RVAdapterMediaHorizontalCreate adapter;

    private RealEstate realEstate;

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
            realEstate = bundle.getParcelable(Constants.GET_PARCELABLE);
            Log.i(TAG, "onCreateView: bundle = " + bundle);

            /* Here, we clone the realEstate object and from that moment on, we use the cache
             * */
            this.prepareCache();

        } else {
            realEstate = getRealEstateCache();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_create_new_listing);
        setTitle("Edit");
        unbinder = ButterKnife.bind(this);

        this.configureActionBar();

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

            } break;

            case R.id.menu_change_currency_button: {

                changeCurrency();
                Utils.updateCurrencyIcon(this, currency, item);
                updatePriceHint();

            } break;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick ({R.id.button_add_edit_photo_id, R.id.button_insert_edit_listing_id})
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
                if (allChecksCorrect()) {
                   editListing();
                }

            }
            break;
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
        Utils.writeCurrentCurrencyShPref(this,currency);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void prepareCache () {
        Log.d(TAG, "prepareCache: called!");

        getRepository().deleteCacheAndSets();

        /* We firstly clone the real estate object in the RealEstateCache
        * */
        getRepository().cloneRealEstate(realEstate);

        /* We delete the bitmapCache and fill it with the bitmaps related to the
        * real estate that is loaded
        * */
        getRepository().deleteAndFillBitmapCache(getRealEstateCache().getListOfImagesIds(), getInternalStorage(), getImagesDir());

        /* We also load the imageRealEstate objects in the corresponding cache to keep track
        * of them. This is needed to if the user decided to update the information */
        getRepository().fillCacheWithImagesRealEstateFromRealEstateCache();

    }

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.getAutocompleteTextViews();
        this.getTextViews();
        this.getSeekBars();

        this.setAllHints();
        this.setTextButtons();
        this.setCrystalSeekBarsMinMaxValues();
        this.setCrystalSeekBarsListeners();
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
    }

    private void getSeekBars() {
        Log.d(TAG, "getSeekBars: called!");
        this.seekBarBedrooms = cardViewNumberOfBedrooms.findViewById(R.id.single_seek_bar_id);
        this.seekBarBathrooms = cardViewNumberOfBathrooms.findViewById(R.id.single_seek_bar_id);
        this.seekBarOtherRooms = cardViewNumberOfOtherRooms.findViewById(R.id.single_seek_bar_id);
    }

    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");

        // TODO: 23/08/2018 Use Resources instead of hardcoded

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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void launchEditPhotoActivity() {
        Log.d(TAG, "launchEditPhotoActivity: called!");

        updateRealEstateCache();

        Intent intent = new Intent(this, PhotoGridActivity.class);
        intent.putExtra(Constants.INTENT_FROM_ACTIVITY, Constants.INTENT_FROM_EDIT);
        startActivity(intent);

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
        return true;
    }

    private void editListing () {
        Log.d(TAG, "editListing: called!");

        Log.w(TAG, "editListing: " + getRealEstateCache().toString());
        Log.i(TAG, "editListing: " + getListOfImagesRealEstateCache().toString());

        ToastHelper.toastShort(this, "Edit listing process executed! No changes yet! They have to be implemented!");
        createNotification();
    }
}
