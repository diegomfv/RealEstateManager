package com.diegomfv.android.realestatemanager.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * Created by Diego Fajardo on 29/08/2018.
 */
public class SignUpActivity extends BaseActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.toolbar_id)
    Toolbar toolbar;

    @BindView(R.id.card_view_first_name_id)
    CardView cvFirstName;

    @BindView(R.id.card_view_last_name_id)
    CardView cvLastName;

    @BindView(R.id.card_view_email_id)
    CardView cvEmail;

    @BindView(R.id.card_view_password_id)
    CardView cvPassword;

    @BindView(R.id.card_view_memorable_data_question_id)
    CardView cvMemDataQuestion;

    @BindView(R.id.card_view_memorable_data_answer_id)
    CardView cvMemDataAnswer;

    TextInputAutoCompleteTextView tvFirstName;

    TextInputAutoCompleteTextView tvLastName;

    TextInputAutoCompleteTextView tvEmail;

    TextInputAutoCompleteTextView tvPassword;

    TextInputAutoCompleteTextView tvMemDataQuestion;

    TextInputAutoCompleteTextView tvMemDataAnswer;

    @BindView(R.id.sign_up_button_id)
    Button buttonSignUp;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBar actionBar;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");


        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        unbinder = ButterKnife.bind(this);

        this.configureToolBar();

        this.getAllAutocompleteTextViews();

        this.configureLayout();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @OnClick(R.id.sign_up_button_id)
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: called!");

        switch (view.getId()) {

            case R.id.sign_up_button_id: {
                saveInfoAndLaunchActivity();
            }
            break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the toolbar.
     * Depending on mainMenu, on the button behaves one way or another. With mainMenu = true,
     * user can return to AuthLoginAtivity via a dialog that will pop-up. With mainMenu = false,
     * the user will go to SearchEngineActivity
     */
    private void configureToolBar() {
        Log.d(TAG, "configureToolBar: called!");
        setSupportActionBar(toolbar);
        setOverflowButtonColor(toolbar, Color.WHITE);

        setToolbarTitle();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");
                Utils.launchActivity(SignUpActivity.this, AuthLoginActivity.class);
            }
        });
    }

    /**
     * Method to set the toolbar title
     */
    private void setToolbarTitle() {
        Log.d(TAG, "setToolbarTitle: called!");
        toolbar.setTitle("Sign Up");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the layout.
     */
    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");
        setHints();
        setPasswordInputType();
    }

    /**
     * Method to set the input type for the password
     */
    private void setPasswordInputType() {
        Log.d(TAG, "setPasswordInputType: called!");
        tvPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    /**
     * Method to set the hints of all the Views.
     */
    private void setHints() {
        Log.d(TAG, "getAllTextInputLayouts: called!");
        Utils.getTextInputLayoutFromCardview(cvFirstName).setHint("First Name");
        Utils.getTextInputLayoutFromCardview(cvLastName).setHint("Last Name");
        Utils.getTextInputLayoutFromCardview(cvEmail).setHint("Email");
        Utils.getTextInputLayoutFromCardview(cvPassword).setHint("Password");
        Utils.getTextInputLayoutFromCardview(cvMemDataQuestion).setHint("Memorable Data Question");
        Utils.getTextInputLayoutFromCardview(cvMemDataAnswer).setHint("Memorable Data Answer");
    }

    /**
     * Method to get references to all the AutocompleteTextViews
     */
    private void getAllAutocompleteTextViews() {
        Log.d(TAG, "getAllTextviews: called!");
        tvFirstName = Utils.getTextInputAutoCompleteTextViewFromCardView(cvFirstName);
        tvLastName = Utils.getTextInputAutoCompleteTextViewFromCardView(cvLastName);
        tvEmail = Utils.getTextInputAutoCompleteTextViewFromCardView(cvEmail);
        tvPassword = Utils.getTextInputAutoCompleteTextViewFromCardView(cvPassword);
        tvMemDataQuestion = Utils.getTextInputAutoCompleteTextViewFromCardView(cvMemDataQuestion);
        tvMemDataAnswer = Utils.getTextInputAutoCompleteTextViewFromCardView(cvMemDataAnswer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to save the information inputted and launch MainActivity
     */
    private void saveInfoAndLaunchActivity() {
        Log.d(TAG, "saveInfoAndLaunchActivity: called!");

        if (allChecksPassed()) {
            Utils.writeAgentDataShPref(this,
                    Utils.getStringFromTextView(tvFirstName),
                    Utils.getStringFromTextView(tvLastName),
                    Utils.getStringFromTextView(tvEmail),
                    Utils.getStringFromTextView(tvPassword),
                    Utils.getStringFromTextView(tvMemDataQuestion),
                    Utils.getStringFromTextView(tvMemDataAnswer));

            ToastHelper.toastShort(this, "Sign up successful");
            Utils.launchActivity(this, MainActivity.class);
        }
    }

    /**
     * Method to check that the information inputted is correct
     */
    // TODO: 29/08/2018 Check!
    private boolean allChecksPassed() {
        Log.d(TAG, "allChecksPassed: called!");

        if (Utils.getStringFromTextView(tvFirstName).length() < 3) {
            ToastHelper.toastShort(this, "The first name is too short");
            return false;

        } else if (Utils.getStringFromTextView(tvLastName).length() < 3) {
            ToastHelper.toastShort(this, "The last name is too short");
            return false;

        } else if (Utils.getStringFromTextView(tvEmail).length() < 5) {
            ToastHelper.toastShort(this, "The email is too short");
            return false;

        } else if (Utils.getStringFromTextView(tvPassword).length() < 5) {
            ToastHelper.toastShort(this, "The password is too short");
            return false;

        } else if (Utils.getStringFromTextView(tvMemDataQuestion).length() < 3) {
            ToastHelper.toastShort(this, "The memorable data question is too short");
            return false;

        } else if (Utils.getStringFromTextView(tvMemDataAnswer).length() < 3) {
            ToastHelper.toastShort(this, "The memorable data answer is too short");
            return false;

        } else {
            return true;
        }
    }
}
