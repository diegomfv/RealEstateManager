package com.diegomfv.android.realestatemanager.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amitshekhar.DebugDB;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

// TODO: 21/08/2018 Clean caches!
// TODO: 30/08/2018 Remember to put a flag so we cannot come her again after signed in
// TODO: 02/09/2018 Allow to continue only if InternalStorageAccess is granted!
public class AuthLoginActivity extends BaseActivity {

    private static final String TAG = AuthLoginActivity.class.getSimpleName();

    //////////////////////////////////////////////////////

    @BindView(R.id.logo_image_id)
    ImageView imageView;

    @BindView(R.id.card_view_email_id)
    CardView cvEmail;

    @BindView(R.id.card_view_password_id)
    CardView cvPassword;

    @BindView(R.id.button_sign_up_password_id)
    Button buttonSignIn;

    @BindView(R.id.forgot_password_id)
    TextView tvForgetPassword;

    @BindView(R.id.button_sign_in_password_id)
    Button buttonSignUp;

    TextInputAutoCompleteTextView tvEmail;

    TextInputAutoCompleteTextView tvPassword;

    //////////////////////////////////////////////////////

    private ActionBar actionBar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: 24/08/2018 Might be nice to une a coordinator layout
        // TODO: 24/08/2018 Put a photo of a nice flat

        //////////////////////////////////////////////////////
        setContentView(R.layout.activity_auth_choose_login);
        unbinder = ButterKnife.bind(this);

        this.getAllAcTextViews();

        this.configureLayout();

        /* We check if we have permissions
         * */
        Utils.checkAllPermissions(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.launchActivity(AuthLoginActivity.this, AddPhotoActivityTrial.class);
            }
        });

    }

    @OnClick({R.id.button_sign_up_password_id, R.id.button_sign_in_password_id})
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: " + ((Button) view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_sign_in_password_id: {
                if (allChecksPassed()) {
                    Utils.launchActivity(this, MainActivity.class);
                }
            }
            break;

            case R.id.button_sign_up_password_id: {
                Utils.launchActivity(this, SignUpActivity.class);
            }
            break;

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

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");
        setHints();
        setPasswordInputType();
        setEmailAndPasswordIfInSharedPref();
        setListeners();
    }

    private void setPasswordInputType() {
        Log.d(TAG, "setPasswordInputType: called!");
        tvPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void setHints () {
        Log.d(TAG, "getAllTextInputLayouts: called!");
        Utils.getTextInputLayoutFromCardview(cvEmail).setHint("Email");
        Utils.getTextInputLayoutFromCardview(cvPassword).setHint("Password");
    }

    private void getAllAcTextViews() {
        Log.d(TAG, "getAllTextviews: called!");
        tvEmail = Utils.getTextInputAutoCompleteTextViewFromCardView(cvEmail);
        tvPassword = Utils.getTextInputAutoCompleteTextViewFromCardView(cvPassword);
    }

    private void setEmailAndPasswordIfInSharedPref() {
        Log.d(TAG, "setEmailIfInSharedPref: called!");
        String[] info = Utils.readCurrentAgentData(this);
        tvEmail.setText(info[2]);
        tvPassword.setText(info[3]);
    }

    private void setListeners () {
        Log.d(TAG, "setListeners: called!");
        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");
                Utils.launchActivity(AuthLoginActivity.this, ForgotPasswordActivity.class);
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean allChecksPassed() {
        Log.d(TAG, "allChecksPassed: called!");

        String[] info = Utils.readCurrentAgentData(this);

        if (Utils.getStringFromTextView(tvEmail).equals(info[2])
                && Utils.getStringFromTextView(tvPassword).equals(info[3])) {
            return true;

        } else {
            ToastHelper.toastShort(this, "Sorry, some info is incorrect");
            return false;
        }
    }
}
