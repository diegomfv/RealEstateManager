package com.diegomfv.android.realestatemanager.ui.rest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diegomfv.android.realestatemanager.R;
import com.diegomfv.android.realestatemanager.adapters.RVAdapterListings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class FragmentListListings extends Fragment {

    private static final String TAG = FragmentListListings.class.getSimpleName();

    //////////////////////////////////////////////////

    private Unbinder unbinder;

    //RecyclerView
    @BindView(R.id.recyclerView_listings_id)
    RecyclerView recyclerView;

    private RVAdapterListings adapter;
    private RecyclerView.LayoutManager mLayoutManager;

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






        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: called!");
        unbinder.unbind();
    }
}
