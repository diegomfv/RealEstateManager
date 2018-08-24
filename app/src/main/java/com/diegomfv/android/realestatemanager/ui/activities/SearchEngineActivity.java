package com.diegomfv.android.realestatemanager.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;
import com.diegomfv.android.realestatemanager.utils.Utils;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SearchEngineActivity extends AppCompatActivity {

    private static final String TAG = SearchEngineActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.card_view_type_id)
    CardView cardViewType;

    @BindView(R.id.card_view_price_id)
    CardView cardViewPrice;

    @BindView(R.id.card_view_surface_area_id)
    CardView cardViewSurfaceArea;

    @BindView(R.id.card_view_number_rooms_id)
    CardView cardViewNumberOfRooms;

    @BindView(R.id.card_view_address_id)
    CardView cardViewAddress;

    @BindView(R.id.card_view_amount_photos_id)
    CardView cardViewAmountPhotos;

    @BindView(R.id.button_search_id)
    Button buttonSearch;

    private AutoCompleteTextView actvType;

    private TextView tvPrice;
    private RangeSeekBar seekBarPrice;

    private TextView tvSurfaceArea;
    private RangeSeekBar seekBarSurfaceArea;

    private TextView tvNumberOfRooms;
    private RangeSeekBar seekBarNumberOfRooms;

    private AutoCompleteTextView actvAddress;

    private TextView tvAmountOfPhotos;
    private RangeSeekBar seekBarAmountOfPhotos;

    // TODO: 24/08/2018 Include CheckBoxes!

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int currency;

    private ActionBar actionBar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.currency = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_search_engine);
        setTitle("Search");
        unbinder = ButterKnife.bind(this);

        this.configureActionBar();

        this.configureLayout();

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

                updatePriceTextView();

            } break;

            case android.R.id.home: {
                Utils.launchActivity(this, MainActivity.class);

            } break;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick (R.id.button_search_id)
    public void buttonClicked (View view) {
        Log.d(TAG, "buttonClicked: " + ((Button)view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_search_id: {

                int maxSelected = (int) seekBarNumberOfRooms.getSelectedMaxValue();
                int minSelected = (int) seekBarNumberOfRooms.getSelectedMinValue();

                ToastHelper.toastLong(this, "maxSelected = " + maxSelected + "; minSelected = " + minSelected);

            } break;
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

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.getTextViews();
        this.getSeekBars();

        this.setAllHints();
        this.setAllTexts();

        this.setMinMaxValuesRangeSeekBars();
    }

    private void getTextViews() {
        Log.d(TAG, "getTextViews: called!");

        this.actvType = cardViewType.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.actvAddress = cardViewAddress.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);

        this.tvPrice = cardViewPrice.findViewById(R.id.textView_title_id);
        this.tvSurfaceArea = cardViewSurfaceArea.findViewById(R.id.textView_title_id);
        this.tvNumberOfRooms = cardViewNumberOfRooms.findViewById(R.id.textView_title_id);
        this.tvAmountOfPhotos = cardViewAmountPhotos.findViewById(R.id.textView_title_id);
    }

    private void getSeekBars() {
        Log.d(TAG, "getSeekBars: called!");

        this.seekBarPrice = cardViewPrice.findViewById(R.id.range_seek_bar_id);
        this.seekBarSurfaceArea = cardViewSurfaceArea.findViewById(R.id.range_seek_bar_id);
        this.seekBarNumberOfRooms = cardViewNumberOfRooms.findViewById(R.id.range_seek_bar_id);
        this.seekBarAmountOfPhotos = cardViewAmountPhotos.findViewById(R.id.range_seek_bar_id);

    }

    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");

        // TODO: 23/08/2018 Use Resources instead of hardcoded text

        setHint(cardViewType, "Type");
        setHint(cardViewAddress, "Address");

    }

    private void setHint (CardView cardView, String hint) {
        Log.d(TAG, "setHint: called!");

        TextInputLayout textInputLayout = cardView.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint(hint);
    }

    private void setAllTexts () {
        Log.d(TAG, "setAllTexts: called!");

        tvPrice.setText("Price (thousands, $)");
        tvSurfaceArea.setText("Surface area (sqm)");
        tvNumberOfRooms.setText("Number of Rooms");
        tvAmountOfPhotos.setText("Amount of Photos");
    }

    private void setMinMaxValuesRangeSeekBars() {
        Log.d(TAG, "setMinMaxValuesRangeSeekBars: called!");

        seekBarPrice.setRangeValues(0, 2000);
        seekBarSurfaceArea.setRangeValues(50, 1000);
        seekBarNumberOfRooms.setRangeValues(1, 9);
        seekBarAmountOfPhotos.setRangeValues(1, 9);

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

    private void updatePriceTextView() {
        Log.d(TAG, "updatePriceTextView: called!");
        tvPrice.setText("Price (thousands, " + Utils.getCurrencySymbol(currency) + ")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureAllAutocompleteTextViews () {
        Log.d(TAG, "configureAutocompleteTextViews: called!");


    }

    private void configureAutocompleteTextView () {
        Log.d(TAG, "configureAutcompleteTextView: called!");


    }



    private void initSearch () {
        Log.d(TAG, "initSearch: called!");

        // TODO: 24/08/2018 Take care in case price in euros.
        // TODO: 24/08/2018 Converto to dollars!


    }



}
