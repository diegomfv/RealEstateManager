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
 * Created by Diego Fajardo on 23/08/2018.
 */

/** This ViewModel is basically similar to ListingsSharedViewModel
 * */
public class PositionViewModel extends AndroidViewModel {

    private static final String TAG = PositionViewModel.class.getSimpleName();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final LiveData<List<RealEstate>> observableListOfListings;

    private final MutableLiveData<RealEstate> itemSelected = new MutableLiveData<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public PositionViewModel(@NonNull Application application, DataRepository dataRepository) {
        super(application);
        Log.d(TAG, "ListingsSharedViewModel: called!");
        observableListOfListings = dataRepository.getLiveDataAllListings();
    }

    /** This method fills the MutableLiveData with the information from
     * the item clicked in FragmentItemDescription. When this gets modified,
     * it automatically triggers the observer via getItemSelected()
     * */
    public void selectItem (RealEstate realEstate) {
        Log.d(TAG, "selectItem: called!");
        itemSelected.setValue(realEstate);
    }

    /** Returns the MutableLiveDataObject which will be used to display
     * data in the FragmentItemDescription
     * */
    public LiveData<RealEstate> getItemSelected() {
        Log.d(TAG, "getItemSelected: called!");
        return itemSelected;
    }

    /** Expose the LiveData so the UI can observe it
     * */
    public LiveData<List<RealEstate>> getObservableListOfListings() {
        return observableListOfListings;
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
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new PositionViewModel(mApplication, mRepository);
        }
    }
}
