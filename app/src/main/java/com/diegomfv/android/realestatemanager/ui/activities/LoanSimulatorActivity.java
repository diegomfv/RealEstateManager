package com.diegomfv.android.realestatemanager.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterLoan;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterMediaHorizontalCreate;
import com.diegomfv.android.realestatemanager.models.Payment;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 06/09/2018.
 */

// TODO: 07/09/2018 When information is displayed in euros, decimals are always ,00

// TODO: 07/09/2018 Format the floats to two decimals
// TODO: 07/09/2018 Add currency variations
public class LoanSimulatorActivity extends BaseActivity {

    private static final String TAG = LoanSimulatorActivity.class.getSimpleName();

    @BindView(R.id.tvLoanAmount)
    TextView tvLoanAmount;

    @BindView(R.id.tvAnnInterestRate)
    TextView tvAnnualInterestRate;

    @BindView(R.id.tvLoanPeriodYears)
    TextView tvLoanPeriodInYears;

    @BindView(R.id.tvPaymentFrequency)
    TextView tvPaymentFrequency;

    @BindView(R.id.tvStartDate)
    TextView tvStartDate;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.recycler_view_loan_simulator_id)
    RecyclerView recyclerView;

    RVAdapterLoan adapter;

    //////////

    TextView tvMonthlyPayment;

    //////////

    TextView tvRatePerPeriod;

    TextView tvNumberOfPayments;

    TextView tvTotalPayments;

    TextView tvTotalInterests;

    TextView tvEstimInterestSavings;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ActionBar actionBar;

    private int currency;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.currency = Utils.readCurrentCurrencyShPref(this);;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_loan_simulator);
        unbinder = ButterKnife.bind(this);

        this.configureActionBar();

        setTexts();
        generateTable();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: called!");
        getMenuInflater().inflate(R.menu.currency_menu, menu);
        Utils.updateCurrencyIconWhenMenuCreated(this, currency, menu, R.id.menu_change_currency_button);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case android.R.id.home: {
                Utils.launchActivity(this, MainActivity.class);

            }
            break;

            case R.id.menu_change_currency_button: {

                changeCurrency();
                Utils.updateCurrencyIcon(this, currency, item);
                generateTable();

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

    private void changeCurrency() {
        Log.d(TAG, "changeCurrency: called!");

        if (this.currency == 0) {
            this.currency = 1;
        } else {
            this.currency = 0;
        }
        Utils.writeCurrentCurrencyShPref(this, currency);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void setTexts() {
        Log.d(TAG, "setTexts: called!");

        tvLoanAmount.setText(String.valueOf(100000d));
        tvAnnualInterestRate.setText(String.valueOf(0.05d));
        tvLoanPeriodInYears.setText(String.valueOf(20));
        tvPaymentFrequency.setText(String.valueOf(12));
        tvStartDate.setText("");

    }

    private float getScheduledPaymentPerPeriod() {
        Log.d(TAG, "simulateLoan: called!");

        float capital = Utils.getFloatFromTextView(tvLoanAmount);
        float i = Utils.getFloatFromTextView(tvAnnualInterestRate);
        int n = Utils.getIntegerFromTextView(tvLoanPeriodInYears);
        int f = Utils.getIntegerFromTextView(tvPaymentFrequency);

        Log.w(TAG, "getScheduledPaymentPerPeriod: = " + capital * i / (1 - Math.pow(1 + i, -n)) / f);

        return (float) (capital * i / (1 - Math.pow(1 + i, -n)) / f); //scheduled payment

    }

    private void generateTable () {
        Log.d(TAG, "generateTable: called!");

        float remainingCapital = Utils.getFloatFromTextView(tvLoanAmount);
        float i = Utils.getFloatFromTextView(tvAnnualInterestRate);
        int f = Utils.getIntegerFromTextView(tvPaymentFrequency);

        float schPayment = getScheduledPaymentPerPeriod();

        float principal;
        float interests;

        float cumInterests = 0f;

        int payN = 0;

        List<Payment> listOfPayments = new ArrayList<>();
        Payment.Builder builder;

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        /////////////////////////////

        while (remainingCapital > schPayment) {

            builder = new Payment.Builder();

            builder.setPaymentDate(date);

            calendar.setTime(date);
            calendar.add(Calendar.MONTH,1);

            payN++;

            date = calendar.getTime();

            builder.setPaynN(payN);
            builder.setBeginningBalance(remainingCapital);
            builder.setSchPayment(schPayment);

            Log.w(TAG, "generateTable: payN" + payN);
            Log.i(TAG, "generateTable: begBalance " + remainingCapital);

            interests = i * remainingCapital / f;
            principal = schPayment - interests;

            remainingCapital -= principal;
            cumInterests += interests;

            builder.setPrincipal(principal);
            builder.setInterests(interests);
            builder.setEndingBalance(remainingCapital);
            builder.setCumInterests(cumInterests);

            Log.i(TAG, "generateTable: principal = " + principal);
            Log.i(TAG, "generateTable: interests = " + interests);
            Log.i(TAG, "generateTable: endBalance = " + remainingCapital);

            listOfPayments.add(builder.build());

        }

        configureRecyclerView(listOfPayments);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureRecyclerView(List<Payment> listOfPayments) {
        Log.d(TAG, "configureRecyclerView: called!");

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.adapter = new RVAdapterLoan(
                this,
                listOfPayments,
                currency);
        this.recyclerView.setAdapter(this.adapter);

        this.configureOnClickRecyclerView();

    }

    private void configureOnClickRecyclerView() {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}
