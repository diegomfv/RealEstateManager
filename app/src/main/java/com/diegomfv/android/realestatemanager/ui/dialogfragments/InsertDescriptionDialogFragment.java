package com.diegomfv.android.realestatemanager.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.util.ToastHelper;
import com.diegomfv.android.realestatemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 02/09/2018.
 */
public class InsertDescriptionDialogFragment extends android.support.v4.app.DialogFragment {

    private static final String TAG = InsertDescriptionDialogFragment.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.card_view_description_id)
    CardView cardViewDescription;

    private AutoCompleteTextView tvDescription;

    private Button buttonOk;

    private ImageRealEstate imageRealEstate;

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public interface InsertDescriptionDialogListener {
        void onDialogPositiveClick(ImageRealEstate imageRealEstate);

        void onDialogNegativeClick();
    }

    private InsertDescriptionDialogFragment.InsertDescriptionDialogListener onButtonClickedListener;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to create a new instance of the fragment
     */
    public static InsertDescriptionDialogFragment newInstance(ImageRealEstate imageRealEstate) {

        InsertDescriptionDialogFragment dialogFragment = new InsertDescriptionDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(Constants.DIALOG_DESCRIPTION, imageRealEstate);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog: called!");

        if (getActivity() != null) {

            if (getArguments() != null) {
                imageRealEstate = getArguments().getParcelable(Constants.DIALOG_DESCRIPTION);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = View.inflate(getActivity(), R.layout.dialog_add_description, null);
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
                            onButtonClickedListener.onDialogNegativeClick();
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
                setImageRealEstateDescription(alertDialog);
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
            onButtonClickedListener = (InsertDescriptionDialogFragment.InsertDescriptionDialogListener) context;
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

        this.tvDescription = cardViewDescription.findViewById(R.id.text_input_layout_id).findViewById(R.id.text_input_autocomplete_text_view_id);

    }

    /**
     * Method to set the hints of all the Views.
     */
    private void setAllHints() {
        Log.d(TAG, "setAllHints: called!");

        // TODO: 23/08/2018 Use Resources instead of hardcode text
        setHint(cardViewDescription, "Description");

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
        tvDescription.setText(imageRealEstate.getDescription());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method to set the description
     */
    private void setImageRealEstateDescription(AlertDialog dialog) {
        Log.d(TAG, "setImageRealEstateDescription: called!");
        imageRealEstate.setDescription(Utils.getStringFromTextView(tvDescription));
        onButtonClickedListener.onDialogPositiveClick(imageRealEstate);
        dialog.dismiss();

    }

}
