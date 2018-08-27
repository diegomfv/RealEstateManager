package com.diegomfv.android.realestatemanager.ui.rest.fragments.tablet;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.activities.MainActivity;
import com.diegomfv.android.realestatemanager.ui.base.BaseFragment;
import com.diegomfv.android.realestatemanager.utils.ItemClickSupport;
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
 * Created by Diego Fajardo on 27/08/2018.
 */
// TODO: 27/08/2018 Room does not work
public class FragmentTabletListListings extends BaseFragment {

    private static final String TAG = FragmentTabletListListings.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

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

    private Unbinder unbinder;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static FragmentTabletListListings newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentTabletListListings();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called!");

        if (getActivity() != null) {
            MainActivity mainActivityRef = (MainActivity) getActivity();
            this.currency = mainActivityRef.getCurrency();
            this.glide = Glide.with(getActivity());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        View view = inflater.inflate(R.layout.fragment_list_listings, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        if (getActivity() != null) {
            this.listingsSharedViewModel = this.createModel();
            this.subscribeToModel(listingsSharedViewModel);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        unbinder.unbind();
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

    private ListingsSharedViewModel createModel () {
        Log.d(TAG, "createModel: called!");

        if (getActivity() != null) {
            ListingsSharedViewModel.Factory factory = new ListingsSharedViewModel.Factory(getApp());
            this.listingsSharedViewModel = ViewModelProviders
                    .of(getActivity(), factory)
                    .get(ListingsSharedViewModel.class);
            return listingsSharedViewModel;
        }
        return null;
    }

    private void subscribeToModel (ListingsSharedViewModel listingsSharedViewModel) {
        Log.d(TAG, "subscribeToModel: called!");

        if (listingsSharedViewModel != null) {
            this.listingsSharedViewModel.getObservableListOfListings().observe(this, new Observer<List<RealEstate>>() {
                        @Override
                        public void onChanged(@Nullable List<RealEstate> realEstates) {
                            Log.d(TAG, "onChanged: called!");
                            if (realEstates != null && realEstates.size() > 0) {
                                listOfRealEstates = realEstates;
                                fillMapOfBitmaps();
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

    @SuppressLint("CheckResult")
    private void fillMapOfBitmaps() {
        Log.d(TAG, "fillMapOfBitmaps: called!");

        listingsCounter = listOfRealEstates.size();

        for (int i = 0; i < listOfRealEstates.size() ; i++) {

            listingId = listOfRealEstates.get(i).getId();

            Single.just(getInternalStorage().readFile(getImagesDir() + listOfRealEstates.get(i).getListOfImagesIds().get(0)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<byte[]>() {
                        @Override
                        public void onSuccess(byte[] data) {
                            Log.i(TAG, "onSuccess: called!");

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

                        if (getActivity() != null) {

                            /** This does not modify the item but triggers the listener
                             * for the other fragment, which is listening (because we set
                             * a MutableLiveData with the value of the real estate)
                             * */
                            if (adapter.getRealEstate(position) != null) {
                                listingsSharedViewModel.selectItem(adapter.getRealEstate(position));
                            }
                        }
                    }
                });
    }
}
