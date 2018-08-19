package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amitshekhar.DebugDB;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;
import com.diegomfv.android.realestatemanager.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthLoginActivity extends AppCompatActivity {

    private static final String TAG = AuthLoginActivity.class.getSimpleName();

    //////////////////////////////////////////////////////

    //////////////////////////////////////////////////////

    @BindView(R.id.button_sign_in_password_id)
    Button buttonPassword;

    @BindView(R.id.button_google_id)
    Button buttonGoogle;

    @BindView(R.id.button_facebook_id)
    Button buttonFacebook;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //////////////////////////////////////////////////////
        setContentView(R.layout.activity_auth_choose_login);
        unbinder = ButterKnife.bind(this);

        /* We check if we have permissions
        * */
        Utils.checkAllPermissions(this);

    }

    @OnClick ({R.id.button_sign_up_password_id, R.id.button_sign_in_password_id, R.id.button_google_id, R.id.button_facebook_id})
    public void buttonClicked (View view) {
        Log.d(TAG, "buttonClicked: " + ((Button)view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_sign_up_password_id: {

                ToastHelper.toastButtonClicked(this, view);

            } break;

            case R.id.button_sign_in_password_id: {

                ToastHelper.toastButtonClicked(this, view);

                Utils.launchActivity(this, MainActivity.class);

            } break;

            case R.id.button_google_id: {

                ToastHelper.toastButtonClicked(this, view);
                Utils.launchActivity(this, CreateNewListingActivity.class);

            } break;

            case R.id.button_facebook_id: {

                ToastHelper.toastButtonClicked(this, view);

                // TODO: 16/08/2018 Delete!
                DebugDB.getAddressLog();

            } break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");

        unbinder.unbind();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case Constants.REQUEST_CODE_ALL_PERMISSIONS: {
                /* Do nothing, */

                if (grantResults.length > 0) {

                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == -1) {
                            //-1 means access NOT GRANTED
                            ToastHelper.toastSomeAccessNotGranted(this);
                            return;
                        }
                    }
                }
            }
        }
    }

}
