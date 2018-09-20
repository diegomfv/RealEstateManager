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
import com.diegomfv.android.realestatemanager.util.Utils;

/**
 * Created by Diego Fajardo on 05/09/2018.
 */
public class SearchDetailActivity extends BaseActivity {

    private static final String TAG = SearchDetailActivity.class.getSimpleName();

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

        //loadFragment(bundle);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case android.R.id.home: {
                Utils.launchActivity(this, SearchResultsActivity.class);

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

//    private void loadFragment(Bundle bundle) {
//        Log.d(TAG, "loadFragmentOrFragments: called!");
//
//        FragmentHandsetItemDescriptionSearch fragmentItemDescription = FragmentHandsetItemDescriptionSearch.newInstance();
//        fragmentItemDescription.setArguments(bundle);
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.fragment2_container_id, fragmentItemDescription)
//                .commit();
//
//    }

}
