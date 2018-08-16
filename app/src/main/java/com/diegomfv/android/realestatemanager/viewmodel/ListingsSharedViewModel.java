package com.diegomfv.android.realestatemanager.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class ListingsSharedViewModel extends AndroidViewModel {

    private static final String TAG = ListingsSharedViewModel.class.getSimpleName();


    public ListingsSharedViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "ListingsSharedViewModel: called!");


    }
}
