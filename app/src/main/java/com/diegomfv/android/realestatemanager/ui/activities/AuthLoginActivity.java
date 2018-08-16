package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amitshekhar.DebugDB;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthLoginActivity extends AppCompatActivity {

    private static final String TAG = AuthLoginActivity.class.getSimpleName();

    //////////////////////////////////////////////////////

    private Context context;

    //////////////////////////////////////////////////////

    @BindView(R.id.button_password_id)
    Button buttonPassword;

    @BindView(R.id.button_google_id)
    Button buttonGoogle;

    @BindView(R.id.button_facebook_id)
    Button buttonFacebook;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = AuthLoginActivity.this;

        //////////////////////////////////////////////////////
        setContentView(R.layout.activity_auth_choose_login);
        unbinder = ButterKnife.bind(this);



    }


    @OnClick ({R.id.button_password_id, R.id.button_google_id, R.id.button_facebook_id})
    public void buttonClicked (View view) {
        Log.d(TAG, "buttonClicked: " + ((Button)view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_password_id: {

                ToastHelper.toastButtonClicked(context, view);

                launchActivity(MainActivity.class);

            } break;

            case R.id.button_google_id: {

                ToastHelper.toastButtonClicked(context, view);

            } break;

            case R.id.button_facebook_id: {

                ToastHelper.toastButtonClicked(context, view);

                // TODO: 16/08/2018 Delete!
                DebugDB.getAddressLog();

            } break;

        }
    }

    private void launchActivity(Class <? extends AppCompatActivity> activity) {
        Log.d(TAG, "launchActivity: called!");

        Intent intent = new Intent(this, activity);
        startActivity (intent);

    }


}
