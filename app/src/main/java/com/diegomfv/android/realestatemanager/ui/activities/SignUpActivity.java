package com.diegomfv.android.realestatemanager.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.diegomfv.android.realestatemanager.data.entities.Agent;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;
import com.jakewharton.rxbinding2.widget.RxAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private TextInputAutoCompleteTextView tvFirstName;

    private TextInputAutoCompleteTextView tvLastName;

    private TextInputAutoCompleteTextView tvEmail;

    private TextInputAutoCompleteTextView tvPassword;

    private TextInputAutoCompleteTextView tvMemDataQuestion;

    private TextInputAutoCompleteTextView tvMemDataAnswer;

    @BindView(R.id.sign_up_button_id)
    Button buttonSignUp;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<Agent> listOfAgents;

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

        /* We get all the accounts so a new account cannot be created with the same email
         * */
        this.getAllAccounts();

        /* Layout configuration
         * */
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

                if (allChecksPassed()) {
                    saveInfoAndLaunchActivity();
                }
            }
            break;
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
     * Setter for listOfAgents
     */
    private void setListOfAgents(List<Agent> listOfAgents) {
        Log.d(TAG, "setListOfAgents: called!");
        this.listOfAgents = listOfAgents;
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

        this.configureToolBar();

        this.getAllAutocompleteTextViews();
        this.setHints();
        this.setPasswordInputType();
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

    ////////////////////////////////////////////////////////////////////////////////////////////////


    @SuppressLint("CheckResult")
    private void getAllAccounts() {
        Log.d(TAG, "getAllAccounts: called!");

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

                        /* We fill the listOfAgents with all the agents in the database.
                         * Ww will use this information to avoid creating two accounts with the same
                         * information
                         * */
                        setListOfAgents(agents);
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
     * Method to save the information inputted and launch MainActivity
     */
    @SuppressLint("CheckResult")
    private void saveInfoAndLaunchActivity() {
        Log.d(TAG, "saveInfoAndLaunchActivity: called!");

        /* Firstly, we check that the enail is not already in use
         * */
        for (int i = 0; i < getListOfAgents().size(); i++) {
            if (getListOfAgents().get(i).getEmail().equalsIgnoreCase(Utils.getStringFromTextView(tvEmail))) {
                ToastHelper.toastShort(this, "This email is already in use by other account.");
                return;
            }
        }

        /* If it is not, we store the information
         * of the new agent in the database
         * */
        getRepository().insertAgent(
                new Agent(
                        Utils.getStringFromTextView(tvEmail),
                        Utils.getStringFromTextView(tvPassword),
                        Utils.getStringFromTextView(tvFirstName),
                        Utils.getStringFromTextView(tvLastName),
                        Utils.getStringFromTextView(tvMemDataQuestion),
                        Utils.getStringFromTextView(tvMemDataAnswer)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: called!");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: completed!");

                        /* We fill Shared Preferences with this agent data
                         * */
                        Utils.writeAgentDataShPref(
                                SignUpActivity.this,
                                new Agent(
                                        Utils.getStringFromTextView(tvEmail),
                                        Utils.getStringFromTextView(tvPassword),
                                        Utils.getStringFromTextView(tvFirstName),
                                        Utils.getStringFromTextView(tvLastName),
                                        Utils.getStringFromTextView(tvMemDataQuestion),
                                        Utils.getStringFromTextView(tvMemDataAnswer))
                        );

                        /* We launch MainActivity using an intent that clears the stack
                         * */
                        ToastHelper.toastShort(SignUpActivity.this, "Sign up successful");
                        launchActivityWithIntent();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        ToastHelper.toastShort(SignUpActivity.this, "Something went wrong");
                    }
                });
    }

    /**
     * Method to check that the information inputted is correct
     */
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to launch the activity with an intent that clears the stack
     */
    private void launchActivityWithIntent() {
        Log.d(TAG, "launchActivityWithIntent: called!");
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}