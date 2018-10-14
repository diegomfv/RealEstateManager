package com.diegomfv.android.realestatemanager.ui.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

/**
 * This activity allows the user to simulate a loan inputting different kind of values.
 * Clicking the edit button, a dialog will be display to input thge information. The table with
 * the loan information can be seen clicking the list button. The user can change at anytime
 * between dollars and euros.
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

    private boolean completeLoanVisible;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called!");

        this.currency = Utils.readCurrentCurrencyShPref(this);

        /* We set the completeLoanVisible to false so the loan does not occupy all the screen
         * */
        this.completeLoanVisible = false;

        /* We set initial values for the loan
         * */
        this.loanAmountInDollars = 100000.00f;
        this.annualInterestRate = 5.00f;
        this.loanPeriodInYears = 20;
        this.paymentFrequency = 12;

        ////////////////////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_loan_simulator);
        unbinder = ButterKnife.bind(this);

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
                showOrHideLoan();
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

    /**
     * This callback gets triggered when the user clicks the positive button on the dialog.
     * It updates the layout with the information inputted in the dialog and generates a new
     * table.
     */
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

    /**
     * This callback gets triggered when the user clicks the negative button on the dialog.
     */
    @Override
    public void onDialogNegativeClick() {
        Log.d(TAG, "onDialogNegativeClick: called!");
        ToastHelper.toastShort(this, "Modifications were not saved");
        generateTable();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that modifies the currency variable and writes the new info to sharedPreferences.
     */
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

    /**
     * Method to configure the layout.
     */
    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.configureToolBar();

        setTextInTextViews();
        setTableTitleTextViewsStyle();
        generateTable();
    }

    /**
     * Method to set the texts in the Text Views.
     **/
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

    /**
     * Sets the style for the TextViews of the first row of the table.
     */
    private void setTableTitleTextViewsStyle() {
        Log.d(TAG, "setTableTitleTextViewsStyle: called!");

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

    /**
     * Method that sets the style of a TextView.
     */
    private void setStyle(TextView textView) {
        Log.d(TAG, "setStyle: called!");
        textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        Typeface typeface = ResourcesCompat.getFont(this, R.font.arima_madurai);
        textView.setTypeface(typeface, Typeface.BOLD);
    }

    /**
     * Method to update the views and, inf the information in them is correct, generate
     * the loan table.
     */
    private void updateViews() {
        Log.d(TAG, "updateViews: called!");
        setTextInTextViews();

        if (allChecksPassed()) {
            generateTable();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the RecyclerView.
     */
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

    /**
     * Method to configure the onClick listeners of the RecyclerView.
     */
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

    /**
     * Method that shows the table occupying all the screen.
     */
    private void showLoan() {
        Log.d(TAG, "showLoan: called!");
        constraintLayout.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        completeLoanVisible = true;

    }


    /**
     * Method that reduces the size of the table and displays the other elements of the layout.
     */
    private void hideLoan() {
        Log.d(TAG, "hideLoan: called!");
        constraintLayout.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
        completeLoanVisible = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to get the scheduled payment per period according to the information inputted
     */
    private float getScheduledPaymentPerPeriod() {
        Log.d(TAG, "simulateLoan: called!");

        float capital = loanAmountInDollars;
        float i = annualInterestRate / 100;
        int n = loanPeriodInYears;
        int f = paymentFrequency;

        Log.w(TAG, "getScheduledPaymentPerPeriod: = " + capital * i / (1 - Math.pow(1 + i, -n)) / f);

        /* We return the scheduled payment
         * */
        return (float) (capital * i / (1 - Math.pow(1 + i, -n)) / f);

    }

    /**
     * Method that generates the table of the loan according to the information inputted
     */
    private void generateTable() {
        Log.d(TAG, "generateTable: called!");

        float remainingCapital = loanAmountInDollars;
        float i = annualInterestRate / 100;
        int f = paymentFrequency;

        /* We get the scheduled payment and set it in the view
         * */
        float schPayment = getScheduledPaymentPerPeriod();
        tvScheduledPayment.setText(Utils.getValueFormattedAccordingToCurrency(schPayment, currency));

        float principal;
        float interests;

        float cumInterests = 0f;

        int payN = 0;

        List<Payment> listOfPayments = new ArrayList<>();
        Payment.Builder builder;

        /* We establish that the first day of the payment will be today
         * */
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        /////////////////////////////

        while (remainingCapital > schPayment) {

            builder = new Payment.Builder();

            /* We set the date and then update it for the next payment
             * */
            builder.setPaymentDate(date);

            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);
            date = calendar.getTime();

            /* We increase the number of the payment
             * */
            payN++;

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

            /* We add the payment to the list that will be used to display the information
             * in the RecyclerView
             * */
            listOfPayments.add(builder.build());

        }

        /* We set in the view the Total Interests
         * */
        tvTotalInterests.setText(Utils.getValueFormattedAccordingToCurrency(cumInterests, currency));

        /* RecyclerView configuration
         * */
        configureRecyclerView(listOfPayments);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to launch the dialog used to input information about the loan
     */
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

    /**
     * Method that shows the loan occupying all the screen
     * or not depending on "completeLoanVisible"
     * variable
     */
    private void showOrHideLoan() {
        Log.d(TAG, "showOrHideLoan: called!");
        if (completeLoanVisible) {
            hideLoan();

        } else {
            showLoan();
        }
    }

    /**
     * Method to check if the information inputted is valid. If it is, we can generate the
     * table
     */
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
