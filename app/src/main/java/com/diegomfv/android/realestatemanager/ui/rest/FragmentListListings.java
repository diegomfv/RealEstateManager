package com.diegomfv.android.realestatemanager.ui.rest;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterListings;
import com.diegomfv.android.realestatemanager.constants.Constants;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.ui.activities.DetailActivity;
import com.diegomfv.android.realestatemanager.ui.activities.MainActivity;
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
 * Created by Diego Fajardo on 16/08/2018.
 */
// TODO: 23/08/2018 Retain the fragment!
public class FragmentListListings extends Fragment {

    private static final String TAG = FragmentListListings.class.getSimpleName();

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

    public static FragmentListListings newInstance() {
        Log.d(TAG, "newInstance: called!");
        return new FragmentListListings();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called!");

        this.currency = 0;

        this.listingsCounter = 0;

        ////////////////////////////////////////////////////////////////////////////////////////////
        View view = inflater.inflate(R.layout.fragment_list_listings, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        /* Glide configuration*/
        if (getRootActivityRef() != null) {
            this.glide = Glide.with(getRootActivityRef());
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

    private MainActivity getRootActivityRef () {
        Log.d(TAG, "getRootActivityRef: called!");
        return (MainActivity) getActivity();
    }

    private List<RealEstate> getListOfRealEstates () {
        Log.d(TAG, "getListOfRealEstates: called");
        if (listOfRealEstates == null) {
            return listOfRealEstates = new ArrayList<>();
        }
        return listOfRealEstates;
    }

    private String getImageFilesDir () {
        Log.d(TAG, "getTemporaryFilesDir: called!");
        return getRootActivityRef().getApp().getImagesDir();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void createModel() {
        Log.d(TAG, "createModel: called!");

        if (getRootActivityRef() != null) {
            ListingsSharedViewModel.Factory factory = new ListingsSharedViewModel.Factory(getRootActivityRef().getApp());
            this.listingsSharedViewModel = ViewModelProviders
                    .of(getRootActivityRef(), factory)
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
                    listOfRealEstates = realEstates;
                    fillMapOfBitmaps();
                    adapter.setData(realEstates);
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

            Single.just(getRootActivityRef().getInternalStorage().readFile(getImageFilesDir() + listOfRealEstates.get(i).getListOfImagesIds().get(0)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<byte[]>() {
                        @Override
                        public void onSuccess(byte[] data) {
                            Log.i(TAG, "onSuccess: called!");
                            Log.i(TAG, "onSuccess: data = " + data);
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

        if (getRootActivityRef() != null) {

            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getRootActivityRef()));
            this.adapter = new RVAdapterListings(
                    getRootActivityRef(),
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

                        /* This code runs when we are using a tablet
                         * */
                        if (getRootActivityRef() != null && getRootActivityRef().findViewById(R.id.fragment2_container_id) != null) {

                            /** This does not modify the item but triggers the listener
                             * for the other fragment, which is listening (because we set
                             * a MutableLiveData with the value of the real estate)
                             * */
                            if (adapter.getRealEstate(position) != null) {
                                listingsSharedViewModel.selectItem(adapter.getRealEstate(position));
                            }
                        }

                        /* This code runs when we are using a handset
                         * */
                        if (getRootActivityRef() != null && getRootActivityRef().findViewById(R.id.fragment2_container_id) == null) {
                            launchDetailActivity(adapter.getRealEstate(position));
                        }
                    }
                });
    }

    /**
     * Launches detail activity
     * with a Parcelable (item clicked) carried by the intent
     */
    private void launchDetailActivity(RealEstate realEstate) {
        Intent intent = new Intent(getRootActivityRef(), DetailActivity.class);
        intent.putExtra(Constants.SEND_PARCELABLE, realEstate);
        startActivity(intent);
    }

}
