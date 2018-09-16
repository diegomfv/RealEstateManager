package com.diegomfv.android.realestatemanager.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

@Dao
public interface ImageRealEstateDao {

    // -------------------
    // READ

    @Query("SELECT * FROM image")
    LiveData<List<ImageRealEstate>> getAllImagesRealEstateLiveData();

    @Query("SELECT * FROM image")
    List<ImageRealEstate> getAllImagesRealEstate();

    // -------------------
    // INSERT
    @Insert
    long insertImageRealEstate (ImageRealEstate imageRealEstate);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertListOfImagesRealEstate (List<ImageRealEstate> listOfImagesRealEstate);



    // -------------------
    // UPDATE


    // -------------------
    // DELETE


}
