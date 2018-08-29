package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.utils.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private TextInputAutoCompleteTextView tvNumberOfBedrooms;

    private TextInputAutoCompleteTextView tvNumberOfBathrooms;

    private TextInputAutoCompleteTextView tvNumberOfOtherRooms;

    private TextInputAutoCompleteTextView tvDescription;

    private TextInputEditText tvAddress;

    @BindView(R.id.button_add_address_id)
    Button buttonEditAddress;

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerView;

    @BindView(R.id.button_add_photo_id)
    Button buttonAddPhoto;

    @BindView(R.id.button_insert_listing_id)
    Button buttonEditListing;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBar actionBar;

    private RealEstate realEstate;

    private boolean accessInternalStorageGranted;

    private int currency;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.currency = Utils.readCurrentCurrencyShPref(this);

        this.accessInternalStorageGranted = false;

        Intent intent = getIntent();
        Bundle bundle = new Bundle();

        if (intent.getExtras() != null) {
            bundle.putParcelable(Constants.GET_PARCELABLE, intent.getExtras().getParcelable(Constants.SEND_PARCELABLE));
            realEstate = bundle.getParcelable(Constants.GET_PARCELABLE);
            Log.i(TAG, "onCreateView: bundle = " + bundle);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.insert_information_layout);
        setTitle("Edit");
        unbinder = ButterKnife.bind(this);

        this.configureActionBar();

        this.configureLayout();

        Utils.showMainContent(progressBarContent, mainLayout);

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

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.getAutocompleteTextViews();
        this.setAllHints();
        this.setTextButtons();
        this.setAllInformation();
    }

    private void getAutocompleteTextViews () {
        Log.d(TAG, "getAutocompleteTextViews: called!");

        this.tvTypeOfBuilding = cardViewType.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvPrice = cardViewPrice.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvSurfaceArea = cardViewSurfaceArea.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvNumberOfBedrooms = cardViewNumberOfBedrooms.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvNumberOfBathrooms= cardViewNumberOfBathrooms.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvNumberOfOtherRooms = cardViewNumberOfOtherRooms.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvDescription = cardViewDescription.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvAddress = cardViewAddress.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
    }

    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");

        // TODO: 23/08/2018 Use Resources instead of hardcoded

        setHint(cardViewType, "Type");
        setHint(cardViewPrice, "Price (" + Utils.getCurrencySymbol(currency).substring(1) + ")");
        setHint(cardViewSurfaceArea, "Surface Area (sqm)");
        setHint(cardViewNumberOfBedrooms, "Number of Bedrooms");
        setHint(cardViewNumberOfBathrooms, "Number of Bathrooms");
        setHint(cardViewNumberOfOtherRooms, "Number of Other Rooms");
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

    private void setAllInformation() {
        Log.d(TAG, "setAllInformation: called!");
        tvTypeOfBuilding.setText(realEstate.getType());
        tvPrice.setText(String.valueOf((int)Utils.getPriceAccordingToCurrency(currency, realEstate.getPrice())));
        tvSurfaceArea.setText(String.valueOf(realEstate.getSurfaceArea()));
        tvNumberOfBedrooms.setText(String.valueOf(realEstate.getRooms().getBedrooms()));
        tvNumberOfBathrooms.setText(String.valueOf(realEstate.getRooms().getBathrooms()));
        tvNumberOfOtherRooms.setText(String.valueOf(realEstate.getRooms().getOtherRooms()));
        tvDescription.setText(realEstate.getDescription());
        tvAddress.setText(Utils.getAddressAsString(realEstate));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////





}
