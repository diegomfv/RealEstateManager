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
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.util.List;

/**
 * Created by Diego Fajardo (https://github.com/diegomfv) on 14/09/2018.
 */
public class EditViewModel extends AndroidViewModel {

    private static final String TAG = EditViewModel.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final LiveData<List<ImageRealEstate>> observableListOfImagesRealEstate;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public EditViewModel(@NonNull Application application, DataRepository dataRepository) {
        super(application);
        Log.d(TAG, "EditViewModel: called!");
        observableListOfImagesRealEstate = dataRepository.getLiveDataAllImagesRealEstate();
    }

    /** Expose the LiveData so the UI can observe it.
     * */
    public LiveData<List<ImageRealEstate>> getObservableListOfImagesRealEstate () {
        return observableListOfImagesRealEstate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new EditViewModel(mApplication, mRepository);
        }
    }




}
