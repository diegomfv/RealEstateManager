package com.diegomfv.android.realestatemanager.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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

    @BindView(R.id.card_view_street_id)
    CardView cardViewStreet;

    @BindView(R.id.card_view_city_id)
    CardView cardViewCity;

    @BindView(R.id.card_view_postcode_id)
    CardView cardViewPostcode;

    private AutoCompleteTextView tvStreet;

    private AutoCompleteTextView tvCity;

    private AutoCompleteTextView tvPostcode;

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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.getAutocompleteTextViews();
        this.setAllHints();
    }

    private void getAutocompleteTextViews () {
        Log.d(TAG, "getAutocompleteTextViews: called!");

        this.tvStreet = cardViewStreet.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvCity = cardViewCity.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvPostcode = cardViewPostcode.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);

    }

    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");

        // TODO: 23/08/2018 Use Resources instead of hardcode text

        setHint(cardViewStreet, "Street");
        setHint(cardViewCity, "City");
        setHint(cardViewPostcode, "Postcode");

    }

    private void setHint (CardView cardView, String hint) {
        Log.d(TAG, "setHint: called!");

        TextInputLayout textInputLayout = cardView.findViewById(R.id.text_input_layout_id);
        textInputLayout.setHint(hint);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkTextViewsText(AlertDialog dialog) {
        Log.d(TAG, "checkTextViewsText: called!");

        if (tvStreet.getText().toString().trim().length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a street");

        } else if (tvCity.getText().toString().trim().length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a city");

        } else if (tvPostcode.getText().toString().trim().length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a postcode");

        } else {
            onButtonClickedListener.onDialogPositiveClick(
                    InsertAddressDialogFragment.this,
                    tvStreet.getText().toString().trim(),
                    tvCity.getText().toString().trim(),
                    tvPostcode.getText().toString().trim());

            dialog.dismiss();
        }
    }
}