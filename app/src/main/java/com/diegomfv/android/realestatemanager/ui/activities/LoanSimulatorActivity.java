package com.diegomfv.android.realestatemanager.ui.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterLoan;
import com.diegomfv.android.realestatemanager.models.Payment;
import com.diegomfv.android.realestatemanager.ui.base.BaseActivity;
import com.diegomfv.android.realestatemanager.ui.dialogfragments.ModifyLoanDialogFragment;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.diegomfv.android.realestatemanager.util.Utils.setOverflowButtonColor;

/**
 * Created by Diego Fajardo on 06/09/2018.
 */

public class LoanSimulatorActivity extends BaseActivity implements ModifyLoanDialogFragment.ModifyLoanDialogListener {

    private static final String TAG = LoanSimulatorActivity.class.getSimpleName();

    @BindView(R.id.toolbar_id)
    Toolbar toolbar;

    @BindView(R.id.constraintLayout_main_id)
    ConstraintLayout constraintLayout;

    @BindView(R.id.frameLayout_id)
    FrameLayout frameLayout;

    @BindView(R.id.tvLoanAmount)
    TextView tvLoanAmount;

    @BindView(R.id.tvAnnualInterestRate)
    TextView tvAnnualInterestRate;

    @BindView(R.id.tvLoanPeriodYears)
    TextView tvLoanPeriodInYears;

    @BindView(R.id.tvPaymentFreq)
    TextView tvPaymentFrequency;

    @BindView(R.id.tvStartDate)
    TextView tvStartDate;

    @BindView(R.id.tvSchedPayment)
    TextView tvScheduledPayment;

    @BindView(R.id.tvTotalInterest)
    TextView tvTotalInterests;

    @BindView(R.id.layout_main_card_view_id)
    CardView cardViewLoanTitle;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.recycler_view_loan_simulator_id)
    RecyclerView recyclerView;

    RVAdapterLoan adapter;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    float loanAmountInDollars;

    float annualInterestRate;

    int loanPeriodInYears;

    int paymentFrequency;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int currency;

    private boolean loanVisible;

    ConstraintLayout.LayoutParams params;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.currency = Utils.readCurrentCurrencyShPref(this);

        this.loanVisible = false;

        /* We set initial values for the loan
         * */
        this.loanAmountInDollars = 100000.00f;
        this.annualInterestRate = 5.00f;
        this.loanPeriodInYears = 20;
        this.paymentFrequency = 12;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_loan_simulator);
        unbinder = ButterKnife.bind(this);

        this.configureToolBar();
        this.configureLayout();

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
        getMenuInflater().inflate(R.menu.loan_menu, menu);
        Utils.updateCurrencyIconWhenMenuCreated(this, currency, menu, R.id.menu_change_currency_button);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: called!");

        switch (item.getItemId()) {

            case R.id.menu_show_loan_id: {
                if (loanVisible) {
                    hideLoan();

                } else {
                    showLoan();
                }

            }
            break;

            case R.id.menu_modify_loan_button: {
                launchModifyLoanDialog();

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


    @Override
    public void onDialogPositiveClick(float loanAmount, float annualInterestRate, int loanPeriodInYears, int paymentFreq) {
        Log.d(TAG, "onDialogPositiveClick: called!");
        Log.w(TAG, "onDialogPositiveClick: " + loanAmount);


        /* Updating the layout
         * */
        this.loanAmountInDollars = loanAmount;
        this.annualInterestRate = annualInterestRate;
        this.loanPeriodInYears = loanPeriodInYears;
        this.paymentFrequency = paymentFreq;
        updateViews();
    }

    @Override
    public void onDialogNegativeClick() {
        Log.d(TAG, "onDialogNegativeClick: called!");
        ToastHelper.toastShort(this, "Modifications were not saved");
        generateTable();
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

        updateViews();
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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");
                Utils.launchActivity(LoanSimulatorActivity.this, MainActivity.class);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");
        setTextInTextViews();
        setRvTitleLayoutStyle();
        generateTable();
    }

    private void setTextInTextViews() {
        Log.d(TAG, "setTextInTextViews: called!");

        /* Random data for the beginning
         * */
        tvLoanAmount.setText(Utils.getValueFormattedAccordingToCurrency(loanAmountInDollars, currency));
        tvAnnualInterestRate.setText(String.valueOf(annualInterestRate));
        tvLoanPeriodInYears.setText(String.valueOf(loanPeriodInYears));
        tvPaymentFrequency.setText(String.valueOf(paymentFrequency));
        tvStartDate.setText(Utils.dateToString(new Date()));
    }

    private void setRvTitleLayoutStyle() {
        Log.d(TAG, "setRvTitleLayoutStyle: called!");

        LinearLayout linearLayout = cardViewLoanTitle.findViewById(R.id.main_layout_id);

        setStyle((TextView) linearLayout.findViewById(R.id.textView_nPay_id));
        setStyle((TextView) linearLayout.findViewById(R.id.textView_pay_date_id));
        setStyle((TextView) linearLayout.findViewById(R.id.textView_beg_balance_id));
        setStyle((TextView) linearLayout.findViewById(R.id.textView_sch_payment_id));
        setStyle((TextView) linearLayout.findViewById(R.id.textView_principal_id));
        setStyle((TextView) linearLayout.findViewById(R.id.textView_interests_id));
        setStyle((TextView) linearLayout.findViewById(R.id.textView_end_balance_id));
        setStyle((TextView) linearLayout.findViewById(R.id.textView_cum_interests_id));
    }

    private void setStyle (TextView textView) {
        Log.d(TAG, "setStyle: called!");
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        Typeface typeface = ResourcesCompat.getFont(this, R.font.arima_madurai);
        textView.setTypeface(typeface, Typeface.BOLD);
    }

    private void updateViews() {
        Log.d(TAG, "updateViews: called!");
        setTextInTextViews();

        if (allChecksPassed()) {
            generateTable();
        }
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

    private void showLoan() {
        Log.d(TAG, "showLoan: called!");
        constraintLayout.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        loanVisible = true;

    }

    private void hideLoan() {
        Log.d(TAG, "hideLoan: called!");
        constraintLayout.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
        loanVisible = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private float getScheduledPaymentPerPeriod() {
        Log.d(TAG, "simulateLoan: called!");

        float capital = loanAmountInDollars;
        float i = annualInterestRate / 100;
        int n = loanPeriodInYears;
        int f = paymentFrequency;

        Log.w(TAG, "getScheduledPaymentPerPeriod: = " + capital * i / (1 - Math.pow(1 + i, -n)) / f);

        return (float) (capital * i / (1 - Math.pow(1 + i, -n)) / f); //scheduled payment

    }

    private void generateTable() {
        Log.d(TAG, "generateTable: called!");

        float remainingCapital = loanAmountInDollars;
        float i = annualInterestRate / 100;
        int f = paymentFrequency;

        float schPayment = getScheduledPaymentPerPeriod();

        tvScheduledPayment.setText(Utils.getValueFormattedAccordingToCurrency(schPayment, currency));

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
            calendar.add(Calendar.MONTH, 1);

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

        tvTotalInterests.setText(Utils.getValueFormattedAccordingToCurrency(cumInterests, currency));

        configureRecyclerView(listOfPayments);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void launchModifyLoanDialog() {
        Log.d(TAG, "launchModifyLoanDialog: called!");

        ModifyLoanDialogFragment
                .newInstance(loanAmountInDollars,
                        annualInterestRate,
                        loanPeriodInYears,
                        paymentFrequency,
                        currency)
                .show(
                        getSupportFragmentManager(),
                        "ModifyLoanDialogFragment");
    }

    private boolean allChecksPassed() {
        Log.d(TAG, "allChecksPassed: called!");

        if (Utils.getStringFromTextView(tvLoanAmount).length() < 4) {
            ToastHelper.toastShort(this, "LoanAmount not valid");
            return false;
        }
        if (Utils.getFloatFromTextView(tvAnnualInterestRate) == 0f) {
            ToastHelper.toastShort(this, "Annual Interest Rate not valid");
            return false;
        }
        if (Utils.getIntegerFromTextView(tvLoanPeriodInYears) == 0) {
            ToastHelper.toastShort(this, "Loan Period not valid");
            return false;
        }
        if (Utils.getIntegerFromTextView(tvPaymentFrequency) == 0) {
            ToastHelper.toastShort(this, "Payment Frequency not valid");
            return false;
        }
        return true;
    }
}
