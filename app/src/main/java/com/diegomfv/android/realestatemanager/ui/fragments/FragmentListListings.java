package com.diegomfv.android.realestatemanager.ui.fragments;

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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterListings;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.activities.DetailActivity;
import com.diegomfv.android.realestatemanager.ui.activities.EditListingActivity;
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
public class FragmentListListings extends BaseFragment {

    private static final String TAG = FragmentListListings.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //RecyclerView
    @BindView(R.id.recyclerView_listings_id)
    RecyclerView recyclerView;

    //RecyclerView Adapter
    private RVAdapterListings adapter;

    private List<RealEstate> listOfRealEstates;

    private ListingsSharedViewModel listingsSharedViewModel;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    int currency;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /** Method that returns
     * an instance of the Fragment
     * */
    public static FragmentListListings newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentListListings();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called!");

        if (getActivity() != null) {
            this.currency = Utils.readCurrentCurrencyShPref(getRootMainActivity());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        View view = inflater.inflate(R.layout.fragment_list_listings, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        this.createModel();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        this.unbinder.unbind();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Getter for listOfRealEstates
     */
    private List<RealEstate> getListOfRealEstates() {
        Log.d(TAG, "getListOfRealEstates: called");
        if (listOfRealEstates == null) {
            return listOfRealEstates = new ArrayList<>();
        }
        return listOfRealEstates;
    }

    /**
     * Setter for listOfRealEstates
     */
    private void setListOfRealEstates(List<RealEstate> list) {
        Log.d(TAG, "setListOfRealEstates: called!");
        this.listOfRealEstates = list;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Method that configures
     * the RecyclerView
     */
    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");

        if (getRootMainActivity() != null) {

            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getRootMainActivity()));
            this.adapter = new RVAdapterListings(
                    getRootMainActivity(),
                    getRepository(),
                    getInternalStorage(),
                    getImagesDir(),
                    getListOfRealEstates(),
                    getGlide(),
                    currency);

            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getRootMainActivity(), R.anim.layout_animation_fall_down);
            recyclerView.setLayoutAnimation(animation);

            this.recyclerView.setAdapter(this.adapter);

            this.configureOnClickRecyclerView();

        }
    }

    /**
     * Method that configures onClick
     * for recyclerView items
     */
    private void configureOnClickRecyclerView() {
        Log.d(TAG, "configureOnClickRecyclerView: called!");

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "onItemClicked: item(" + position + ") clicked!");

                        if (getRootMainActivity() != null) {

                            if ((getRootMainActivity()).getEditModeActive()) {
                                /* If editMode is ACTIVE, we launch EditListingActivity when an item is clicked. It does
                                 * not matter whether we are in a handset or a tablet
                                 * */
                                launchActivityWithRealEstate(adapter.getRealEstate(position), EditListingActivity.class);

                            } else {
                                /* If editMode is NOT ACTIVE, the functionality varies depending on if the user is using
                                 * a handset or a tablet.
                                 * */
                                if (getRootMainActivity().getDeviceIsHandset()) {
                                    /* This code runs when we are using a handset. The app displays information about the
                                     * listing in DetailActivity
                                     * */
                                    launchActivityWithRealEstate(adapter.getRealEstate(position), DetailActivity.class);

                                } else {
                                    /* This code runs when we are using a tablet. The app displays information about the listing
                                     * using the other fragment. Then information transference is done via a the shared ViewModel
                                     * */
                                    listingsSharedViewModel.selectItem(adapter.getRealEstate(position));
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Method that creates the model
     * to display the necessary information
     */
    private void createModel() {
        Log.d(TAG, "createModel: called!");

        if (getRootMainActivity() != null) {

            ListingsSharedViewModel.Factory factory = new ListingsSharedViewModel.Factory(getApp());
            this.listingsSharedViewModel = ViewModelProviders
                    .of(getRootMainActivity(), factory)
                    .get(ListingsSharedViewModel.class);

            subscribeToModel();
        }
    }

    /**
     * Method to subscribe to model. Depending on mainMenu variable (see MainActivity) we show
     * some information or another ("mainMenu = true" displays all the listings in the database
     * whereas "mainMenu = false" displays all the found articles).
     */
    private void subscribeToModel() {
        Log.d(TAG, "subscribeToModel: called!");

        if (listingsSharedViewModel != null) {

            if ((getRootMainActivity()).getMainMenu()) {
                Log.w(TAG, "subscribeToModel: mainMenu = true");

                /* If we are in the main menu, we load all the articles
                * */
                this.listingsSharedViewModel.getObservableListOfListings().observe(this, new Observer<List<RealEstate>>() {
                    @Override
                    public void onChanged(@Nullable List<RealEstate> realEstates) {
                        Log.d(TAG, "onChanged: called!");
                        if (realEstates != null && realEstates.size() > 0) {
                            setListOfRealEstates(realEstates);
                            adapter.setData(getListOfRealEstates());

                            /* We load the first item automatically in case we are in a tablet
                            * */
                            listingsSharedViewModel.selectItem(realEstates.get(0));
                        }
                    }
                });

            } else {
                Log.w(TAG, "subscribeToModel: mainMenu = false");

                /* If we are in the search menu, we load the found articles
                 * */

                this.listingsSharedViewModel.getObservableListOfFoundArticles().observe(this, new Observer<List<RealEstate>>() {
                    @Override
                    public void onChanged(@Nullable List<RealEstate> realEstates) {
                        Log.d(TAG, "onChanged: called!");
                        if (realEstates != null && realEstates.size() > 0) {
                            setListOfRealEstates(realEstates);
                            adapter.setData(getListOfRealEstates());

                            /* We load the first item automatically in case we are in a tablet
                             * */
                            listingsSharedViewModel.selectItem(realEstates.get(0));
                        }
                    }
                });
            }
            this.configureRecyclerView();
        }
    }

    /**
     * Launches an activity
     * with a Parcelable (item clicked) carried by the intent
     */
    private void launchActivityWithRealEstate(RealEstate realEstate, Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(getRootMainActivity(), activity);
        intent.putExtra(Constants.SEND_PARCELABLE, realEstate);
        startActivity(intent);
    }

}
