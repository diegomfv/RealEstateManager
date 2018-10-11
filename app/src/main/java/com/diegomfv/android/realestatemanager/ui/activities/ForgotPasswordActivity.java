package com.diegomfv.android.realestatemanager.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.Agent;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.TextInputAutoCompleteTextView;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * Created by Diego Fajardo on 29/08/2018.
 */

/**
 * This activity allows the user to recover the password if he/she answers correctly
 * to the memorable data question.
 */
public class ForgotPasswordActivity extends BaseActivity {

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.toolbar_id)
    Toolbar toolbar;

    @BindView(R.id.textView_question_text_id)
    TextView tvQuestionText;

    @BindView(R.id.card_view_memorable_data_answer_id)
    CardView cardViewAnswer;

    private TextInputAutoCompleteTextView tvAnswer;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Agent agent;

    private Unbinder unbinder;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_forgot_password);
        unbinder = ButterKnife.bind(this);

        /* We get the agent info passed in the intent
         * */
        this.getIntentInfo();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @OnClick(R.id.button_remind_password_id)
    public void buttonClicked(View view) {
        Log.d(TAG, "buttonClicked: " + ((Button) view).getText().toString() + " clicked!");

        switch (view.getId()) {

            case R.id.button_remind_password_id: {

                if (Utils.getStringFromTextView(tvAnswer).equals(agent.getMemorableDataAnswer())) {
                    ToastHelper.toastLong(this, "PASSWORD: " + agent.getPassword());

                } else {
                    ToastHelper.toastLong(this, "Incorrect answer");
                }
            }
            break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case android.R.id.home: {
                Utils.launchActivity(this, AuthLoginActivity.class);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the toolbar.
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
                Utils.launchActivity(ForgotPasswordActivity.this, AuthLoginActivity.class);
            }
        });
    }

    /**
     * Method to set the toolbar title
     */
    private void setToolbarTitle() {
        Log.d(TAG, "setToolbarTitle: called!");
        toolbar.setTitle("Recover Password");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the layout.
     */
    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");
        this.configureToolBar();
        this.getAutocompleteTextViews();
        this.setAllHints();
        this.setText();
    }

    /**
     * Method to get a reference to the AutocompleteTextViews.
     */
    private void getAutocompleteTextViews() {
        Log.d(TAG, "getAutocompleteTextViews: called!");
        this.tvAnswer = cardViewAnswer.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
    }

    /**
     * Method to set the hints of all the Views.
     */
    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");
        // TODO: 23/08/2018 Use Resources instead of hardcoded text
        setAutoCompleteTextViewHint(cardViewAnswer, "Answer");
    }

    /**
     * Method that sets the hint in a AutoCompleteTextView.
     */
    private void setAutoCompleteTextViewHint(CardView cardView, String hint) {
        Log.d(TAG, "setAutoCompleteTextViewHint: called!");
        TextInputLayout textInputLayout = cardView.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint(hint);
    }

    /**
     * Method to set the text in a specific view.
     */
    private void setText() {
        Log.d(TAG, "setText: called!");
        tvQuestionText.setText(agent.getMemorableDataQuestion());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to get the intent info and fill the agent field.
     */
    private void getIntentInfo() {
        Log.d(TAG, "getIntentInfo: called!");
        agent = Objects.requireNonNull(getIntent().getExtras()).getParcelable(Constants.SEND_PARCELABLE);
        configureLayout();
    }
}
