package com.diegomfv.android.realestatemanager.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 06/09/2018.
 */
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


    @BindView(R.id.recycler_view_loan_simulator_id)
    RecyclerView recyclerView;

    //////////

    TextView tvMonthlyPayment;

    //////////

    TextView tvRatePerPeriod;

    TextView tvNumberOfPayments;

    TextView tvTotalPayments;

    TextView tvTotalInterests;

    TextView tvEstimInterestSavings;


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_loan_simulator);
        unbinder = ButterKnife.bind(this);

        setTexts();
        generateTable();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called!");
        unbinder.unbind();
    }

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

        int payN = 1;

        /////////////////////////////

        while (remainingCapital > schPayment) {
            payN++;

            Log.w(TAG, "generateTable: payN" + payN);
            Log.i(TAG, "generateTable: begBalance " + remainingCapital);

            interests = i * remainingCapital / f;
            principal = schPayment - interests;

            remainingCapital -= principal;

            Log.i(TAG, "generateTable: principal = " + principal);
            Log.i(TAG, "generateTable: interests = " + interests);
            Log.i(TAG, "generateTable: endBalance = " + remainingCapital);

        }

    }

}
