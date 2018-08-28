package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.rest.fragments.handset.FragmentHandsetItemDescription;
import com.diegomfv.android.realestatemanager.utils.Utils;

/**
 * Created by Diego Fajardo on 15/08/2018.
 */
// TODO: 28/08/2018 Add possibility to change the currency
public class DetailActivity extends BaseActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBar actionBar;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        Intent intent = getIntent();
        Bundle bundle = new Bundle();

        if (intent.getExtras() != null) {
            bundle.putParcelable(Constants.GET_PARCELABLE, intent.getExtras().getParcelable(Constants.SEND_PARCELABLE));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_detail);

        this.configureActionBar();

        loadFragment(bundle);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case android.R.id.home: {
                Utils.launchActivity(this, MainActivity.class);

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void loadFragment(Bundle bundle) {
        Log.d(TAG, "loadFragmentOrFragments: called!");

        FragmentHandsetItemDescription fragmentItemDescription = FragmentHandsetItemDescription.newInstance();
        fragmentItemDescription.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment2_container_id, fragmentItemDescription)
                .commit();

    }


}
