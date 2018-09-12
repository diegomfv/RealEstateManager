package com.diegomfv.android.realestatemanager.ui.fragments.handset.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterListings;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.activities.DetailActivity;
import com.diegomfv.android.realestatemanager.ui.activities.EditListingActivity;
import com.diegomfv.android.realestatemanager.ui.activities.MainActivity;
import com.diegomfv.android.realestatemanager.ui.base.BaseFragment;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.Utils;
import com.diegomfv.android.realestatemanager.viewmodel.ListingsSharedViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */

// TODO: 12/09/2018 SOLD label touches price label
public class FragmentHandsetListListingsMain extends BaseFragment {

    private static final String TAG = FragmentHandsetListListingsMain.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;

    //RecyclerView
    @BindView(R.id.recyclerView_listings_id)
    RecyclerView recyclerView;

    //RecyclerView Adapter
    private RVAdapterListings adapter;

    private List<RealEstate> listOfRealEstates;

    int currency;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static FragmentHandsetListListingsMain newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentHandsetListListingsMain();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: 27/08/2018 Check this!
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called!");

        if (getActivity() != null) {
            this.currency = Utils.readCurrentCurrencyShPref(getActivity());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        View view = inflater.inflate(R.layout.fragment_list_listings, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        this.configureRecyclerView();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        this.unbinder.unbind();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<RealEstate> getListOfRealEstates () {
        Log.d(TAG, "getListOfRealEstates: called");
        if (getActivity() != null) {
            if (listOfRealEstates == null) {
                return listOfRealEstates = ((MainActivity) getActivity()).getListOfRealEstates();
            }
            return listOfRealEstates;
        }
        return listOfRealEstates = new ArrayList<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");

        if (getActivity() != null) {

            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            this.adapter = new RVAdapterListings(
                    getActivity(),
                    getRepository(),
                    getInternalStorage(),
                    getImagesDir(),
                    getListOfRealEstates(),
                    getGlide(),
                    currency);
            this.recyclerView.setAdapter(this.adapter);

        }
        this.configureOnClickRecyclerView();
    }

    /**
     * Method that configures onClick for recyclerView items
     */
    private void configureOnClickRecyclerView() {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");

                        /* This code runs when we are using a handset
                         * */
                        if (getActivity() != null) {

                            if (((MainActivity)getActivity()).getEditModeActive()) {
                                launchActivityWithRealEstate(adapter.getRealEstate(position), EditListingActivity.class);

                            } else {
                                launchActivityWithRealEstate(adapter.getRealEstate(position), DetailActivity.class);
                            }
                        }
                    }
                });
    }

    /**
     * Launches an activity
     * with a Parcelable (item clicked) carried by the intent
     */
    private void launchActivityWithRealEstate(RealEstate realEstate, Class <? extends AppCompatActivity> activity) {
        Intent intent = new Intent(getActivity(), activity);
        intent.putExtra(Constants.SEND_PARCELABLE, realEstate);
        startActivity(intent);
    }

}
