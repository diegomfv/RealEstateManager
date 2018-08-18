package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.utils.FirebasePushIdGenerator;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */
public class AddPhotoActivity extends AppCompatActivity {

    private static final String TAG = AddPhotoActivity.class.getSimpleName();

    /////////////////////////////////

    @BindView(R.id.button_go_back_id)
    Button buttonGoBack;

    @BindView(R.id.button_add_photo_id)
    Button buttonAddPhoto;

    /////////////////////////////////

    private Unbinder unbinder;

    private HashMap<String,String> mapOfDescriptions;

    private Intent intent;

    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");
        setContentView(R.layout.activity_add_photo);
        unbinder = ButterKnife.bind(this);

        mapOfDescriptions = new HashMap<>();

        getDescriptionsMap();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @OnClick ({R.id.button_go_back_id, R.id.button_add_photo_id})
    public void buttonclicked (View view) {
        Log.d(TAG, "buttonClicked: " + ((Button)view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_go_back_id: {

                launchActivityWithIntentFilledWithMap();


            } break;

            case R.id.button_add_photo_id: {

                mapOfDescriptions.put(FirebasePushIdGenerator.generate(), FirebasePushIdGenerator.generate());
                launchActivityWithIntentFilledWithMap();


            } break;


        }

    }


    private void getDescriptionsMap() {
        Log.d(TAG, "getDescriptionsMap: called!");

        intent = getIntent();

        if (intent != null && intent.getSerializableExtra(Constants.DESCRIPTIONS_SERIALIZABLE) != null) {
            mapOfDescriptions = (HashMap<String, String>) intent.getSerializableExtra(Constants.DESCRIPTIONS_SERIALIZABLE);

            ToastHelper.toastShort(this, mapOfDescriptions.toString());

        }

    }


    private void launchActivityWithIntentFilledWithMap () {
        Log.d(TAG, "launchActivityWithIntentFilledWithMap: called!");

        Intent intent = new Intent(this, CreateNewListingActivity.class);
        intent.putExtra(Constants.DESCRIPTIONS_SERIALIZABLE, mapOfDescriptions);
        startActivity (intent);

    }

}















