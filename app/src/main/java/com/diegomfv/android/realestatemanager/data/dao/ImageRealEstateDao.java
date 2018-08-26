package com.diegomfv.android.realestatemanager.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;

import java.util.List;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

@Dao
public interface ImageRealEstateDao {

    // -------------------
    // READ

    @Query("SELECT * FROM realestate ORDER BY type")
    LiveData<List<ImageRealEstate>> getAllImagesRealEstateLiveData();

    // -------------------
    // INSERT
    @Insert
    long insertImageRealEstate (ImageRealEstate imageRealEstate);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long[] insertListOfImagesRealEstate (List<ImageRealEstate> listOfImagesRealEstate);

    // -------------------
    // UPDATE


    // -------------------
    // DELETE


}
