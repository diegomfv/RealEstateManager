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
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */

/**
 * Tássio Auad, Medium
 * DialogFragment/AlertDialog dismiss automatically on click button.
 * <p>
 * What happens with AlertDialog’s setButton() method (and I imagine the same with
 * AlertDialogBuilder’s setPositiveButton() and setNegativeButton()) is that the button
 * you set (e.g. AlertDialog.BUTTON_POSITIVE) with it will actually trigger TWO different
 * OnClickListener objects when pressed. The first being DialogInterface.OnClickListener,
 * which is a parameter to setButton(), setPositiveButton(), and setNegativeButton().
 * The other is View.OnClickListener, which will be set to automatically dismiss your AlertDialog
 * when any of its button is pressed — and is set by AlertDialog itself.
 * That is why we override the View.OnClickListener();
 */

public class InsertAddressDialogFragment extends DialogFragment {

    private static final String TAG = InsertAddressDialogFragment.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.card_view_street_id)
    CardView cardViewStreet;

    @BindView(R.id.card_view_locality_id)
    CardView cardViewLocality;

    @BindView(R.id.card_view_address_id)
    CardView cardViewCity;

    @BindView(R.id.card_view_postcode_id)
    CardView cardViewPostcode;

    private AutoCompleteTextView tvStreet;

    private AutoCompleteTextView tvLocality;

    private AutoCompleteTextView tvCity;

    private AutoCompleteTextView tvPostcode;

    private AddressRealEstate addressRealEstate;

    private Button buttonOk;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface InsertAddressDialogListener {
        void onDialogPositiveClick(AddressRealEstate addressRealEstate);

        void onDialogNegativeClick();
    }

    private InsertAddressDialogListener listener;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to create a new instance of the fragment
     */
    public static InsertAddressDialogFragment newInstance(AddressRealEstate addressRealEstate) {

        InsertAddressDialogFragment dialogFragment = new InsertAddressDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(Constants.DIALOG_ADDRESS, addressRealEstate);
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
                addressRealEstate = getArguments().getParcelable(Constants.DIALOG_ADDRESS);
            }

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
            listener = (InsertAddressDialogListener) context;
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

    /**
     * Method to configure the layout
     */
    private void configureLayout() {
        Log.d(TAG, "configureLayout: called!");

        this.getAutocompleteTextViews();
        this.setAllHints();
        this.setText();
    }

    /**
     * Method to get a reference to the AutocompleteTextViews
     */
    private void getAutocompleteTextViews() {
        Log.d(TAG, "getAutocompleteTextViews: called!");
        this.tvStreet = cardViewStreet.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvLocality = cardViewLocality.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvCity = cardViewCity.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
        this.tvPostcode = cardViewPostcode.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);
    }

    /**
     * Method to set the hints of all the Views.
     */
    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");

        // TODO: 23/08/2018 Use Resources instead of hardcode text

        setHint(cardViewStreet, "Street");
        setHint(cardViewLocality, "Locality");
        setHint(cardViewCity, "City");
        setHint(cardViewPostcode, "Postcode");
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
        tvStreet.setText(addressRealEstate.getStreet());
        tvLocality.setText(addressRealEstate.getLocality());
        tvCity.setText(addressRealEstate.getCity());
        tvPostcode.setText(addressRealEstate.getPostcode());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that checks if the TextViews's texts introduced are valid
     */
    private void checkTextViewsText(AlertDialog dialog) {
        Log.d(TAG, "checkTextViewsText: called!");

        if (Utils.getStringFromTextView(tvStreet).length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a street");

        } else if (Utils.getStringFromTextView(tvLocality).length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a locality");

        } else if (Utils.getStringFromTextView(tvCity).length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a city");

        } else if (Utils.getStringFromTextView(tvPostcode).length() == 0) {
            ToastHelper.toastShort(getActivity(), "Please, introduce a postcode");

        } else {
            listener.onDialogPositiveClick(
                    new AddressRealEstate(
                            Utils.getStringFromTextView(tvStreet),
                            Utils.getStringFromTextView(tvLocality),
                            Utils.getStringFromTextView(tvCity),
                            Utils.getStringFromTextView(tvPostcode)
                    ));

            dialog.dismiss();
        }
    }
}
