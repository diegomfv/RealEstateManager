package com.diegomfv.android.realestatemanager.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo (https://github.com/diegomfv) on 23/09/2018.
 */
public class ModifyLoanDialogFragment extends DialogFragment {

    private static final String TAG = ModifyLoanDialogFragment.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.card_view_loan_amount_id)
    CardView cardViewLoanAmount;

    @BindView(R.id.card_view_loan_annual_interest_rate_id)
    CardView cardViewLoanAnnualInterest;

    @BindView(R.id.card_view_loan_period_in_years_id)
    CardView cardViewLoanPeriodInYears;

//    @BindView(R.id.card_view_loan_payment_frequency_id)
//    CardView cardViewLoanPaymentFrequency;

    private Button buttonOk;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private TextInputEditText tvLoanAmount;

    private TextInputEditText tvLoanAnnualIntRate;

    private TextInputEditText tvLoanPeriodYears;

    private TextInputEditText tvLoanPaymentFreq;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private float loanAmountInDollars;

    private float annualInterestRate;

    private int loanPeriodInYears;

    private int paymentFreq;

    private int currency;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface ModifyLoanDialogListener {
        void onDialogPositiveClick(float loanAmount, float annualInterestRate,
                                   int loanPeriodInYears, int paymentFreq);

        void onDialogNegativeClick();
    }

    private ModifyLoanDialogListener listener;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to create a new instance of the fragment
     */
    public static ModifyLoanDialogFragment newInstance(float loanAmount, float annualInterestRate,
                                                       int loanPeriodInYears, int paymentFreq,
                                                       int currency) {

        ModifyLoanDialogFragment dialogFragment = new ModifyLoanDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putFloat(Constants.DIALOG_LOAN_AMOUNT, loanAmount);
        args.putFloat(Constants.DIALOG_ANNUAL_INTEREST_RATE, annualInterestRate);
        args.putInt(Constants.DIALOG_LOAN_PERIOD_YEARS, loanPeriodInYears);
        args.putInt(Constants.DIALOG_PAYMENT_FREQ, paymentFreq);
        args.putInt(Constants.DIALOG_CURRENCY, currency);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: called!");

        if (getActivity() != null) {

            if (getArguments() != null) {
                loanAmountInDollars = getArguments().getFloat(Constants.DIALOG_LOAN_AMOUNT);
                annualInterestRate = getArguments().getFloat(Constants.DIALOG_ANNUAL_INTEREST_RATE);
                loanPeriodInYears = getArguments().getInt(Constants.DIALOG_LOAN_PERIOD_YEARS);
                paymentFreq = getArguments().getInt(Constants.DIALOG_PAYMENT_FREQ);
                currency = getArguments().getInt(Constants.DIALOG_CURRENCY);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getActivity(), R.layout.dialog_modify_loan, null);
            unbinder = ButterKnife.bind(this, view);

            this.configureLayout();

            builder.setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick: called!");


                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick: called!");
                            listener.onDialogNegativeClick();
                        }
                    });

            return builder.create();

        } else {
            ToastHelper.toastShort(getActivity(), "The activity is null. DialogFragment cannot be created");
            return null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called!");

        final AlertDialog alertDialog = (AlertDialog) getDialog();
        buttonOk = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: called!");
                checkTextViewsText(alertDialog);
            }
        });

    }

    /**
     * We set the listener in OnAttach()
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: called!");

        try {
            listener = (ModifyLoanDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implementModifyLoanDialogListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        buttonOk.setOnClickListener(null);
        unbinder.unbind();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to configure the layout
     */
    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");
        this.getTextViews();
        this.setAllHints();
        this.setText();
    }

    /**
     * Method to get a reference to the TextViews
     */
    private void getTextViews() {
        Log.d(TAG, "getTextViews: called!");
        this.tvLoanAmount = cardViewLoanAmount.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
        this.tvLoanAnnualIntRate = cardViewLoanAnnualInterest.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
        this.tvLoanPeriodYears = cardViewLoanPeriodInYears.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
        //this.tvLoanPaymentFreq = cardViewLoanPaymentFrequency.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_edit_text_id);
    }

    /**
     * Method to set the hints of all the Views.
     */
    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");
        // TODO: 23/08/2018 Use Resources instead of hardcode text
        setHint(cardViewLoanAmount, "Loan Amount (" + Utils.getCurrencySymbol(currency) + ")");
        setHint(cardViewLoanAnnualInterest, "Annual Interest Rate (%)");
        setHint(cardViewLoanPeriodInYears, "Loan Period(in Years)");
        //setHint(cardViewLoanPaymentFrequency, "Payment Frequency (payments in  a year)");
    }

    /**
     * Method that sets the hint in a TextInputLayout.
     */
    private void setHint(CardView cardView, String hint) {
        Log.d(TAG, "setHint: called!");
        TextInputLayout textInputLayout = cardView.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint(hint);
    }

    /**
     * Method to set the text of a TextView
     */
    private void setText() {
        Log.d(TAG, "setText: called!");
        tvLoanAmount.setText(String.valueOf(Utils.getValueAccordingToCurrency(currency, loanAmountInDollars)));
        tvLoanAnnualIntRate.setText(String.valueOf(annualInterestRate));
        tvLoanPeriodYears.setText(String.valueOf(loanPeriodInYears));
        //tvLoanPaymentFreq.setText(String.valueOf(paymentFreq));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkTextViewsText(AlertDialog dialog) {
        Log.d(TAG, "checkTextViewsText: called!");

        float loanAmount = Utils.getFloatFromTextView(tvLoanAmount);
        float interestRate = Utils.getFloatFromTextView(tvLoanAnnualIntRate);
        int loanPeriod = Utils.getIntegerFromTextView(tvLoanPeriodYears);
        int paymentFrequency = 12;


        if (Utils.getStringFromTextView(tvLoanAmount).length() == 0 || loanAmount == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a Loan Amount");

        } else if (Utils.getStringFromTextView(tvLoanAnnualIntRate).length() == 0 || interestRate == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce an Annual Interest Rate");

        } else if (Utils.getStringFromTextView(tvLoanPeriodYears).length() == 0 || loanPeriod == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a Period in years");

//        } else if (Utils.getStringFromTextView(tvLoanPaymentFreq).length() == 0 || paymentFrequency == 0) {
//            ToastHelper.toastShort(getActivity(), "Please, introduce the Payment Frequency");

        } else if (loanAmount > 1000000) {
            ToastHelper.toastShort(getActivity(), "Sorry, the loan amount is too high");

        } else if (interestRate > 15) {
            ToastHelper.toastShort(getActivity(), "Sorry, the interest rate is too high");

        } else if (loanPeriod > 60) {
            ToastHelper.toastShort(getActivity(), "Sorry, the loan period is too high");

        } else if (paymentFrequency > 12) {
            ToastHelper.toastShort(getActivity(), "Sorry, the payment frequency is too high");

        } else if (loanAmount < 50000) {
            ToastHelper.toastShort(getActivity(), "Sorry, the loan amount is too low");

        } else if (loanPeriod < 5) {
            ToastHelper.toastShort(getActivity(), "Sorry, the loan period is too short");

        } else if (paymentFrequency < 1) {
            ToastHelper.toastShort(getActivity(), "Sorry, the payment frequency is too low");

        } else {

            loanAmountInDollars = Float.valueOf(Utils.getStringFromTextView(tvLoanAmount));
            annualInterestRate = Float.valueOf(Utils.getStringFromTextView(tvLoanAnnualIntRate));
            loanPeriodInYears = Integer.valueOf(Utils.getStringFromTextView(tvLoanPeriodYears));
            //paymentFreq = Integer.valueOf(Utils.getStringFromTextView(tvLoanPaymentFreq));

            if (currency == 1) {
                //if the price is in euros, we convert it to dollars
                loanAmountInDollars = Float.valueOf(Utils.getStringFromTextView(tvLoanAmount));
            }

            listener.onDialogPositiveClick(
                    loanAmountInDollars,
                    annualInterestRate,
                    loanPeriodInYears,
                    paymentFreq
            );

            dialog.dismiss();
        }
    }
}
