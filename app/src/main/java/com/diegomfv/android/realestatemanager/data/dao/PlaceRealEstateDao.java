package com.diegomfv.android.realestatemanager.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;

import java.util.List;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

@Dao
public interface PlaceRealEstateDao {

    // -------------------
    // READ


    // -------------------
    // INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertListOfPlaceRealEstate (List<PlaceRealEstate> listOfImagesRealEstate);

    // -------------------
    // UPDATE


    // -------------------
    // DELETE

}
