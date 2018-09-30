package com.diegomfv.android.realestatemanager.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.models.Payment;
import com.diegomfv.android.realestatemanager.util.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Diego Fajardo (https://github.com/diegomfv) on 07/09/2018.
 */
public class RVAdapterLoan extends RecyclerView.Adapter<RVAdapterLoan.MyViewHolder> {

    private static final String TAG = RVAdapterLoan.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Context context;

    private List<Payment> listOfPayments;

    int currency;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public RVAdapterLoan(Context context, List<Payment> listOfPayments, int currency) {
        this.context = context;
        this.listOfPayments = listOfPayments;
        this.currency = currency;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called!");

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(
                R.layout.rv_loan_item,
                parent,
                false);

        return new RVAdapterLoan.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called! = " + position);
        holder.updateItem(position);

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called!");
        if (listOfPayments == null) {
            return 0;
        } else {
            return listOfPayments.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = RVAdapterListings.MyViewHolder.class.getSimpleName();

        @BindView(R.id.textView_nPay_id)
        TextView tvNPay;

        @BindView(R.id.textView_pay_date_id)
        TextView tvPayDate;

        @BindView(R.id.textView_beg_balance_id)
        TextView tvBegBalance;

        @BindView(R.id.textView_sch_payment_id)
        TextView tvSchPayment;

        @BindView(R.id.textView_principal_id)
        TextView tvPrincipal;

        @BindView(R.id.textView_interests_id)
        TextView tvInterests;

        @BindView(R.id.textView_end_balance_id)
        TextView tvEndBalance;

        @BindView(R.id.textView_cum_interests_id)
        TextView tvCumInterests;

        ////////////////////////////////////////////////////////////////////////////////////////////

        public MyViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "MyViewHolder: called!");
            ButterKnife.bind(this, itemView);

        }

        /**
         * Method that updates the item of the holder.
         */
        public void updateItem(int position) {
            Log.d(TAG, "updateItem: called!");
            setTexts(position);
        }

        /**
         * Method to update the TextViews of the holder.
         */
        private void setTexts(int position) {
            Log.d(TAG, "setText: called!");
            tvNPay.setText(String.valueOf(listOfPayments.get(position).getPayN()));
            tvPayDate.setText(Utils.dateToString(listOfPayments.get(position).getPaymentDate()));
            tvBegBalance.setText(String.valueOf(Utils.getCurrencySymbol(currency) + " " + Utils.getValueFormattedAccordingToCurrency(listOfPayments.get(position).getBeginningBalance(), currency)));
            tvSchPayment.setText(String.valueOf(Utils.getCurrencySymbol(currency) + " " + Utils.getValueFormattedAccordingToCurrency(listOfPayments.get(position).getSchPayment(), currency)));
            tvPrincipal.setText(String.valueOf(Utils.getCurrencySymbol(currency) + " " + Utils.getValueFormattedAccordingToCurrency(listOfPayments.get(position).getPrincipal(), currency)));
            tvInterests.setText(String.valueOf(Utils.getCurrencySymbol(currency) + " " + Utils.getValueFormattedAccordingToCurrency(listOfPayments.get(position).getInterests(), currency)));
            tvEndBalance.setText(String.valueOf(Utils.getCurrencySymbol(currency) + " " + Utils.getValueFormattedAccordingToCurrency(listOfPayments.get(position).getEndingBalance(), currency)));
            tvCumInterests.setText(String.valueOf(Utils.getCurrencySymbol(currency) + " " + Utils.getValueFormattedAccordingToCurrency(listOfPayments.get(position).getCumInterests(), currency)));
        }
    }
}
