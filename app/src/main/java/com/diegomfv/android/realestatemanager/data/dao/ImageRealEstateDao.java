package com.diegomfv.android.realestatemanager.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;

import java.util.List;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

@Dao
public interface ImageRealEstateDao {

    // -------------------
    // READ


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