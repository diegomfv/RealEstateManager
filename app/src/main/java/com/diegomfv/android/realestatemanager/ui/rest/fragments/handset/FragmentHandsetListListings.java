package com.diegomfv.android.realestatemanager.ui.rest.fragments.handset;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */

public class FragmentHandsetListListings extends BaseFragment {

    private static final String TAG = FragmentHandsetListListings.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Unbinder unbinder;

    //RecyclerView
    @BindView(R.id.recyclerView_listings_id)
    RecyclerView recyclerView;

    //RecyclerView Adapter
    private RVAdapterListings adapter;

    //Glide
    private RequestManager glide;

    //ViewModel
    private ListingsSharedViewModel listingsSharedViewModel;

    private List<RealEstate> listOfRealEstates;

    private Map<String, Bitmap> mapOfBitmaps;

    private String listingId;

    int listingsCounter;

    int currency;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static FragmentHandsetListListings newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentHandsetListListings();
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

        this.listingsCounter = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////
        View view = inflater.inflate(R.layout.fragment_list_listings, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        /* Glide configuration*/
        if (getActivity() != null) {
            this.glide = Glide.with(getActivity());
        }

        this.configureRecyclerView();

        this.createModel();

        this.subscribeToModel(listingsSharedViewModel);

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
        if (listOfRealEstates == null) {
            return listOfRealEstates = new ArrayList<>();
        }
        return listOfRealEstates;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void createModel() {
        Log.d(TAG, "createModel: called!");

        if (getActivity() != null) {
            ListingsSharedViewModel.Factory factory = new ListingsSharedViewModel.Factory(getApp());
            this.listingsSharedViewModel = ViewModelProviders
                    .of(getActivity(), factory)
                    .get(ListingsSharedViewModel.class);
        }
    }

    private void subscribeToModel(ListingsSharedViewModel listingsViewModel) {
        Log.d(TAG, "subscribeToModel: called!");

        if (listingsViewModel != null) {
            this.listingsSharedViewModel.getObservableListOfListings().observe(this, new Observer<List<RealEstate>>() {
                @Override
                public void onChanged(@Nullable List<RealEstate> realEstates) {
                    Log.d(TAG, "onChanged: called!");
                    if (realEstates != null) {
                        listOfRealEstates = realEstates;
                        fillMapOfBitmaps(realEstates);
                        adapter.setData(realEstates);
                    }
                }
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<String, Bitmap> getMapOfBitmaps () {
        Log.d(TAG, "getMapOfBitmaps: called!");
        if (mapOfBitmaps == null) {
            return mapOfBitmaps = new HashMap<>();
        }
        return mapOfBitmaps;
    }

    private void fillMapOfBitmaps(List<RealEstate> realEstates) {
        Log.d(TAG, "fillMapOfBitmaps: called!");

        listingsCounter = realEstates.size();
        Log.i(TAG, "fillMapOfBitmaps: realEstates.size() = " + realEstates.size());

        for (int i = 0; i < realEstates.size(); i++) {

            listingId = realEstates.get(i).getId();
            Log.i(TAG, "fillMapOfBitmaps: listingId = " + listingId);

            getImageFromInternalStorage (realEstates.get(i), listingId);

        }
    }

    @SuppressLint("CheckResult")
    private void getImageFromInternalStorage(RealEstate realEstate, final String listingId) {
        Log.d(TAG, "getImageFromInternalStorage: called!");

        Single.just(getInternalStorage().readFile(getImagesDir() + realEstate.getListOfImagesIds().get(0)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<byte[]>() {
                    @Override
                    public void onSuccess(byte[] data) {
                        Log.i(TAG, "onSuccess: called!");

                        Log.i(TAG, "onSuccess: listingId = " + listingId);

                        getMapOfBitmaps().put(
                                listingId,
                                BitmapFactory.decodeByteArray(data, 0 , data.length));
                        listingsCounter--;

                        if (listingsCounter == 0) {
                            adapter.setDataBitmaps(getMapOfBitmaps());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());

                    }
                });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void configureRecyclerView() {
        Log.d(TAG, "configureRecyclerView: called!");

        if (getActivity() != null) {

            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            this.adapter = new RVAdapterListings(
                    getActivity(),
                    getListOfRealEstates(),
                    getMapOfBitmaps(),
                    glide,
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
