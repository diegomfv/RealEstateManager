package com.diegomfv.android.realestatemanager.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

@Dao
public interface PlaceRealEstateDao {

    // -------------------
    // READ

    @Query("SELECT * FROM place ORDER BY id")
    LiveData<List<PlaceRealEstate>> getAllPlacesRealEstateLiveData();

    @Query("SELECT * FROM place ORDER BY id")
    List<PlaceRealEstate> getAllPlacesRealEstate();

    // -------------------
    // INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertListOfPlaceRealEstate (List<PlaceRealEstate> listOfImagesRealEstate);

    ;

    // -------------------
    // UPDATE


    // -------------------
    // DELETE

}
