package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

// TODO: 05/09/2018 Keep the searching information stored with SharedPreferences
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

    private List<CheckBox> listOfBuildingTypeCheckboxes;
    private List<CheckBox> listOfCityCheckboxes;
    private List<CheckBox> listOfLocalityCheckboxes;
    private List<CheckBox> listOfPointOfInterestCheckboxes;

    private TextView tvPrice;
    private CrystalRangeSeekbar seekBarPrice;
    private boolean seekBarPriceUsed;

    private TextView tvSurfaceArea;
    private CrystalRangeSeekbar seekBarSurfaceArea;
    private boolean seekBarSurfaceAreaUsed;

    private TextView tvNumberOfRooms;
    private CrystalRangeSeekbar seekBarNumberOfRooms;
    private boolean seekBarNumberOfRoomsUsed;

    private TextView tvAmountOfPhotos;
    private CrystalRangeSeekbar seekBarAmountOfPhotos;
    private boolean seekbarAmountOfPhotosUsed;

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

    private int updateCounter;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.currency = Utils.readCurrentCurrencyShPref(this);

        this.seekBarPriceUsed = false;
        this.seekBarSurfaceAreaUsed = false;
        this.seekBarNumberOfRoomsUsed = false;
        this.seekbarAmountOfPhotosUsed = false;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_search_engine);
        unbinder = ButterKnife.bind(this);

        this.configureLayout();

        Utils.showMainContent(progressBarContent, mainLayout);

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
                updateCurrency();
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
            }
            break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateCurrency() {
        Log.d(TAG, "updateCurrency: called!");

        if (this.currency == 0) {
            this.currency = 1;
        } else {
            this.currency = 0;
        }
        Utils.writeCurrentCurrencyShPref(this, currency);
    }

    private void updatePriceTextView() {
        Log.d(TAG, "updatePriceTextView: called!");
        tvPrice.setText("Price (thousands, " + Utils.getCurrencySymbol(currency) + ")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<RealEstate> getListOfRealEstate() {
        Log.d(TAG, "getListOfRealEstate: called!");

        if (listOfRealEstate == null) {
            return listOfRealEstate = new ArrayList<>();
        }
        return listOfRealEstate;
    }

    private List<PlaceRealEstate> getListOfPlaceRealEstate() {
        Log.d(TAG, "getListOfPlaceRealEstate: called!");

        if (listOfPlaceRealEstate == null) {
            return listOfPlaceRealEstate = new ArrayList<>();
        }
        return listOfPlaceRealEstate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.configureToolBar();

        this.getViewGroups();
        this.getTextViews();
        this.getSeekBars();
        this.setAllTexts();
        this.setCrystalSeekBarListeners();

        this.setMinMaxValuesRangeSeekBars(getListOfRealEstate());
        this.prepareSets();

    }

    private void getViewGroups() {
        Log.d(TAG, "getViewGroups: called!");
        this.linearLayoutTypes = cardViewType.findViewById(R.id.linear_layout_checkboxes_id);
        this.linearLayoutCities = cardViewCities.findViewById(R.id.linear_layout_checkboxes_id);
        this.linearLayoutLocalities = cardViewLocalities.findViewById(R.id.linear_layout_checkboxes_id);
        this.linearLayoutTypesPointsOfInterestNearby = cardViewPointsOfInterest.findViewById(R.id.linear_layout_checkboxes_id);
    }

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

    private void getSeekBars() {
        Log.d(TAG, "getSeekBars: called!");
        this.seekBarPrice = cardViewPrice.findViewById(R.id.single_seek_bar_id);
        this.seekBarSurfaceArea = cardViewSurfaceArea.findViewById(R.id.single_seek_bar_id);
        this.seekBarNumberOfRooms = cardViewNumberOfRooms.findViewById(R.id.single_seek_bar_id);
        this.seekBarAmountOfPhotos = cardViewAmountPhotos.findViewById(R.id.single_seek_bar_id);

    }

    private void setAllTexts() {
        Log.d(TAG, "setAllTexts: called!");
        tvPrice.setText("Price (thousands, $)");
        tvSurfaceArea.setText("Surface area (sqm)");
        tvNumberOfRooms.setText("Number of Rooms");
        tvAmountOfPhotos.setText("Amount of Photos");

        tvTypes.setText("Types of Building");
        tvCities.setText("Cities");
        tvLocalities.setText("Localities");
        tvPointsOfInterest.setText("Types of Points of Interest");
    }

    private void setMinMaxValuesRangeSeekBars(List<RealEstate> listOfRealEstates) {
        Log.d(TAG, "setMinMaxValuesRangeSeekBars: called!");
        float maxPrice = 0;
        float maxSurfaceArea = 0;
        int maxNumberOfRooms = 0;
        int maxAmountOfPhotos = 0;

        for (int i = 0; i < listOfRealEstates.size(); i++) {
            if (maxPrice < listOfRealEstates.get(i).getPrice()) {
                maxPrice = (int) listOfRealEstates.get(i).getPrice();
            }
            if (maxSurfaceArea < listOfRealEstates.get(i).getSurfaceArea()) {
                maxSurfaceArea = listOfRealEstates.get(i).getSurfaceArea();
            }
            if (maxNumberOfRooms < listOfRealEstates.get(i).getRooms().getTotalNumberOfRooms()) {
                maxNumberOfRooms = listOfRealEstates.get(i).getRooms().getTotalNumberOfRooms();
            }
            if (maxAmountOfPhotos < listOfRealEstates.get(i).getListOfImagesIds().size()) {
                maxAmountOfPhotos = listOfRealEstates.get(i).getListOfImagesIds().size();
            }
        }

        Log.d(TAG, "setMinMaxValuesRangeSeekBars: maxAmountOfPhotos = " + maxAmountOfPhotos);

        setMinMaxValues(seekBarPrice, 0, maxPrice);
        setMinMaxValues(seekBarSurfaceArea, 50, maxSurfaceArea);
        setMinMaxValues(seekBarNumberOfRooms, 1, maxNumberOfRooms);
        setMinMaxValues(seekBarAmountOfPhotos, 1, maxAmountOfPhotos);

        if (maxNumberOfRooms == 1) {
            seekBarNumberOfRooms.setEnabled(false);
            tvNumberOfRooms.setText("Number of Rooms (Disabled)");
        }

        if (maxAmountOfPhotos == 1) {
            seekBarAmountOfPhotos.setEnabled(false);
            tvAmountOfPhotos.setText("Amount of Photos (Disabled)");
        }

    }

    private void setMinMaxValues(CrystalRangeSeekbar seekBar, float min, float max) {
        Log.d(TAG, "setMinMaxValues: called!");
        seekBar.setMinValue(min);
        seekBar.setMaxValue(max);
    }

    private void setCrystalSeekBarListeners() {
        Log.d(TAG, "setCrystalSeekBarListeners: called!");
        setListeners(seekBarPrice);
        setListeners(seekBarSurfaceArea);
        setListeners(seekBarNumberOfRooms);
        setListeners(seekBarAmountOfPhotos);
    }

    private void setListeners(final CrystalRangeSeekbar seekBar) {
        Log.d(TAG, "setListeners: called!");

        seekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                Log.d(TAG, "valueChanged: min = " + minValue + " - " + "maxValue = " + maxValue);
                setTextDependingOnSeekBar(seekBar, minValue, maxValue);
                setUsedStateAccordingToSeekBar(seekBar);
            }
        });

        seekBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d(TAG, "finalValue: called1");
                setTextDependingOnSeekBar(seekBar, minValue, maxValue);
                setUsedStateAccordingToSeekBar(seekBar);
            }
        });
    }

    private void setTextDependingOnSeekBar(CrystalRangeSeekbar seekBar, Number min, Number max) {
        Log.d(TAG, "setTextDependingOnTextView: called!");
        if (seekBar == seekBarPrice) {
            tvPrice.setText("Price (" + Utils.getCurrencySymbol(currency) + ") - [" + min + ", " + max + "]");
            this.seekBarPriceUsed = true;
        } else if (seekBar == seekBarSurfaceArea) {
            tvSurfaceArea.setText("Surface Area (sqm) - [" + min + ", " + max + "]");
            this.seekBarSurfaceAreaUsed = true;
        } else if (seekBar == seekBarNumberOfRooms) {
            tvNumberOfRooms.setText("Number of Rooms - [" + min + ", " + max + "]");
            this.seekBarNumberOfRoomsUsed = true;
        } else if (seekBar == seekBarAmountOfPhotos) {
            tvAmountOfPhotos.setText("Amount of Photos - [" + min + ", " + max + "]");
            this.seekbarAmountOfPhotosUsed = true;
        }
    }

    private void setUsedStateAccordingToSeekBar(CrystalRangeSeekbar seekBar) {
        Log.d(TAG, "setUsedStateAccordingToSeekBar: called!");
        if (seekBar == seekBarPrice) {
            this.seekBarPriceUsed = true;
        } else if (seekBar == seekBarSurfaceArea) {
            this.seekBarSurfaceAreaUsed = true;
        } else if (seekBar == seekBarNumberOfRooms) {
            this.seekBarNumberOfRoomsUsed = true;
        } else if (seekBar == seekBarAmountOfPhotos) {
            this.seekbarAmountOfPhotosUsed = true;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void prepareSets() {
        Log.d(TAG, "prepareSets: called!");
        setOfBuildingTypes = null;
        setOfLocalities = null;
        setOfCities = null;
        setOfTypesOfPointsOfInterest = null;
        fillSets();
    }

    private Set<String> getSetOfBuildingTypes() {
        Log.d(TAG, "getSetOfBuildingTypes: called!");
        if (setOfBuildingTypes == null) {
            return setOfBuildingTypes = new HashSet<>();
        }
        return setOfBuildingTypes;

    }

    private Set<String> getSetOfLocalities() {
        Log.d(TAG, "getSetOfLocalities: called!");
        if (setOfLocalities == null) {
            return setOfLocalities = new HashSet<>();
        }
        return setOfLocalities;
    }

    private Set<String> getSetOfCities() {
        Log.d(TAG, "getSetOfCities: called!");
        if (setOfCities == null) {
            return setOfCities = new HashSet<>();
        }
        return setOfCities;
    }

    private Set<String> getSetOfTypesOfPointsOfInterest() {
        Log.d(TAG, "getSetOfTypesOfPointsOfInterest: called!");
        if (setOfTypesOfPointsOfInterest == null) {
            return setOfTypesOfPointsOfInterest = new HashSet<>();
        }
        return setOfTypesOfPointsOfInterest;
    }

    @SuppressLint("CheckResult")
    private void fillSets() {
        Log.d(TAG, "fillSets: called!");
        getRepository().getAllListingsRealEstateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new io.reactivex.Observer<List<RealEstate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");

                    }

                    @Override
                    public void onNext(List<RealEstate> list) {
                        Log.d(TAG, "onNext: called!");
                        listOfRealEstate = list;
                        fillSetOfBuildingTypes(list);
                        fillSetOfLocalities(list);
                        fillSetOfCities(list);
                        fillSetOfTypesOfPointsOfInterest();
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

    private void fillSetOfBuildingTypes(List<RealEstate> list) {
        Log.d(TAG, "fillSetOfBuildingTypes: called!");
        Log.w(TAG, "fillSetOfBuildingTypes: " + list);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                getSetOfBuildingTypes().add(list.get(i).getType());
            }
        }
    }

    private void fillSetOfLocalities(List<RealEstate> list) {
        Log.d(TAG, "fillSetOfLocalities: called!");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                getSetOfLocalities().add(list.get(i).getAddress().getLocality());
            }
        }
    }

    private void fillSetOfCities(List<RealEstate> list) {
        Log.d(TAG, "fillSetOfCities: called!");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                getSetOfCities().add(list.get(i).getAddress().getCity());
            }
        }
    }

    @SuppressLint("CheckResult")
    private void fillSetOfTypesOfPointsOfInterest() {
        Log.d(TAG, "fillSetOfTypesOfPointsOfInterest: called!");
        getRepository().getAllPlacesRealEstateObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new io.reactivex.Observer<List<PlaceRealEstate>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onNext(List<PlaceRealEstate> list) {
                        listOfPlaceRealEstate = list;
                        Log.d(TAG, "onNext: called!");
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                getSetOfTypesOfPointsOfInterest().addAll(list.get(i).getTypesList());
                            }
                        }
                        addCheckboxesToLayout();
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

    private void addCheckboxesToLayout() {
        Log.d(TAG, "addCheckboxesToLayout: called!");

        Log.w(TAG, "addCheckboxesToLayout: " + getSetOfTypesOfPointsOfInterest());

        for (String buildingType : getSetOfBuildingTypes()) {
            fillWithCheckboxes(
                    linearLayoutTypes,
                    buildingType,
                    getListOfBuildingTypeCheckboxes());
        }

        for (String city : getSetOfCities()) {
            fillWithCheckboxes(
                    linearLayoutCities,
                    city,
                    getListOfCityCheckboxes());
        }

        for (String locality : getSetOfLocalities()) {
            fillWithCheckboxes(
                    linearLayoutLocalities,
                    locality,
                    getListOfLocalityCheckboxes());
        }

        for (String typeOfPointOfInterest : getSetOfTypesOfPointsOfInterest()) {
            fillWithCheckboxes(
                    linearLayoutTypesPointsOfInterestNearby,
                    typeOfPointOfInterest,
                    getListOfPointOfInterestCheckboxes());
        }
    }

    private void fillWithCheckboxes(LinearLayout linearLayout, String type, List<CheckBox> listOfCheckboxes) {
        Log.d(TAG, "addPointsOfInterestCheckboxesToLayout: called!");

        CheckBox checkBox = new CheckBox(this);
        linearLayout.addView(checkBox);

        // TODO: 26/08/2018 LayoutParams does not work!

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMarginStart(8);
        layoutParams.setMargins(8, 8, 0, 0);

        checkBox.setLayoutParams(layoutParams);
        checkBox.setText(Utils.capitalize(Utils.replaceUnderscore(type)));
        checkBox.setTag(type);

        listOfCheckboxes.add(checkBox);
    }

    public List<CheckBox> getListOfBuildingTypeCheckboxes() {
        Log.d(TAG, "getListOfBuildingTypeCheckboxes: called!");
        if (listOfBuildingTypeCheckboxes == null) {
            return listOfBuildingTypeCheckboxes = new ArrayList<>();
        }
        return listOfBuildingTypeCheckboxes;
    }

    public List<CheckBox> getListOfCityCheckboxes() {
        Log.d(TAG, "getListOfCityCheckboxes: called!");
        if (listOfCityCheckboxes == null) {
            return listOfCityCheckboxes = new ArrayList<>();
        }
        return listOfCityCheckboxes;
    }

    public List<CheckBox> getListOfLocalityCheckboxes() {
        Log.d(TAG, "getListOfBuildingTypeCheckboxes: called!");
        if (listOfLocalityCheckboxes == null) {
            return listOfLocalityCheckboxes = new ArrayList<>();
        }
        return listOfLocalityCheckboxes;
    }

    public List<CheckBox> getListOfPointOfInterestCheckboxes() {
        Log.d(TAG, "getListOfPointOfInterestCheckboxes: called!");
        if (listOfPointOfInterestCheckboxes == null) {
            return listOfPointOfInterestCheckboxes = new ArrayList<>();
        }
        return listOfPointOfInterestCheckboxes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String getTextFromView(TextView textView) {
        Log.d(TAG, "getTextFromView: called!");
        return Utils.capitalize(Utils.getStringFromTextView(textView));
    }

    private int getMaxValueFromSeekBar(CrystalRangeSeekbar rangeSeekBar) {
        Log.d(TAG, "getMaxValueFromSeekBar: called!");
        return (int) rangeSeekBar.getSelectedMaxValue();
    }

    private int getMinValueFromSeekBar(CrystalRangeSeekbar rangeSeekBar) {
        Log.d(TAG, "getMinValueFromSeekBar: called!");
        return (int) rangeSeekBar.getSelectedMinValue();
    }

    private boolean seekBarNotUsed(CrystalRangeSeekbar seekBar) {
        Log.d(TAG, "seekBarNotUsed: called!");

        if (seekBar == seekBarPrice) {
            return seekBarPriceUsed;
        } else if (seekBar == seekBarSurfaceArea) {
            return seekBarSurfaceAreaUsed;
        } else if (seekBar == seekBarNumberOfRooms) {
            return seekBarNumberOfRoomsUsed;
        } else if (seekBar == seekBarAmountOfPhotos) {
            return seekbarAmountOfPhotosUsed;
        } else {
            return true;
        }
    }

    private boolean textViewNotUsed(TextView textView) {
        Log.d(TAG, "textViewNotUsed: called!");
        return getTextFromView(textView).equals("");
    }

    private boolean maxMinValuesFilterPassed(CrystalRangeSeekbar rangeSeekBar, float value) {
        Log.d(TAG, "maxMinValuesFilterPassed: called!");

        if (value > (float) rangeSeekBar.getSelectedMinValue()
                && value < (float) rangeSeekBar.getSelectedMaxValue()) {
            return true;
        }
        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initSearch() {
        Log.d(TAG, "initSearch: called!");

        // TODO: 05/09/2018 Show Progress Bar!

        // TODO: 17/09/2018 This is wrong. The ideal process would be to update all the real estates
        // TODO: 17/09/2018 in the database whether they passed the filter or not. This way, the
        // TODO: search information will be stored and can be accessed at any moment without a search

        /* We create a new list
        * to store the found real estates
        * */
        List<RealEstate> listOfFoundRealEstates = new ArrayList<>();

        for (int i = 0; i < listOfRealEstate.size(); i++) {

            /* If the real estate does not pass the filter, we set it to false
            * */
            if (!allFiltersPassed(listOfRealEstate.get(i))) {
                listOfRealEstate.get(i).setFound(false);
                continue;
            }

            /* If the real estate passes the filter, we set the found field to true and add
            * it to the list
            * */
            listOfRealEstate.get(i).setFound(true);
            listOfFoundRealEstates.add(listOfRealEstate.get(i));
        }

        if (listOfFoundRealEstates.size() > 0) {
            updateRealEstatesWithFoundInfo(listOfFoundRealEstates);

        } else {
            ToastHelper.toastLong(this, "No results were found");
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //FILTERS

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
        for (int i = 0; i < getListOfBuildingTypeCheckboxes().size(); i++) {
            if (realEstate.getType().equalsIgnoreCase(listOfCheckedTypes.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean priceFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "priceFilterPassed: called!");

        /* We firstly check that the seekBar was used
         * */
        if (seekBarNotUsed(seekBarPrice)) {
            return true;
        }

        /* We take care that the price is in dollars. Besides we multiply it by 1000
         * because the range bar show prices divided by thousands
         * */
        int maxPrice = getMaxValueFromSeekBar(seekBarPrice) * 1000;
        int minPrice = getMinValueFromSeekBar(seekBarPrice) * 1000;

        if (currency == 1) {
            maxPrice = (int) Utils.convertEuroToDollar(maxPrice);
            minPrice = (int) Utils.convertEuroToDollar(minPrice);
        }

        if (realEstate.getPrice() > minPrice && realEstate.getPrice() < maxPrice) {
            return true;
        }
        return false;
    }

    private boolean surfaceAreaFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "surfaceAreaFilterPassed: called!");

        /* We firstly check that values were entered
         * */
        if (seekBarNotUsed(seekBarSurfaceArea)) {
            return true;
        }

        if (maxMinValuesFilterPassed(seekBarSurfaceArea, realEstate.getSurfaceArea())) {
            return true;
        }
        return false;
    }

    private boolean numberOfRoomsFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "numberOfRoomsFilterPassed: called!");

        /* We firstly check that values were entered
         * */
        if (seekBarNotUsed(seekBarNumberOfRooms)) {
            return true;
        }

        if (maxMinValuesFilterPassed(seekBarNumberOfRooms, realEstate.getRooms().getTotalNumberOfRooms())) {
            return true;
        }
        return false;
    }

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

    private boolean amountOfPhotosFilterPassed(RealEstate realEstate) {
        Log.d(TAG, "amountOfPhotosFilterPassed: called!");

        /* We firstly check that values were entered
         * */
        if (seekBarNotUsed(seekBarAmountOfPhotos)) {
            return true;
        }

        if (maxMinValuesFilterPassed(seekBarAmountOfPhotos, realEstate.getListOfImagesIds().size())) {
            return true;
        }
        return false;
    }

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
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("CheckResult")
    private void updateRealEstatesWithFoundInfo (final List<RealEstate> list) {
        Log.d(TAG, "updateRealEstatesWithFoundInfo: called!");
        if (list != null) {

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

                            }
                        });
            }
        }
    }

    private void launchActivityWithIntent () {
        Log.d(TAG, "launchActivityWithIntent: called!");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.INTENT_FROM_SEARCH_ENGINE, Constants.STRING_FROM_SEARCH_ENGINE);
        startActivity(intent);
    }
}