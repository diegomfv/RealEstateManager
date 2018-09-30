package com.diegomfv.android.realestatemanager.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Diego Fajardo on 05/09/2018.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = DatePickerFragment.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface DatePickerFragmentListener {
        void onDateSet(Date date);

        void onNegativeButtonClicked();
    }

    private DatePickerFragment.DatePickerFragmentListener listener;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to create a new instance of the fragment
     */
    public static DatePickerFragment newInstance() {

        DatePickerFragment dialogFragment = new DatePickerFragment();
        return dialogFragment;
    }

    /**
     * Could also be done like this and avoid using onAttach()
     */
//    public static DatePickerFragment newInstance (DatePickerFragmentListener listener) {
//
//        DatePickerFragment dialogFragment = new DatePickerFragment();
//        fragment.setDatePickerListener(listener);
//        return dialogFragment;
//    }
    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: called!");

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: negative button clicked!");
                        notifyDatePickerListenerNegativeButtonClicked();
                    }
                });

        return datePickerDialog;

    }

    /**
     * We set the listener in OnAttach()
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: called!");
        try {
            listener = (DatePickerFragment.DatePickerFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DatePickerFragmentListener");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.w(TAG, "onDateSet: year = " + year + "; " + "month = " + month + "; " + "dayOfMonth = " + dayOfMonth);

        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);
        Date date = c.getTime();

        // Here we call the listener and pass the date back to it.
        if (listener != null) {
            notifyDatePickerListenerDateIsSet(date);
        }
    }

    /**
     * Method to notify the listener that the date is set
     */
    protected void notifyDatePickerListenerDateIsSet(Date date) {
        if (this.listener != null) {
            this.listener.onDateSet(date);
        }
    }

    /**
     * Method to notify the listener that the negative button has been clicked
     */
    protected void notifyDatePickerListenerNegativeButtonClicked() {
        if (this.listener != null) {
            this.listener.onNegativeButtonClicked();
        }
    }
}
