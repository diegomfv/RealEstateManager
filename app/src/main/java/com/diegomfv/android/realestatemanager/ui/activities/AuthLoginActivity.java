package com.diegomfv.android.realestatemanager.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amitshekhar.DebugDB;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.Agent;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * AuthLoginActivity is the entry point of the app.
 * It allows the user to sign in, sign up or recover the password
 */
public class AuthLoginActivity extends BaseActivity {

    private static final String TAG = AuthLoginActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    private TextInputAutoCompleteTextView tvEmail;

    private TextInputAutoCompleteTextView tvPassword;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Agent> listOfAgents;

    private boolean locationPermissionGranted;

    private boolean accessInternalStorageGranted;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_auth_choose_login);
        unbinder = ButterKnife.bind(this);

        this.locationPermissionGranted = false;
        this.accessInternalStorageGranted = false;

        /* We get the list of all agents from the database
         * */
        this.getListOfAgentsFromDatabase();

        /* We get a reference to the views
         * */
        this.getAllAcTextViews();

        /* Layout configuration*/
        this.configureLayout();

        /* We check if we have the necessary permissions for the app to work
         * */
        checkAllPermissions();

        /* In case of configuration change, we load the saved info before rotation
         * */
        loadInfoBeforeRotation(savedInstanceState);
    }

    @OnClick({R.id.button_sign_up_password_id, R.id.button_sign_in_password_id})
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: " + ((Button) view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_sign_in_password_id: {

                /* We check if we have the necessary permissions.
                * If we do not, we ask for them.
                 * */
                if (allNecessaryPermissionsGranted()) {
                    if (allChecksPassed()) {

                        /* If all checks has been passed, it also means the information
                        * of the agent is stored in SharedPreferences
                        * */
                        launchMainActivityWithIntent();
                    }

                } else {
                    notifyAndAskForPermissions();
                }

            }
            break;

            case R.id.button_sign_up_password_id: {
                DebugDB.getAddressLog();

                if (allNecessaryPermissionsGranted()) {
                    Utils.launchActivity(this, SignUpActivity.class);

                } else {
                    notifyAndAskForPermissions();
                }
            }
            break;
        }
    }

    /**
     * We store the views information in the bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: called!");
        outState.putString(Constants.BUNDLE_EMAIL, Utils.getStringFromTextView(tvEmail));
        outState.putString(Constants.BUNDLE_PASSWORD, Utils.getStringFromTextView(tvPassword));
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
        Log.d(TAG, "onRequestPermissionsResult: called!");

        switch (requestCode) {

            case Constants.REQUEST_CODE_ALL_PERMISSIONS: {

                //0: Coarse Location
                //1: Fine Location
                //2: Write External Storage
                //3: Read External Storage

                if (grantResults.length > 0) {

                    for (int i = 0; i < grantResults.length; i++) {
                        Log.w(TAG, "onRequestPermissionsResult: " + grantResults[i]);

                        switch (i) {

                            case 0:
                            case 1: {
                                if (grantResults[i] != -1) {
                                    locationPermissionGranted = true;
                                }
                            }
                            break;

                            case 2:
                            case 3: {
                                if (grantResults[i] != -1) {
                                    accessInternalStorageGranted = true;
                                }
                            }
                            break;

                        }
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for listOfAgents
     */
    private List<Agent> getListOfAgents() {
        Log.d(TAG, "getListOfAgents: called!");
        if (listOfAgents == null) {
            return listOfAgents = new ArrayList<>();
        }
        return listOfAgents;
    }

    /**
     * Method that retrieves the list of agents in the database
     */
    @SuppressLint("CheckResult")
    private void getListOfAgentsFromDatabase() {
        Log.d(TAG, "getListOfAgentsFromDatabase: called!");

        getRepository().getAllAgents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Agent>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onNext(List<Agent> agents) {
                        Log.d(TAG, "onNext: " + agents);
                        listOfAgents = agents;
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

    /**
     * Method that is used to configure the layout
     */
    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");
        setHints();
        setPasswordInputType();
        setEmailIfInSharedPref();
        setListeners();
    }

    /**
     * Method that sets the password input type
     */
    private void setPasswordInputType() {
        Log.d(TAG, "setPasswordInputType: called!");
        tvPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    /**
     * Method that sets the hints in the views
     */
    private void setHints() {
        Log.d(TAG, "getAllTextInputLayouts: called!");
        Utils.getTextInputLayoutFromCardview(cvEmail).setHint("Email");
        Utils.getTextInputLayoutFromCardview(cvPassword).setHint("Password");
    }

    /**
     * Method used to get a reference to the views
     */
    private void getAllAcTextViews() {
        Log.d(TAG, "getAllTextviews: called!");
        tvEmail = Utils.getTextInputAutoCompleteTextViewFromCardView(cvEmail);
        tvPassword = Utils.getTextInputAutoCompleteTextViewFromCardView(cvPassword);
    }

    /**
     * Method used to set the information from SharedPreferences in the views
     */
    private void setEmailIfInSharedPref() {
        Log.d(TAG, "setEmailIfInSharedPref: called!");
        String[] info = Utils.readCurrentAgentData(this);
        tvEmail.setText(info[2]);
    }

    /**
     * Method used to set a listener to a specific text view
     */
    private void setListeners() {
        Log.d(TAG, "setListeners: called!");
        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");

                if (allNecessaryPermissionsGranted()) {

                    if (Utils.getStringFromTextView(tvEmail).isEmpty()) {
                        ToastHelper.toastShort(AuthLoginActivity.this, "Please, introduce an email");

                    } else if (listOfAgents.size() > 0) {

                        for (int i = 0; i < listOfAgents.size(); i++) {

                            if (Utils.getStringFromTextView(tvEmail).equalsIgnoreCase(listOfAgents.get(i).getEmail())) {
                                launchForgotPasswordActivityWithIntent(listOfAgents.get(i));
                                return;
                            }
                        }
                        ToastHelper.toastShort(AuthLoginActivity.this, "This email is not registered");

                    } else {
                        ToastHelper.toastShort(AuthLoginActivity.this, "Please, register first.");
                    }

                } else {
                    notifyAndAskForPermissions();
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to notify the user that the necessary permissions have not been granted and
     * that launches the permissions requests
     */
    private void notifyAndAskForPermissions() {
        Log.d(TAG, "notifyAndAskForPermissions: called!");
        ToastHelper.toastShort(this, "The app needs more permissions");
        checkAllPermissions();
    }

    /**
     * Method that checks if we have the necessary permissions (location and internal storage)
     */
    private boolean allNecessaryPermissionsGranted() {
        Log.d(TAG, "allNecessaryPermissionsGranted: called!");
        return locationPermissionGranted && accessInternalStorageGranted;
    }

    /**
     * Method used to check if the information inputted is correct. If it is, MainActivity is
     * launched
     */
    private boolean allChecksPassed() {
        Log.d(TAG, "allChecksPassed: called!");

        if (getListOfAgents().size() > 0) {

            /* We iterate throught the agents list to find if the email and password inputted match
             * with any in the database.
             * */
            for (int i = 0; i < getListOfAgents().size(); i++) {

                if (Utils.getStringFromTextView(tvEmail).equalsIgnoreCase(getListOfAgents().get(i).getEmail())) {
                    if (Utils.getStringFromTextView(tvPassword).equals(getListOfAgents().get(i).getPassword())) {

                        /* If the password of the email coincides, then we save the information in
                         * SharedPreferences and return true, which will launch MainActivity
                         * */
                        Utils.writeAgentDataShPref(this, getListOfAgents().get(i));
                        return true;
                    }
                }
            }

            /* If we do not find a matching password, the user gets notified
             * */
            ToastHelper.toastShort(this, "Sorry, some info is incorrect");
            return false;

        } else {
            ToastHelper.toastShort(this, "Please, register first.");
            return false;
        }
    }

    /**
     * Method to launch ForgotPasswordActivity with the information of the agent according
     * to the email inputted
     */
    private void launchForgotPasswordActivityWithIntent(Agent agent) {
        Log.d(TAG, "launchForgotPasswordActivityWithIntent: called!");
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra(Constants.SEND_PARCELABLE, agent);
        startActivity(intent);
    }

    /**
     * Method to launch MainActivity with an intent that clears the stack
     */
    private void launchMainActivityWithIntent() {
        Log.d(TAG, "launchMainActivityWithIntent: called!");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that checks if we have the necessary permissions
     */
    private void checkAllPermissions() {
        Log.d(TAG, "checkPermissions: called!");

        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                && checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationPermissionGranted = true;

        } else {
            requestPermission(Constants.ALL_PERMISSIONS, Constants.REQUEST_CODE_ALL_PERMISSIONS);
            return;
        }

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            accessInternalStorageGranted = true;

        } else {
            requestPermission(Constants.ALL_PERMISSIONS, Constants.REQUEST_CODE_ALL_PERMISSIONS);
        }
    }

    /**
     * Method that checks if we have a specific permission granted
     */
    private boolean checkPermission(String permission) {
        Log.d(TAG, "checkPermissions: called!");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method used to request permissions
     */
    private void requestPermission(String[] permissions, int requestCode) {
        Log.d(TAG, "requestPermission: called!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    /**
     * Method to load the information from the bundle (before screen rotation) in the textViews
     */
    private void loadInfoBeforeRotation(Bundle savedInstanceState) {
        Log.d(TAG, "loadInfoBeforeRotation: called!");
        if (savedInstanceState != null) {
            Log.i(TAG, "loadInfoBeforeRotation: " + savedInstanceState.getString(Constants.BUNDLE_EMAIL));
            Log.i(TAG, "loadInfoBeforeRotation: " + savedInstanceState.getString(Constants.BUNDLE_PASSWORD));
            tvEmail.setText(savedInstanceState.getString(Constants.BUNDLE_EMAIL));
            tvPassword.setText(savedInstanceState.getString(Constants.BUNDLE_PASSWORD));
        }
    }
}
