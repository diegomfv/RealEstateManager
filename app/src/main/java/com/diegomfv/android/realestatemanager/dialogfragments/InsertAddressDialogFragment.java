package com.diegomfv.android.realestatemanager.dialogfragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
public class InsertAddressDialogFragment extends DialogFragment {

    private static final String TAG = InsertAddressDialogFragment.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface InsertAddressDialogListener {

        void onDialogPositiveClick (DialogFragment dialogFragment);
        void onDialogNegativeClick (DialogFragment dialogFragment);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: called!");

        if (getActivity() != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getActivity(), R.layout.dialog_insert_address, null);
            unbinder = ButterKnife.bind(this, view);

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
                        }
                    });

            return builder.create();

        } else {
            ToastHelper.toastShort(getActivity(), "The activity is null. DialogFragment cannot be created");
            return null;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        unbinder.unbind();
    }
}
