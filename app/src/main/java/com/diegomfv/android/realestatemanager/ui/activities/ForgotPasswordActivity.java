package com.diegomfv.android.realestatemanager.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
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

/**
 * Created by Diego Fajardo on 29/08/2018.
 */
public class ForgotPasswordActivity extends BaseActivity {

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.card_view_memorable_data_question_id)
    CardView cardViewQuestion;

    @BindView(R.id.card_view_memorable_data_answer_id)
    CardView cardViewAnswer;

    private TextInputAutoCompleteTextView tvQuestion;

    private TextInputAutoCompleteTextView tvAnswer;

    private Button buttonRemindPassword;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBar actionBar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_forgot_password);
        unbinder = ButterKnife.bind(this);

        this.configureActionBar();

        this.configureLayout();

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

                //5 --> memorable data answer from shared Pref
                if (Utils.getStringFromTextView(tvAnswer).equals(Utils.readCurrentAgentData(this)[5])) {
                    ToastHelper.toastLong(this, "PASSWORD: " + Utils.readCurrentAgentData(this)[3]);

                } else {
                    ToastHelper.toastLong(this, "Incorrect answer");


                }

            } break;
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

    private void configureActionBar() {
        Log.d(TAG, "configureActionBar: called!");

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeActionContentDescription(getResources().getString(R.string.go_back));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.getAutocompleteTextViews();
        this.setAllHints();
        this.setText();

    }

    private void getAutocompleteTextViews() {
        Log.d(TAG, "getAutocompleteTextViews: called!");
        this.tvQuestion = cardViewQuestion.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvAnswer = cardViewAnswer.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
    }

    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");

        // TODO: 23/08/2018 Use Resources instead of hardcoded
        setAcTvHint(cardViewQuestion, "Question");
        setAcTvHint(cardViewAnswer, "Answer");
    }

    private void setAcTvHint(CardView cardView, String hint) {
        Log.d(TAG, "setAcTvHint: called!");
        TextInputLayout textInputLayout = cardView.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint(hint);
    }

    private void setText () {
        Log.d(TAG, "setText: called!");
        tvQuestion.setText(Utils.readCurrentAgentData(this)[4]);

    }
}
