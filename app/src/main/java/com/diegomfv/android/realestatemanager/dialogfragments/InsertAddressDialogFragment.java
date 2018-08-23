package com.diegomfv.android.realestatemanager.dialogfragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.utils.ToastHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */

/**
 * Tássio Auad, Medium
 * DialogFragment/AlertDialog dismiss automatically on click button.
 *
 * What happens with AlertDialog’s setButton() method (and I imagine the same with
 * AlertDialogBuilder’s setPositiveButton() and setNegativeButton()) is that the button
 * you set (e.g. AlertDialog.BUTTON_POSITIVE) with it will actually trigger TWO different
 * OnClickListener objects when pressed. The first being DialogInterface.OnClickListener,
 * which is a parameter to setButton(), setPositiveButton(), and setNegativeButton().
 * The other is View.OnClickListener, which will be set to automatically dismiss your AlertDialog
 * when any of its button is pressed — and is set by AlertDialog itself.
 * That is why we override the View.OnClickListener();
 * */

public class InsertAddressDialogFragment extends DialogFragment {

    private static final String TAG = InsertAddressDialogFragment.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.edit_text_street_id)
    EditText editTextStreet;

    @BindView(R.id.edit_text_city_id)
    EditText editTextCity;

    @BindView(R.id.edit_text_postcode_id)
    EditText editTextPostcode;

    private Button buttonOk;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface InsertAddressDialogListener {
        void onDialogPositiveClick (DialogFragment dialogFragment, String street, String city, String postcode);
        void onDialogNegativeClick (DialogFragment dialogFragment);
    }

    private InsertAddressDialogListener onButtonClickedListener;

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
                            onButtonClickedListener.onDialogNegativeClick(InsertAddressDialogFragment.this);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: called!");

        try {
            onButtonClickedListener = (InsertAddressDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement InsertAddressDialogListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        buttonOk.setOnClickListener(null);
        unbinder.unbind();

    }

    private void checkTextViewsText(AlertDialog dialog) {
        Log.d(TAG, "checkTextViewsText: called!");

        if (editTextStreet.getText().toString().trim().length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a street");

        } else if (editTextCity.getText().toString().trim().length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a city");

        } else if (editTextPostcode.getText().toString().trim().length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a postcode");

        } else {
            onButtonClickedListener.onDialogPositiveClick(
                    InsertAddressDialogFragment.this,
                    editTextStreet.getText().toString().trim(),
                    editTextCity.getText().toString().trim(),
                    editTextPostcode.getText().toString().trim());

            dialog.dismiss();
        }
    }
}
