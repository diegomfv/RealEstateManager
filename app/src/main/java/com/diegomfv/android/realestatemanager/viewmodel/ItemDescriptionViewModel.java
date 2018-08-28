package com.diegomfv.android.realestatemanager.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.diegomfv.android.realestatemanager.RealEstateManagerApp;
import com.diegomfv.android.realestatemanager.data.DataRepository;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;

import java.util.List;

/**
 * Created by Diego Fajardo on 28/08/2018.
 */
public class ItemDescriptionViewModel extends AndroidViewModel {

    private static final String TAG = ItemDescriptionViewModel.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final LiveData<List<PlaceRealEstate>> observablePlacesRealEstate;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ItemDescriptionViewModel(@NonNull Application application, DataRepository dataRepository) {
        super(application);
        Log.d(TAG, "ListingsSharedViewModel: called!");
        observablePlacesRealEstate = dataRepository.getObservableAllPlacesRealEstate();
    }

    /** Expose the LiveData so the UI can observe it
     * */
    public LiveData<List<PlaceRealEstate>> getObservablePlacesRealEstate() {
        return observablePlacesRealEstate;
    }

    ////////////////////////////////////

    /** Factory to create the ViewModel with the repository available
     * */
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
            return (T) new ItemDescriptionViewModel(mApplication, mRepository);
        }
    }
}


