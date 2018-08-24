package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.data.AppDatabase;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.utils.Utils;
import com.diegomfv.android.realestatemanager.viewmodel.SearchEngineViewModel;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.snatik.storage.Storage;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

// TODO: 24/08/2018 Insert Checkboxes programmatically according to the different types we have in the database
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
    CardView cardViewCity;

    @BindView(R.id.card_view_amount_photos_id)
    CardView cardViewAmountPhotos;

    @BindView(R.id.button_search_id)
    Button buttonSearch;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.linear_layout_checkboxes_id)
    LinearLayout checkBoxesLinearLayout;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private AutoCompleteTextView actvType;

    private TextView tvPrice;
    private RangeSeekBar seekBarPrice;

    private TextView tvSurfaceArea;
    private RangeSeekBar seekBarSurfaceArea;

    private TextView tvNumberOfRooms;
    private RangeSeekBar seekBarNumberOfRooms;

    private AutoCompleteTextView actvCity;

    private TextView tvAmountOfPhotos;
    private RangeSeekBar seekBarAmountOfPhotos;

    // TODO: 24/08/2018 Include CheckBoxes!

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<RealEstate> listOfListings;

    private List<PlaceRealEstate> listOfPlaceRealEstate;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Set<String> setOfTypes;

    private Set<String> setOfCities;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int currency;

    private ActionBar actionBar;

    private SearchEngineViewModel searchEngineViewModel;

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

        this.createModel();

        this.subscribeToModel(searchEngineViewModel);

        // TODO: 24/08/2018 Delete!
        this.addView();

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

                initSearch();

            } break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: 24/08/2018 Check this!
    private void addView () {
        Log.d(TAG, "addView: called!");

        CheckBox checkBox = new CheckBox(this);
        checkBoxesLinearLayout.addView(checkBox);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMarginStart(8);
        layoutParams.setMargins(0,8,0,0);

        checkBox.setLayoutParams(layoutParams);
        checkBox.setText("New Checkbox");

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //SINGLETON GETTERS

    private RealEstateManagerApp getApp () {
        Log.d(TAG, "getApp: called");
        return (RealEstateManagerApp) getApplication();
    }

    private AppDatabase getAppDatabase () {
        Log.d(TAG, "getAppDatabase: called!");
        return getApp().getDatabase();
    }

    private Storage getInternalStorage() {
        Log.d(TAG, "getInternalStorage: called!");
        return getApp().getInternalStorage();
    }

    private RealEstate getRealEstateCache () {
        Log.d(TAG, "getRealEstateCache: called!");
        return getApp().getRepository().getRealEstateCache();
    }

    private List<ImageRealEstate> getListOfImagesRealEstateCache () {
        Log.d(TAG, "getListOfImagesRealEstateCache: called!");
        return getApp().getRepository().getListOfImagesRealEstateCache();
    }

    private List<PlaceRealEstate> getListOfPlacesByNearbyCache () {
        Log.d(TAG, "getListOfImagesRealEstateCache: called!");
        return getApp().getRepository().getListOfPlacesRealEstateCache();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<RealEstate> getListOfListings () {
        Log.d(TAG, "getListOfListings: called!");

        if (listOfListings == null) {
            return listOfListings = new ArrayList<>();
        }
        return listOfListings;
    }

    private List<PlaceRealEstate> getListOfPlaceRealEstate () {
        Log.d(TAG, "getListOfPlaceRealEstate: called!");

        if (listOfPlaceRealEstate == null) {
            return listOfPlaceRealEstate = new ArrayList<>();
        }
        return listOfPlaceRealEstate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

//    private void createSets (List<RealEstate> listOfListings) {
//        Log.d(TAG, "createSets: called!");
//
//        for (int i = 0; i < listOfListings.size() ; i++) {
//            getSetOfTypes().add(listOfListings.get(i).getType());
//            getSetOfCities().add(listOfListings.get(i).getAddress());
//        }
//    }


    public Set<String> getSetOfTypes () {
        Log.d(TAG, "getSetOfTypes: called!");
        if (setOfTypes == null) {
            setOfTypes = new HashSet<>();
            //refreshSetOfTypes();
            return setOfTypes = new HashSet<>();
        }
        return setOfTypes;
    }

    public Set<String> getSetOfCities () {
        Log.d(TAG, "getSetOfCities: called!");
        if (setOfCities == null) {
            return setOfCities = new HashSet<>();
        }
        return setOfCities;
    }

    public void refreshSets () {
        Log.d(TAG, "refreshSets: called!");

    }

//    private Set<String> refreshSetOfTypes () {
//        Log.d(TAG, "refreshSetOfTypes: called!");
//
//        for (int i = 0; i < listOfListings.; i++) {
//
//        }
//    }

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
        this.actvCity = cardViewCity.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);

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
        setHint(cardViewCity, "City");

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

    private void createModel () {
        Log.d(TAG, "createModel: called!");

        SearchEngineViewModel.Factory factory = new SearchEngineViewModel.Factory(getApp());
        this.searchEngineViewModel = ViewModelProviders
                .of(this, factory)
                .get(SearchEngineViewModel.class);


    }

    private void subscribeToModel (SearchEngineViewModel searchEngineViewModel) {
        Log.d(TAG, "subscribeToModel: called!");

        if (searchEngineViewModel != null) {

            this.searchEngineViewModel.getObservableListOfListings().observe(this, new Observer<List<RealEstate>>() {
                @Override
                public void onChanged(@Nullable List<RealEstate> realEstates) {
                    Log.d(TAG, "onChanged: called!");
                    listOfListings = realEstates;
                    //createSets(listOfListings);
                }
            });

            this.searchEngineViewModel.getObservableAllPlacesRealEstate().observe(this, new Observer<List<PlaceRealEstate>>() {
                @Override
                public void onChanged(@Nullable List<PlaceRealEstate> placeRealEstates) {
                    Log.d(TAG, "onChanged: called!");
                    listOfPlaceRealEstate = placeRealEstates;
                }
            });
        }
    }

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

    private void configureAllAutocompleteTextViews () {
        Log.d(TAG, "configureAutocompleteTextViews: called!");

        this.configureAutocompleteTextView(actvType, setOfTypes.toArray(new String[setOfTypes.size()]));
        this.configureAutocompleteTextView(actvCity, setOfCities.toArray(new String[setOfCities.size()]));

    }

    @SuppressLint("CheckResult")
    private void configureAutocompleteTextView (AutoCompleteTextView autoCompleteTextView,
                                                String[] arrayOfStrings) {
        Log.d(TAG, "configureAutcompleteTextView: called!");

        ArrayAdapter<String> autocompleteAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1, //This layout has to be a textview
                arrayOfStrings
        );

        autoCompleteTextView.setAdapter(autocompleteAdapter);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initSearch () {
        Log.d(TAG, "initSearch: called!");

        List<RealEstate> listOfFilteredRealEstate = new ArrayList<>();

        int maxPrice = getMaxValueFromSeekBar(seekBarPrice);
        int minPrice = getMinValueFromSeekBar(seekBarPrice);

        if (currency == 1) {
            maxPrice = (int) Utils.convertEuroToDollar(maxPrice);
            minPrice = (int) Utils.convertEuroToDollar(minPrice);
        }

        for (int i = 0; i < listOfListings.size(); i++) {

            if (filterNotPassed(listOfListings.get(i))) {
                continue;
            }

            //insertListing(listOfListings.get(i));

        }



        // TODO: 24/08/2018 Take care in case price in euros.
        // TODO: 24/08/2018 Convert to to dollars!

        // TODO: 24/08/2018 DELETE ALL OF THIS





        printLog(getTextFromView(actvType));
        printLog(getTextFromView(actvCity));

        printLog(getMinValueFromSeekBar(seekBarPrice) + getMaxValueFromSeekBar(seekBarPrice) + "");
        printLog(getMinValueFromSeekBar(seekBarSurfaceArea) + getMaxValueFromSeekBar(seekBarSurfaceArea) + "");
        printLog(getMinValueFromSeekBar(seekBarNumberOfRooms) + getMaxValueFromSeekBar(seekBarNumberOfRooms) + "");
        printLog(getMinValueFromSeekBar(seekBarAmountOfPhotos) + getMaxValueFromSeekBar(seekBarAmountOfPhotos) + "");

        CheckBox checkBox = findViewById(R.id.checkbox1_id);
        printLog(getPointOfInterestIfChecked(checkBox));
        checkBox = findViewById(R.id.checkbox2_id);
        printLog(getPointOfInterestIfChecked(checkBox));
        checkBox = findViewById(R.id.checkbox3_id);
        printLog(getPointOfInterestIfChecked(checkBox));
        checkBox = findViewById(R.id.checkbox4_id);
        printLog(getPointOfInterestIfChecked(checkBox));
    }

    private String getTextFromView (TextView textView) {
        Log.d(TAG, "getTextFromView: called!");
        return Utils.capitalize(textView.getText().toString().trim());
    }

    private boolean seekBarUsed (RangeSeekBar rangeSeekBar) {
        Log.d(TAG, "seekBarNotUsed: called!");
        if (rangeSeekBar.getSelectedMinValue().equals(rangeSeekBar.getAbsoluteMinValue())
                && rangeSeekBar.getSelectedMaxValue().equals(rangeSeekBar.getAbsoluteMaxValue())) {
            return false;
        }
        return true;
    }

    private int getMaxValueFromSeekBar (RangeSeekBar rangeSeekBar) {
        Log.d(TAG, "getMaxValueFromSeekBar: called!");
        if (seekBarUsed(rangeSeekBar)) {
            return (int) rangeSeekBar.getSelectedMaxValue();
        }
        return -1;
    }

    private int getMinValueFromSeekBar (RangeSeekBar rangeSeekBar) {
        Log.d(TAG, "getMinValueFromSeekBar: called!");
        if (seekBarUsed(rangeSeekBar)) {
            return (int) rangeSeekBar.getSelectedMinValue();
        }
        return -1;
    }

    private String getPointOfInterestIfChecked (CheckBox checkBox) {
        Log.d(TAG, "getPointOfInterestIfChecked: called!");
        if (checkBox.isChecked()) {
            return getTextFromView(checkBox);
        }
        return "";
    }

    // TODO: 24/08/2018 Delete!
    private void printLog (String text) {
        Log.i(TAG, "printLog: " + text);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //FILTERS

    private boolean filterNotPassed (RealEstate realEstate) {
        Log.d(TAG, "filterNotPassed: called!");

        if (typeFilterNotPassed(realEstate)) {
            return true;
        }
        return false;

//        if (priceFilterNotPassed(realEstate)) {
//            return true;
//        }
//
//        if (surfaceAreaFilterNotPassed(realEstate)) {
//            return true;
//        }
//
//        if (numberOfRoomsFilterNotPassed(realEstate)) {
//            return true;
//        }
//
//        if (cityFilterNotPassed(realEstate)) {
//            return true;
//        }
//
//        if (amountOfPhotosFilterNotPassed(realEstate)) {
//            return true;
//        }
//
//        if (checkboxesFilterNotPassed(realEstate)) {
//            return true;
//        }
//        return false;
    }

    private boolean typeFilterNotPassed (RealEstate realEstate) {
        Log.d(TAG, "typeFilterNotPassed: called!");

//        if (realEstate.getType().contains())

        return true;
    }

//      if (listOfListings.get(i).getPrice() > minPrice && listOfListings.get(i).getPrice() < maxPrice) {

}
