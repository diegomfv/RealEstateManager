package com.diegomfv.android.realestatemanager.ui.rest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diegomfv.android.realestatemanager.R;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class FragmentItemDescription extends Fragment {

    private static final String TAG = FragmentItemDescription.class.getSimpleName();

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

        return view;
    }



}
