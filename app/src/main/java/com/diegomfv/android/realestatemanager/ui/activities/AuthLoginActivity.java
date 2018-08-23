package com.diegomfv.android.realestatemanager.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amitshekhar.DebugDB;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.AppDatabase;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;
import com.diegomfv.android.realestatemanager.utils.Utils;
import com.snatik.storage.Storage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

// TODO: 21/08/2018 Clean caches!
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

    ////////////////////////////////////////////////////////////////////////////////////////////////


}
