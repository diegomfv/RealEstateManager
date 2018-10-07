package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.fragments.FragmentItemDescription;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * Created by Diego Fajardo on 15/08/2018.
 */
public class DetailActivity extends BaseActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.toolbar_id)
    Toolbar toolbar;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Bundle bundle;

    private int currency;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: called!");

        this.getInfoFromIntent();

        this.currency = Utils.readCurrentCurrencyShPref(this);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_detail);
        unbinder = ButterKnife.bind(this);

        this.configureToolbar();

        loadFragment(bundle);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: called!");
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
            }
            break;

        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the toolbar.
     */
    private void configureToolbar() {
        Log.d(TAG, "configureToolbar: called!");

        setSupportActionBar(toolbar);
        setTitle("Create a New Listing");
        setOverflowButtonColor(toolbar, Color.WHITE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");
                onBackPressed();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that modifies the currency variable and writes the new info to sharedPreferences.
     * It also loads the fragment again
     */
    private void changeCurrency() {
        Log.d(TAG, "changeCurrency: called!");

        if (this.currency == 0) {
            this.currency = 1;
        } else {
            this.currency = 0;
        }
        Utils.writeCurrentCurrencyShPref(this, currency);
        loadFragment(bundle);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to get the information from the intent. It will be passed to the fragment later
     */
    private void getInfoFromIntent() {
        Log.d(TAG, "getInfoFromIntent: called!");

        Intent intent = getIntent();
        bundle = new Bundle();

        if (intent.getExtras() != null) {
            bundle.putParcelable(Constants.GET_PARCELABLE, intent.getExtras().getParcelable(Constants.SEND_PARCELABLE));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to load the fragment (if the bundle has been filled, the information will be passed
     * to the fragment using the args). It replaces the fragment.
     */
    private void loadFragment(Bundle bundle) {
        Log.d(TAG, "loadFragmentOrFragments: called!");

        FragmentItemDescription fragmentItemDescription = FragmentItemDescription.newInstance();
        fragmentItemDescription.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment2_container_id, fragmentItemDescription)
                .commit();
    }
}
