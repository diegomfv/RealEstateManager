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

import com.diegomfv.android.realestatemanager.util.ToastHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Diego Fajardo on 05/09/2018.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private static final String TAG = DatePickerFragment.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface DatePickerFragmentListener {
        void onDateSet(Date date);
    }

    private DatePickerFragment.DatePickerFragmentListener listener;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static DatePickerFragment newInstance () {

        DatePickerFragment dialogFragment = new DatePickerFragment();
        return dialogFragment;
    }

    /** Could also be done like this and avoid using onAttach()
     * */
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

        return new DatePickerDialog(getActivity(), this, year, month, day);

    }


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
            notifyDatePickerListener(date);
        }
    }

    protected void notifyDatePickerListener(Date date) {
        if(this.listener != null) {
            this.listener.onDateSet(date);
        }
    }
}