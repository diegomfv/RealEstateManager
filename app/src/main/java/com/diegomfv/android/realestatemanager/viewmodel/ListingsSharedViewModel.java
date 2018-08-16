package com.diegomfv.android.realestatemanager.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.data.DataRepository;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.util.List;

/**
 * Created by Diego Fajardo on 16/08/2018.
 */
public class ListingsSharedViewModel extends AndroidViewModel {

    private static final String TAG = ListingsSharedViewModel.class.getSimpleName();

    //////////////////

    private final LiveData<List<RealEstate>> observableListOfListings;

    private final MutableLiveData<RealEstate> itemSelected = new MutableLiveData<>();

    //////////////////

    public ListingsSharedViewModel(@NonNull Application application, DataRepository dataRepository) {
        super(application);
        Log.d(TAG, "ListingsSharedViewModel: called!");

        observableListOfListings = dataRepository.getObservableAllListings();

    }

    public void selectItem (RealEstate realEstate) {
        Log.d(TAG, "selectItem: called!");
        itemSelected.setValue(realEstate);

    }

    public LiveData<RealEstate> getSelected () {
        Log.d(TAG, "getSelected: called!");
        return itemSelected;
    }

    /**
     * Expose the LiveData so the UI can observe it. */
    public LiveData<List<RealEstate>> getObservableListOfListings() {
        return observableListOfListings;
    }

    ////////////////////////////////////

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final DataRepository mRepository;

        public Factory(@NonNull Application application) {
            mApplication = application;
            mRepository = ((RealEstateManagerApp) application).getRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ListingsSharedViewModel(mApplication, mRepository);
        }
    }

}
