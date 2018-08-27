package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.rest.fragments.handset.FragmentHandsetItemDescription;

/**
 * Created by Diego Fajardo on 15/08/2018.
 */
public class DetailActivity extends BaseActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

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

        loadFragment(bundle);

    }

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
