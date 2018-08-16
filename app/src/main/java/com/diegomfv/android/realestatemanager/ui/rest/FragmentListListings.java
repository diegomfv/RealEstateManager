package com.diegomfv.android.realestatemanager.ui.rest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterListings;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.utils.ItemClickSupport;
import com.diegomfv.android.realestatemanager.viewmodel.ListingsSharedViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class FragmentListListings extends Fragment {

    private static final String TAG = FragmentListListings.class.getSimpleName();

    //////////////////////////////////////////////////

    private RealEstateManagerApp app;

    private Unbinder unbinder;

    //RecyclerView
    @BindView(R.id.recyclerView_listings_id)
    RecyclerView recyclerView;

    private RVAdapterListings adapter;

    //Glide
    private RequestManager glide;

    //ViewModel
    private ListingsSharedViewModel listingsViewModel;

    //////////////////////////////////////////////////

    public static FragmentListListings newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentListListings();
    }

    //////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called!");

        View view = inflater.inflate(R.layout.fragment_list_listings, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getActivity() != null) {
            app = (RealEstateManagerApp) getActivity().getApplication();
        }

        /* Glide configuration*/
        if (getActivity() != null) {
            this.glide = Glide.with(getActivity());
        }

        configureRecyclerView();
        listingsViewModel = createModel();
        subscribeToModel(listingsViewModel);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        unbinder.unbind();
    }

    /////////////////////////////////////////////////

    private ListingsSharedViewModel createModel () {
        Log.d(TAG, "createModel: called!");

        if (getActivity() != null) {
            ListingsSharedViewModel.Factory factory = new ListingsSharedViewModel.Factory(app);
            listingsViewModel = ViewModelProviders
                    .of(getActivity(), factory)
                    .get(ListingsSharedViewModel.class);

            return listingsViewModel;
        }

        return null;
    }

    private void subscribeToModel (ListingsSharedViewModel listingsViewModel) {
        Log.d(TAG, "subscribeToModel: called!");

        if (listingsViewModel != null) {

            listingsViewModel.getObservableListOfListings().observe(this, new Observer<List<RealEstate>>() {
                @Override
                public void onChanged(@Nullable List<RealEstate> realEstates) {
                    Log.d(TAG, "onChanged: called!");

                    adapter.setData(realEstates);

                }
            });

        }
    }

    private void configureRecyclerView () {
        Log.d(TAG, "configureRecyclerView: called!");

        if (getActivity() != null) {

            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            this.adapter = new RVAdapterListings(
                    getActivity(),
                    glide);
            this.recyclerView.setAdapter(this.adapter);

        }

        this.configureOnClickRecyclerView();

    }

    /** Method that configures onClick for recyclerView items
     * */
    private void configureOnClickRecyclerView () {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");

                        /** This does not modify the item but triggers the listener
                         * for the other fragment, which is listening (because we set
                         * a MutableLiveData with the value of the real estate)
                         * */
                        if (adapter.getRealEstate(position) != null) {
                            listingsViewModel.selectItem(adapter.getRealEstate(position));
                        }

                    }
                });
    }

}
