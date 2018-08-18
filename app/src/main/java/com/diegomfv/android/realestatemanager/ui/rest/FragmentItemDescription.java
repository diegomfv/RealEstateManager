package com.diegomfv.android.realestatemanager.ui.rest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.viewmodel.ListingsSharedViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class FragmentItemDescription extends Fragment {

    private static final String TAG = FragmentItemDescription.class.getSimpleName();

    ///////////////////////////////

    //App
    RealEstateManagerApp app;

    @BindView(R.id.recyclerView_media_id)
    RecyclerView recyclerViewMedia;

    @BindView(R.id.textView_description_text_id)
    TextView textViewDescriptionText;

    @BindView(R.id.recyclerView_features_id)
    RecyclerView recyclerViewFeatures;

    //ViewModel
    private ListingsSharedViewModel listingsViewModel;

    //Glide
    private RequestManager glide;

    private RealEstate realEstate;

    private Unbinder unbinder;

    ////////////////////////////////

    public static FragmentItemDescription newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentItemDescription();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called!");

        View view = inflater.inflate(R.layout.fragment_item_description, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        if (getActivity() != null) {
            this.app = (RealEstateManagerApp) getActivity().getApplication();
        }

        /* Glide configuration*/
        if (getActivity() != null) {
            this.glide = Glide.with(getActivity());
        }

        /* This code runs in handsets
        * */
        if (getActivity() != null && getActivity().findViewById(R.id.fragment2_container_id) == null) {

            Bundle bundle = getArguments();
            if (bundle != null) {
                realEstate = bundle.getParcelable(Constants.GET_PARCELABLE);
            }

            if (realEstate != null) {
                textViewDescriptionText.setText(realEstate.getDescription());
            }
        }

        /* This code runs in tablets
        * */
        if (getActivity() != null && getActivity().findViewById(R.id.fragment2_container_id) != null) {

            this.listingsViewModel = this.createModel();

            this.subscribeToModel(listingsViewModel);

        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        this.unbinder.unbind();
    }

    ///////////////////////////////

    private ListingsSharedViewModel createModel () {
        Log.d(TAG, "createModel: called!");

        if (getActivity() != null) {
            ListingsSharedViewModel.Factory factory = new ListingsSharedViewModel.Factory(app);
            this.listingsViewModel = ViewModelProviders
                    .of(getActivity(), factory)
                    .get(ListingsSharedViewModel.class);

            return listingsViewModel;
        }

        return null;
    }

    private void subscribeToModel (ListingsSharedViewModel listingsViewModel) {
        Log.d(TAG, "subscribeToModel: called!");

        if (listingsViewModel != null) {

            this.listingsViewModel.getItemSelected().observe(this, new Observer<RealEstate>() {
                @Override
                public void onChanged(@Nullable RealEstate realEstate) {
                    Log.d(TAG, "onChanged: called!");

                    if (realEstate != null) {
                        textViewDescriptionText.setText(realEstate.getDescription());
                    }
                }
            });
        }
    }

}
