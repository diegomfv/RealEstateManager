package com.diegomfv.android.realestatemanager.ui.fragments.handset.search;

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
import com.diegomfv.android.realestatemanager.ui.activities.SearchDetailActivity;
import com.diegomfv.android.realestatemanager.ui.base.BaseFragment;
import com.diegomfv.android.realestatemanager.util.ItemClickSupport;
import com.diegomfv.android.realestatemanager.util.Utils;
import com.diegomfv.android.realestatemanager.viewmodel.ListingsSharedViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */

public class FragmentHandsetListListingsSearch extends BaseFragment {

    private static final String TAG = FragmentHandsetListListingsSearch.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;

    //RecyclerView
    @BindView(R.id.recyclerView_listings_id)
    RecyclerView recyclerView;

    //RecyclerView Adapter
    private RVAdapterListings adapter;

    int currency;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static FragmentHandsetListListingsSearch newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentHandsetListListingsSearch();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

        Log.w(TAG, "onCreateView: foundRealEstates = " + getFoundRealEstates());

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

    private List<RealEstate> getFoundRealEstates () {
        Log.d(TAG, "getFoundRealEstates: called!");
        return getRepository().getListOfFoundRealEstates();
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
                    getFoundRealEstates(),
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
                            launchActivityWithRealEstate(adapter.getRealEstate(position), SearchDetailActivity.class);
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
