package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
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

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

public class SearchEngineActivity extends BaseActivity {

    private static final String TAG = SearchEngineActivity.class.getSimpleName();

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

    @BindView(R.id.card_view_cities_id)
    CardView cardViewCities;

    @BindView(R.id.card_view_localities_id)
    CardView cardViewLocalities;

    @BindView(R.id.card_view_number_rooms_id)
    CardView cardViewNumberOfRooms;

    @BindView(R.id.card_view_amount_photos_id)
    CardView cardViewAmountPhotos;

    @BindView(R.id.checkbox_on_sale_id)
    CheckBox checkBoxOnSale;

    @BindView(R.id.checkbox_sold_id)
    CheckBox checkBoxSold;

    @BindView(R.id.card_view_points_of_interest_id)
    CardView cardViewPointsOfInterest;

    @BindView(R.id.button_search_id)
    Button buttonSearch;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private LinearLayout linearLayoutTypes;
    private LinearLayout linearLayoutLocalities;
    private LinearLayout linearLayoutCities;
    private LinearLayout linearLayoutTypesPointsOfInterestNearby;

    private TextView tvTypes;
    private TextView tvCities;
    private TextView tvLocalities;
    private TextView tvPointsOfInterest;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<AppCompatCheckBox> listOfBuildingTypeCheckboxes;
    private List<AppCompatCheckBox> listOfCityCheckboxes;
    private List<AppCompatCheckBox> listOfLocalityCheckboxes;
    private List<AppCompatCheckBox> listOfPointOfInterestCheckboxes;

    private TextView tvPrice;
    private CrystalRangeSeekbar seekBarPrice;

    private TextView tvSurfaceArea;
    private CrystalRangeSeekbar seekBarSurfaceArea;

    private TextView tvNumberOfRooms;
    private CrystalRangeSeekbar seekBarNumberOfRooms;

    private TextView tvAmountOfPhotos;
    private CrystalRangeSeekbar seekBarAmountOfPhotos;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<RealEstate> listOfRealEstate;

    private List<PlaceRealEstate> listOfPlaceRealEstate;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //SETs used for adapters of the autocompleteTextView

    private Set<String> setOfBuildingTypes;

    private Set<String> setOfLocalities;

    private Set<String> setOfCities;

    private Set<String> setOfTypesOfPointsOfInterest;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int currency;

    /* "maxPriceInDollars" stores the max price directly from the real estate.
     * "maxPrice" varies depending on the currency*/
    float maxPriceInDollars;
    float maxPrice;

    float maxSurfaceArea;
    int maxNumberOfRooms;
    int maxAmountOfPhotos;

    private int updateCounter;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.currency = Utils.readCurrentCurrencyShPref(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_search_engine);
        unbinder = ButterKnife.bind(this);

        /* We get the lists from the repository. After that, the configuration process starts
         * */
        this.fetchListsAndSetsAsync();

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

            case R.id.menu_change_currency_button: {
                changeCurrency();
                Utils.updateCurrencyIcon(this, currency, item);
                updatePriceTextView();
            }
            break;

            case android.R.id.home: {
                Utils.launchActivity(this, MainActivity.class);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_search_id)
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: " + ((Button) view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_search_id: {
                initSearch();

                /* This commented section WAS USED to catch an exception related to the
                 * CHOREOGRAPHER. It is not used anymore but I left it here to remember this issue
                 * */
//                try {
//                    initSearch();
//                } catch(Exception e){
//                    // WindowManager$BadTokenException will be caught and the app would not display
//                    // the 'Force Close' message
//                    ToastHelper.toastShort(this, "Exception caught!");
//                }
            }
            break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that modifies the currency variable and writes the new info to sharedPreferences.
     * It also loads the fragment (or fragments).
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

    /**
     * Method to set the price of the TextView related to price
     */
    private void updatePriceTextView() {
        Log.d(TAG, "updatePriceTextView: called!");
        updateMaxPrice();
        setMinMaxValues(seekBarPrice, 0, maxPrice);
        tvPrice.setText("Price (" + Utils.getCurrencySymbol(currency) + ") - [" + 0 + ", " + seekBarPrice.getSelectedMaxValue() + "]");

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for listOfRealEstate
     */
    private List<RealEstate> getListOfRealEstate() {
        Log.d(TAG, "getListOfRealEstate: called!");
        if (listOfRealEstate == null) {
            return listOfRealEstate = new ArrayList<>();
        }
        return listOfRealEstate;
    }

    /**
     * Setter for listOfRealEstate
     */
    private void setListRealEstate(List<RealEstate> realEstateList) {
        Log.d(TAG, "setListRealEstate: called!");
        this.listOfRealEstate = realEstateList;
    }

    /**
     * Getter for listOfPlaceRealEstate
     */
    private List<PlaceRealEstate> getListOfPlaceRealEstate() {
        Log.d(TAG, "getListOfPlaceRealEstate: called!");
        if (listOfPlaceRealEstate == null) {
            return listOfPlaceRealEstate = new ArrayList<>();
        }
        return listOfPlaceRealEstate;
    }

    /**
     * Setter for listOfPlaceRealEstate
     */
    private void setListOfPlaceRealEstate(List<PlaceRealEstate> listOfPlaceRealEstate) {
        Log.d(TAG, "setListRealEstate: called!");
        this.listOfPlaceRealEstate = listOfPlaceRealEstate;
    }

    /**
     * Getter for setOfBuildingTypes.
     */
    private Set<String> getSetOfBuildingTypes() {
        Log.d(TAG, "getSetOfBuildingTypes: called!");
        if (setOfBuildingTypes == null) {
            return setOfBuildingTypes = new HashSet<>();
        }
        return setOfBuildingTypes;

    }

    /**
     * Getter for setOfLocalities.
     */
    private Set<String> getSetOfLocalities() {
        Log.d(TAG, "getSetOfLocalities: called!");
        if (setOfLocalities == null) {
            return setOfLocalities = new HashSet<>();
        }
        return setOfLocalities;
    }

    /**
     * Getter for setOfCities.
     */
    private Set<String> getSetOfCities() {
        Log.d(TAG, "getSetOfCities: called!");
        if (setOfCities == null) {
            return setOfCities = new HashSet<>();
        }
        return setOfCities;
    }

    /**
     * Getter for setOfTypesOfPointsOfInterest
     */
    private Set<String> getSetOfTypesOfPointsOfInterest() {
        Log.d(TAG, "getSetOfTypesOfPointsOfInterest: called!");
        if (setOfTypesOfPointsOfInterest == null) {
            return setOfTypesOfPointsOfInterest = new HashSet<>();
        }
        return setOfTypesOfPointsOfInterest;
    }


    /**
     * Method to get both lists, list of real estate and list of places real estate, and init the
     * layout configuration
     */
    @SuppressLint("CheckResult")
    private void fetchListsAndSetsAsync() {
        Log.d(TAG, "fetchListsAndSetsAsync: called!");

        getRepository().getAllListingsRealEstateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<RealEstate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onNext(List<RealEstate> realEstates) {
                        Log.d(TAG, "onNext: " + realEstates);

                        /* We store the list of real estates
                         * */
                        setListRealEstate(realEstates);

                        /* We get necessary info for the search engine and store it
                         * */
                        getMaxValuesForSeekBars();

                        /* We get the list of places real estate and init configuration process
                         * */
                        getPlacesRealEstate();
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

    /**
     * Method to get the list of places real estate and start configuration process
     */
    @SuppressLint("CheckResult")
    private void getPlacesRealEstate() {
        Log.d(TAG, "getPlacesRealEstate: called!");

        getRepository().getAllPlacesRealEstateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<PlaceRealEstate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onNext(List<PlaceRealEstate> placeRealEstates) {
                        Log.d(TAG, "onNext: " + placeRealEstates);

                        /* We store the list of places real estate
                         * */
                        setListOfPlaceRealEstate(placeRealEstates);

                        /* We start the layout configuration*/
                        configureLayout();

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

    /**
     * This method is used to store necessary information that will be used later when
     * configuring the layout. They are values to set the max values of the seekbars
     */
    private void getMaxValuesForSeekBars() {
        Log.d(TAG, "getMaxValuesForSeekBars: called!");

        maxPriceInDollars = 0;
        maxPrice = 0;
        maxSurfaceArea = 0;
        maxNumberOfRooms = 0;
        maxAmountOfPhotos = 0;

        for (int i = 0; i < getListOfRealEstate().size(); i++) {
            if (maxPriceInDollars < getListOfRealEstate().get(i).getPrice()) {
                maxPriceInDollars = getListOfRealEstate().get(i).getPrice();
            }
            if (maxSurfaceArea < getListOfRealEstate().get(i).getSurfaceArea()) {
                maxSurfaceArea = getListOfRealEstate().get(i).getSurfaceArea();
            }
            if (maxNumberOfRooms < getListOfRealEstate().get(i).getRooms().getTotalNumberOfRooms()) {
                maxNumberOfRooms = getListOfRealEstate().get(i).getRooms().getTotalNumberOfRooms();
            }
            if (maxAmountOfPhotos < getListOfRealEstate().get(i).getListOfImagesIds().size()) {
                maxAmountOfPhotos = getListOfRealEstate().get(i).getListOfImagesIds().size();
            }
        }

        /* Depending on if we are using euros or dollars, we set the max price the seekbar
         * can show
         * */
        updateMaxPrice();
    }

    /**
     * Method to configure the sets.
     */
    private void configureSets() {
        Log.d(TAG, "configureSets: called!");
        setOfBuildingTypes = null;
        setOfLocalities = null;
        setOfCities = null;
        setOfTypesOfPointsOfInterest = null;
        fillSets();
    }

    /**
     * Method to fill the sets
     */
    private void fillSets() {
        Log.d(TAG, "fillSets: called!");
        fillSetOfBuildingTypes(getListOfRealEstate());
        fillSetOfLocalities(getListOfRealEstate());
        fillSetOfCities(getListOfRealEstate());
        fillSetOfTypesOfPointsOfInterest();
    }

    /**
     * Method to fill the setOfBuildingTypes
     */
    private void fillSetOfBuildingTypes(List<RealEstate> list) {
        Log.d(TAG, "fillSetOfBuildingTypes: called!");
        Log.w(TAG, "fillSetOfBuildingTypes: " + list);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {

                /* We check that the type is not null or empty to avoid adding
                 * a checkbox with no text
                 * */
                if (list.get(i).getType() != null
                        && !list.get(i).getType().isEmpty())
                    getSetOfBuildingTypes().add(list.get(i).getType());
            }
        }
    }

    /**
     * Method to fill the setOfLocalities
     */
    private void fillSetOfLocalities(List<RealEstate> list) {
        Log.d(TAG, "fillSetOfLocalities: called!");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                getSetOfLocalities().add(list.get(i).getAddress().getLocality());
            }
        }
    }

    /**
     * Method to fill the setOfCities
     */
    private void fillSetOfCities(List<RealEstate> list) {
        Log.d(TAG, "fillSetOfCities: called!");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                getSetOfCities().add(list.get(i).getAddress().getCity());
            }
        }
    }

    /**
     * Method to fill the setOfTypesOfPointsOfInterest
     */
    private void fillSetOfTypesOfPointsOfInterest() {
        Log.d(TAG, "fillSetOfTypesOfPointsOfInterest: called!");
        for (int i = 0; i < getListOfPlaceRealEstate().size(); i++) {
            getSetOfTypesOfPointsOfInterest().addAll(getListOfPlaceRealEstate().get(i).getTypesList());
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the toolbar.
     * Depending on mainMenu, on the button behaves one way or another. With mainMenu = true,
     * user can return to AuthLoginAtivity via a dialog that will pop-up. With mainMenu = false,
     * the user will go to SearchEngineActivity
     */
    private void configureToolBar() {
        Log.d(TAG, "configureToolBar: called!");

        setSupportActionBar(toolbar);
        //setTitle("Create a New Listing");
        setOverflowButtonColor(toolbar, Color.WHITE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");
                Utils.launchActivity(SearchEngineActivity.this, MainActivity.class);
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
     * Method to configure the layout.
     */
    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.configureToolBar();

        this.getViewGroups();
        this.getTextViews();
        this.getSeekBars();

        this.setCrystalSeekBarListeners();
        this.setAllTexts();

        this.setMinMaxValuesRangeSeekBars();
        this.configureSets();

        this.addCheckboxesToLayout();

        /* When the configuration process ends, we load the main content and hide the progress bar
         * */
        Utils.showMainContent(progressBarContent, mainLayout);

    }

    /**
     * Method to get references to the ViewGroups.
     */
    private void getViewGroups() {
        Log.d(TAG, "getViewGroups: called!");
        this.linearLayoutTypes = cardViewType.findViewById(R.id.linear_layout_checkboxes_id);
        this.linearLayoutCities = cardViewCities.findViewById(R.id.linear_layout_checkboxes_id);
        this.linearLayoutLocalities = cardViewLocalities.findViewById(R.id.linear_layout_checkboxes_id);
        this.linearLayoutTypesPointsOfInterestNearby = cardViewPointsOfInterest.findViewById(R.id.linear_layout_checkboxes_id);
    }

    /**
     * Method to get references to the TextViews.
     */
    private void getTextViews() {
        Log.d(TAG, "getTextViews: called!");

        this.tvPrice = cardViewPrice.findViewById(R.id.textView_title_id);
        this.tvSurfaceArea = cardViewSurfaceArea.findViewById(R.id.textView_title_id);
        this.tvNumberOfRooms = cardViewNumberOfRooms.findViewById(R.id.textView_title_id);
        this.tvAmountOfPhotos = cardViewAmountPhotos.findViewById(R.id.textView_title_id);

        this.tvTypes = linearLayoutTypes.findViewById(R.id.textView_add_checkboxes);
        this.tvCities = linearLayoutCities.findViewById(R.id.textView_add_checkboxes);
        this.tvLocalities = linearLayoutLocalities.findViewById(R.id.textView_add_checkboxes);
        this.tvPointsOfInterest = linearLayoutTypesPointsOfInterestNearby.findViewById(R.id.textView_add_checkboxes);

    }

    /**
     * Method to get references to the SeekBars.
     */
    private void getSeekBars() {
        Log.d(TAG, "getSeekBars: called!");
        this.seekBarPrice = cardViewPrice.findViewById(R.id.single_seek_bar_id);
        this.seekBarSurfaceArea = cardViewSurfaceArea.findViewById(R.id.single_seek_bar_id);
        this.seekBarNumberOfRooms = cardViewNumberOfRooms.findViewById(R.id.single_seek_bar_id);
        this.seekBarAmountOfPhotos = cardViewAmountPhotos.findViewById(R.id.single_seek_bar_id);

    }

    /**
     * Method to set the text of all TextViews.
     */
    private void setAllTexts() {
        Log.d(TAG, "setAllTexts: called!");
        updatePriceTextView();
        tvSurfaceArea.setText("Surface area (sqm) - [" + 0 + ", " + (int) maxSurfaceArea + "]");
        tvNumberOfRooms.setText("Number of Rooms - [" + 0 + ", " + maxNumberOfRooms + "]");
        tvAmountOfPhotos.setText("Amount of Photos - [" + 0 + ", " + maxAmountOfPhotos + "]");

        tvTypes.setText("Types of Building");
        tvCities.setText("Cities");
        tvLocalities.setText("Localities");
        tvPointsOfInterest.setText("Types of Points of Interest");
    }

    /**
     * Method to set the min an max values in the seekBars.
     */
    private void setMinMaxValuesRangeSeekBars() {
        Log.d(TAG, "setMinMaxValuesRangeSeekBars: called!");
        setMinMaxValues(seekBarPrice, 0, maxPriceInDollars);
        setMinMaxValues(seekBarSurfaceArea, 0, maxSurfaceArea);
        setMinMaxValues(seekBarNumberOfRooms, 0, maxNumberOfRooms);
        setMinMaxValues(seekBarAmountOfPhotos, 0, maxAmountOfPhotos);

        if (maxPriceInDollars == 0) {
            seekBarPrice.setEnabled(false);
            tvPrice.setText("Price (Disabled)");
        }

        if (maxSurfaceArea == 0) {
            seekBarNumberOfRooms.setEnabled(false);
            tvSurfaceArea.setText("Surface Area (Disabled)");
        }

        if (maxNumberOfRooms == 0) {
            seekBarNumberOfRooms.setEnabled(false);
            tvNumberOfRooms.setText("Number of Rooms (Disabled)");
        }

        if (maxAmountOfPhotos == 0) {
            seekBarAmountOfPhotos.setEnabled(false);
            tvAmountOfPhotos.setText("Amount of Photos (Disabled)");
        }
    }

    /**
     * Sets the min an max values for a seekBar.
     */
    private void setMinMaxValues(CrystalRangeSeekbar seekBar, float min, float max) {
        Log.d(TAG, "setMinMaxValues: called!");
        seekBar.setMinValue(min);
        seekBar.setMaxValue(max);
    }

    /**
     * Sets the listeners for the seekBars.
     */
    private void setCrystalSeekBarListeners() {
        Log.d(TAG, "setCrystalSeekBarListeners: called!");
        setListeners(seekBarPrice);
        setListeners(seekBarSurfaceArea);
        setListeners(seekBarNumberOfRooms);
        setListeners(seekBarAmountOfPhotos);
    }

    /**
     * Sets the listener for a seekBar.
     */
    private void setListeners(final CrystalRangeSeekbar seekBar) {
        Log.d(TAG, "setListeners: called!");

        seekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                Log.d(TAG, "valueChanged: min = " + minValue + " - " + "maxValue = " + maxValue);
                setTextDependingOnSeekBar(seekBar, minValue, maxValue);
            }
        });

        seekBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d(TAG, "finalValue: called1");
                setTextDependingOnSeekBar(seekBar, minValue, maxValue);
            }
        });
    }

    /**
     * Sets the text of the TextView related to a seekBar depending on the seekBar.
     */
    private void setTextDependingOnSeekBar(CrystalRangeSeekbar seekBar, Number min, Number max) {
        Log.d(TAG, "setTextDependingOnTextView: called!");
        if (seekBar == seekBarPrice) {
            tvPrice.setText("Price (" + Utils.getCurrencySymbol(currency) + ") - [" + min + ", " + max + "]");
        } else if (seekBar == seekBarSurfaceArea) {
            tvSurfaceArea.setText("Surface Area (sqm) - [" + min + ", " + max + "]");
        } else if (seekBar == seekBarNumberOfRooms) {
            tvNumberOfRooms.setText("Number of Rooms - [" + min + ", " + max + "]");
        } else if (seekBar == seekBarAmountOfPhotos) {
            tvAmountOfPhotos.setText("Amount of Photos - [" + min + ", " + max + "]");
        }
    }

    private void updateMaxPrice() {
        Log.d(TAG, "updateMaxPrice: called!");
        if (currency == 0) {
            maxPrice = maxPriceInDollars;

        } else {
            maxPrice = Utils.convertDollarToEuro(maxPriceInDollars);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that uses the sets to fill the layout with checkboxes
     */
    private void addCheckboxesToLayout() {
        Log.d(TAG, "addCheckboxesToLayout: called!");

        Log.w(TAG, "addCheckboxesToLayout: " + getSetOfTypesOfPointsOfInterest());

        for (String buildingType : getSetOfBuildingTypes()) {
            fillWithCheckbox(
                    linearLayoutTypes,
                    buildingType,
                    getListOfBuildingTypeCheckboxes());
        }

        for (String city : getSetOfCities()) {
            fillWithCheckbox(
                    linearLayoutCities,
                    city,
                    getListOfCityCheckboxes());
        }

        for (String locality : getSetOfLocalities()) {
            fillWithCheckbox(
                    linearLayoutLocalities,
                    locality,
                    getListOfLocalityCheckboxes());
        }

        for (String typeOfPointOfInterest : getSetOfTypesOfPointsOfInterest()) {
            fillWithCheckbox(
                    linearLayoutTypesPointsOfInterestNearby,
                    typeOfPointOfInterest,
                    getListOfPointOfInterestCheckboxes());
        }
    }

    /**
     * Method that fills the layout with checkboxes according to certain information
     *
     * @param linearLayout The layout that will contain the checkbox
     * @param value The value that we are passing (type, city, locality or point of interest)
     * @param listOfCheckboxes A list (global value) that will contain the references to the created checkboxes
     *
     */
    @SuppressLint("RestrictedApi")
    private void fillWithCheckbox(LinearLayout linearLayout, String value, List<AppCompatCheckBox> listOfCheckboxes) {
        Log.d(TAG, "addPointsOfInterestCheckboxesToLayout: called!");
        AppCompatCheckBox checkBox = new AppCompatCheckBox(this);
        linearLayout.addView(checkBox);

//
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        layoutParams.setMarginStart(8);
//        layoutParams.setMargins(8, 8, 0, 0);
//
//        checkBox.setLayoutParams(layoutParams);

        checkBox.setText(Utils.capitalize(Utils.replaceUnderscore(value)));
        checkBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        checkBox.setTag(value);
        checkBox.setSupportButtonTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));

        /* We fill the list that
         * we have passed as an argument
         * */
        listOfCheckboxes.add(checkBox);
    }

    /**
     * Getter for listOfBuildingTypeCheckboxes
     */
    public List<AppCompatCheckBox> getListOfBuildingTypeCheckboxes() {
        Log.d(TAG, "getListOfBuildingTypeCheckboxes: called!");
        if (listOfBuildingTypeCheckboxes == null) {
            return listOfBuildingTypeCheckboxes = new ArrayList<>();
        }
        return listOfBuildingTypeCheckboxes;
    }

    /**
     * Getter for listOfCityCheckboxes
     */
    public List<AppCompatCheckBox> getListOfCityCheckboxes() {
        Log.d(TAG, "getListOfCityCheckboxes: called!");
        if (listOfCityCheckboxes == null) {
            return listOfCityCheckboxes = new ArrayList<>();
        }
        return listOfCityCheckboxes;
    }

    /**
     * Getter for listOfLocalityCheckboxes
     */
    public List<AppCompatCheckBox> getListOfLocalityCheckboxes() {
        Log.d(TAG, "getListOfBuildingTypeCheckboxes: called!");
        if (listOfLocalityCheckboxes == null) {
            return listOfLocalityCheckboxes = new ArrayList<>();
        }
        return listOfLocalityCheckboxes;
    }

    /**
     * Getter for listOfPointOfInterestCheckboxes
     */
    public List<AppCompatCheckBox> getListOfPointOfInterestCheckboxes() {
        Log.d(TAG, "getListOfPointOfInterestCheckboxes: called!");
        if (listOfPointOfInterestCheckboxes == null) {
            return listOfPointOfInterestCheckboxes = new ArrayList<>();
        }
        return listOfPointOfInterestCheckboxes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to init the search of listings
     */
    @SuppressLint("CheckResult")
    private void initSearch() {
        Log.d(TAG, "initSearch: called!");

        /* We disable user interaction
         * */
        Utils.hideMainContent(progressBarContent, mainLayout);

        /* We use RxJava to load the process in a background thread.
         * Try/catch is used to catch a possible ChoreoGrapher exception
         * */
        try {
            Single.just("Init process")
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribeWith(new SingleObserver<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "onSubscribe: called!");
                        }

                        @Override
                        public void onSuccess(String s) {
                            Log.d(TAG, "onSuccess: " + s);

                            /* This variable will allow us to see if at least
                             * we found one real estate. If that is the case,
                             * we will proceed showing them.
                             * */
                            boolean atLeastOneFound = false;

                            for (int i = 0; i < getListOfRealEstate().size(); i++) {

                                if (allFiltersPassed(getListOfRealEstate().get(i))) {
                                    getListOfRealEstate().get(i).setFound(true);
                                    atLeastOneFound = true;

                                } else {
                                    getListOfRealEstate().get(i).setFound(false);
                                }

                            }

                            if (atLeastOneFound) {

                                /* We found at least one real estate matching the criteria.
                                 *
                                 * We update the found information of each real estate so the ViewModel can show these
                                 * real estates in MainActivity. The method also launches the activity.
                                 * */
                                updateRealEstatesWithFoundInfo(getListOfRealEstate());

                            } else {

                                /* If no real estates were found, we notify the user and
                                 * show the main layout
                                 * */
                                new Handler(SearchEngineActivity.this.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "run: called!");
                                        ToastHelper.toastShort(SearchEngineActivity.this,
                                                "Sorry, no real estates were found matching this criteria");
                                        Utils.showMainContent(progressBarContent, mainLayout);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                            notifyUserErrorDuringProcess();
                        }
                    });

        } catch (Exception e) {
            notifyUserErrorDuringProcess();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to check if a listing passes the type filter
     */
    private boolean typeFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "typeFilterPassed: called!");
        
        List<String> listOfCheckedTypes = new ArrayList<>();

        /* A list is filled with all the texts of the related checked checkboxes
         * */
        for (int i = 0; i < getListOfBuildingTypeCheckboxes().size(); i++) {
            if (getListOfBuildingTypeCheckboxes().get(i).isChecked()) {
                listOfCheckedTypes.add(Utils.getStringFromTextView(getListOfBuildingTypeCheckboxes().get(i)));
            }
        }

        /* Means the user did not check any checkbox
         * */
        if (listOfCheckedTypes.size() == 0) {
            return true;
        }

        /* The user checked at least one checkbox
         * */
        for (int i = 0; i < listOfCheckedTypes.size(); i++) {
            if (realEstate.getType().equalsIgnoreCase(listOfCheckedTypes.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check if a listing passes the price filter
     */
    private boolean priceFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "priceFilter: called!");

        if (seekBarPrice.getSelectedMinValue().floatValue() == 0
                && seekBarPrice.getSelectedMaxValue().floatValue() == maxPrice) {

            /* In this case, the seekBar has not been used and the filter is automatically passed
             * */
            return true;

        } else {

            float minPriceSelected;
            float maxPriceSelected;

            /* Firstly, we convert the euros to dollars if necessary
             * */
            if (currency == 0) {
                minPriceSelected = seekBarPrice.getSelectedMinValue().floatValue();
                maxPriceSelected = seekBarPrice.getSelectedMaxValue().floatValue();

            } else {
                minPriceSelected = Utils.convertEuroToDollar(seekBarPrice.getSelectedMinValue().floatValue());
                maxPriceSelected = Utils.convertEuroToDollar(seekBarPrice.getSelectedMaxValue().floatValue());
            }

            /* The user selected a price filter.
             * Check if the real estate passes it.
             * */
            if (realEstate.getPrice() >= minPriceSelected
                    && realEstate.getPrice() <= maxPriceSelected) {

                /* The filter has been passed
                 * */
                return true;

            } else {

                /* The filter has not been passed
                 * */
                return false;
            }
        }
    }

    private boolean surfaceAreaFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "surfaceAreaFilterPassed: called!");

        if (seekBarSurfaceArea.getSelectedMinValue().floatValue() == 0
                && seekBarSurfaceArea.getSelectedMaxValue().floatValue() == maxSurfaceArea) {

            /* In this case, the seekBar has not been used and the filter is automatically passed
             * */
            return true;

        } else {

            /* The user selected a surfaceArea filter.
             * Check if the real estate passes it.
             * */
            if (realEstate.getSurfaceArea() >= seekBarSurfaceArea.getSelectedMinValue().floatValue()
                    && realEstate.getSurfaceArea() <= seekBarSurfaceArea.getSelectedMaxValue().floatValue()) {

                /* The filter has been passed
                 * */
                return true;

            } else {

                /* The filter has not been passed
                 * */
                return false;
            }
        }
    }

    /**
     * Method to check if a listing passes the number of rooms filter
     */
    private boolean numberOfRoomsFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "numberOfRoomsFilterPassed: called!");

        if (seekBarNumberOfRooms.getSelectedMinValue().floatValue() == 0
                && seekBarNumberOfRooms.getSelectedMaxValue().floatValue() == maxNumberOfRooms) {

            /* In this case, the seekBar has not been used and the filter is automatically passed
             * */
            return true;

        } else {

            /* The user selected a total rooms filter.
             * Check if the real estate passes it.
             * */
            if (realEstate.getRooms().getTotalNumberOfRooms() >= seekBarNumberOfRooms.getSelectedMinValue().floatValue()
                    && realEstate.getRooms().getTotalNumberOfRooms() <= seekBarNumberOfRooms.getSelectedMaxValue().floatValue()) {

                /* The filter has been passed
                 * */
                return true;

            } else {

                /* The filter has not been passed
                 * */
                return false;
            }
        }
    }

    /**
     * Method to check if a listing passes the locality filter
     */
    private boolean localityFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "localityFilterPassed: called!");

        List<String> listOfCheckedTypes = new ArrayList<>();

        /* A list is filled with all the texts of the related checked checkboxes
         * */
        for (int i = 0; i < getListOfLocalityCheckboxes().size(); i++) {
            if (getListOfLocalityCheckboxes().get(i).isChecked()) {
                listOfCheckedTypes.add(Utils.getStringFromTextView(getListOfLocalityCheckboxes().get(i)));
            }
        }

        /* Means the user did not check any checkbox
         * */
        if (listOfCheckedTypes.size() == 0) {
            return true;
        }

        /* The user checked at least one checkbox
         * */
        for (int i = 0; i < getListOfLocalityCheckboxes().size(); i++) {
            if (realEstate.getAddress().getLocality().equalsIgnoreCase(listOfCheckedTypes.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check if a listing passes the city filter
     */
    private boolean cityFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "cityFilterPassed: called!");

        List<String> listOfCheckedTypes = new ArrayList<>();

        /* A list is filled with all the texts of the related checked checkboxes
         * */
        for (int i = 0; i < getListOfCityCheckboxes().size(); i++) {
            if (getListOfCityCheckboxes().get(i).isChecked()) {
                listOfCheckedTypes.add(Utils.getStringFromTextView(getListOfCityCheckboxes().get(i)));
            }
        }

        /* Means the user did not check any checkbox
         * */
        if (listOfCheckedTypes.size() == 0) {
            return true;
        }

        /* The user checked at least one checkbox
         * */
        for (int i = 0; i < getListOfCityCheckboxes().size(); i++) {
            if (realEstate.getAddress().getCity().equalsIgnoreCase(listOfCheckedTypes.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to check if a listing passes the amount of photos filter
     */
    private boolean amountOfPhotosFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "amountOfPhotosFilterPassed: called!");

        if (seekBarAmountOfPhotos.getSelectedMinValue().floatValue() == 0
                && seekBarAmountOfPhotos.getSelectedMaxValue().floatValue() == maxAmountOfPhotos) {

            /* In this case, the seekBar has not been used and the filter is automatically passed
             * */
            return true;

        } else {

            /* The user selected a total rooms filter.
             * Check if the real estate passes it.
             * */
            if (realEstate.getListOfImagesIds().size() >= seekBarAmountOfPhotos.getSelectedMinValue().floatValue()
                    && realEstate.getListOfImagesIds().size() <= seekBarAmountOfPhotos.getSelectedMaxValue().floatValue()) {

                /* The filter has been passed
                 * */
                return true;

            } else {

                /* The filter has not been passed
                 * */
                return false;
            }
        }
    }

    /**
     * Method to check if a listing passes the on sale filter
     */
    private boolean onSaleFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "onSaleFilterPassed: called!");

        if ((!checkBoxOnSale.isChecked() && !checkBoxSold.isChecked())
                || (checkBoxOnSale.isChecked() && checkBoxSold.isChecked())) {
            return true;
        }

        if (checkBoxOnSale.isChecked() && realEstate.getDateSale() == null) {
            return true;
        }

        if (checkBoxSold.isChecked() && realEstate.getDateSale() != null) {
            return true;
        }
        return false;
    }

    /**
     * Method to check if a listing passes the Points Of Interest filter
     */
    private boolean pointsOfInterestFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "pointsOfInterestFilterPassed: called!");

        List<String> listOfCheckedPointsOfInterest = new ArrayList<>();

        for (int i = 0; i < getListOfPointOfInterestCheckboxes().size(); i++) {
            if (getListOfPointOfInterestCheckboxes().get(i).isChecked()) {
                listOfCheckedPointsOfInterest.add(Utils.getStringFromTextView(getListOfPointOfInterestCheckboxes().get(i)));
            }
        }

        /* Means the user did not check any checkbox
         * */
        if (listOfCheckedPointsOfInterest.size() == 0) {
            return true;
        }

        if (realEstate.getListOfNearbyPointsOfInterestIds() != null) {

            /* We get all the nearby point of interests' types of a real estate
             * */
            List<PlaceRealEstate> listOfPointsOfInterestRelatedToTheRealEstate = new ArrayList<>();

            for (int i = 0; i < realEstate.getListOfNearbyPointsOfInterestIds().size(); i++) {
                for (int j = 0; j < getListOfPlaceRealEstate().size(); j++) {
                    if (realEstate.getListOfNearbyPointsOfInterestIds().get(i).equals(getListOfPlaceRealEstate().get(j).getId())) {
                        listOfPointsOfInterestRelatedToTheRealEstate.add(getListOfPlaceRealEstate().get(j));
                    }
                }
            }

            /* Now we extract all the points of interest related to the real estate
             * */
            Set<String> setOfPointsOfInterest = new HashSet<>();
            for (int i = 0; i < listOfPointsOfInterestRelatedToTheRealEstate.size(); i++) {
                for (int j = 0; j < listOfPointsOfInterestRelatedToTheRealEstate.get(i).getTypesList().size(); j++) {
                    setOfPointsOfInterest.add(Utils.capitalize(Utils.replaceUnderscore(listOfPointsOfInterestRelatedToTheRealEstate.get(i).getTypesList().get(j))));
                }
            }

            // TODO: 09/10/2018 This could be modified
            // TODO: CURRENTLY: if at least one point if interest is found, the real estate will be shown
            // TODO: POSSIBLE: only show the real estate if ALL the criteria are matched
            /* Finally, we do the checks. The real estate will pass the filter if at least one type
             * of point of interest can be found in the list of points of interest related to the
             * real estate
             * */
            for (int i = 0; i < listOfPointsOfInterestRelatedToTheRealEstate.size(); i++) {
                for (int j = 0; j < listOfCheckedPointsOfInterest.size(); j++) {
                    if (setOfPointsOfInterest.contains(listOfCheckedPointsOfInterest.get(j))) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    //FILTERS

    /**
     * Method that returns true if all filters has been passed. If that is the case, the listing
     * will be added to a list and displayed in the list of listings found bu the Search Engine
     */
    private boolean allFiltersPassed(RealEstate realEstate) {
        Log.d(TAG, "allFiltersPassed: called!");

        /* Could be simplified but we leave it like this for readability
         * */

        if (!typeFilterPassed(realEstate)) {
            return false;
        }

        if (!priceFilterPassed(realEstate)) {
            return false;
        }

        if (!surfaceAreaFilterPassed(realEstate)) {
            return false;
        }

        if (!numberOfRoomsFilterPassed(realEstate)) {
            return false;
        }

        if (!cityFilterPassed(realEstate)) {
            return false;
        }

        if (!localityFilterPassed(realEstate)) {
            return false;
        }

        if (!amountOfPhotosFilterPassed(realEstate)) {
            return false;
        }

        if (!onSaleFilterPassed(realEstate)) {
            return false;
        }

        if (!pointsOfInterestFilterPassed(realEstate)) {
            return false;
        }

        return true;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that uses RxJava to update the list of found articles. When the counter reaches 0,
     * the SearchEngineActivity is launched
     */
    @SuppressLint("CheckResult")
    private void updateRealEstatesWithFoundInfo(final List<RealEstate> list) {
        Log.d(TAG, "updateRealEstatesWithFoundInfo: called!");
        if (list != null) {

            /* Variable that allows us to know when the updating process has finished so
             * we can load the activity.
             * */
            updateCounter = 0;

            for (int i = 0; i < list.size(); i++) {
                getRepository().updateRealEstate(list.get(i))
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

                                /* The counter allows us to know when the process
                                 * has finished and we can launch next activity
                                 * */
                                updateCounter++;
                                if (list.size() == updateCounter) {
                                    launchActivityWithIntent();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: called!");
                                notifyUserErrorDuringProcess();
                            }
                        });
            }
        }
    }

    /**
     * Method to launch the specific activity with a concrete intent
     */
    private void launchActivityWithIntent() {
        Log.d(TAG, "launchActivityWithIntent: called!");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.INTENT_FROM_SEARCH_ENGINE, Constants.STRING_FROM_SEARCH_ENGINE);
        startActivity(intent);
    }

    private void notifyUserErrorDuringProcess() {
        Log.d(TAG, "notifyUserErrorDuringProcess: called!");
        ToastHelper.toastShort(this, "There was an error during the process. Please, try again.");
        Utils.showMainContent(progressBarContent, mainLayout);
    }

}