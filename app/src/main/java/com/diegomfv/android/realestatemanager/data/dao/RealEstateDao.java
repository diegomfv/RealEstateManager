package com.diegomfv.android.realestatemanager.data.dao;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

/** Data Access Object
 * */

/** LiveData runs, by default, outside of the main thread.
 * We use LiveDate to observe changes in the Database; for other
 * operations such as insert, updateItem or delete we do not need
 * to observe changes in the database. For those operations,
 * we will not use LiveData and therefore we will keep using the
 * executors.
 * With LiveDate, we will get notified when there are changes
 * in the database.
 * */

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.diegomfv.android.realestatemanager.data.entities.RealEstate;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;

/** A method, annotated with @Insert can return a long.
 * This is the newly generated ID for the inserted row.
 * A method, annotated with @Update can return an int.
 * This is the number of updated rows.
 *
 * @Update will try to updateItem all your fields using the value of the primary key in a where clause.
 * If your entity is not persisted in the database yet,
 * the updateItem query will not be able to find a row and will not updateItem anything.
 *
 * You can use @Insert(onConflict = OnConflictStrategy.REPLACE). This will try
 * to insert the entity and, if there is an existing row that has the same ID value,
 * it will delete it and replace it with the entity you are trying to insert.
 * Be aware that, if you are using auto generated IDs, this means that the the resulting row will
 * have a different ID than the original that was replaced. If you want to preserve the ID,
 * then you have to come up with a custom way to do it.*/

@Dao
public interface RealEstateDao {

    // -------------------
    // READ

    @Query("SELECT * FROM realestate ORDER BY type")
    LiveData<List<RealEstate>> getAllListingsOrderedByTypeLiveData();

    @Query("SELECT * FROM realestate WHERE found=:found ORDER BY type")
    LiveData<List<RealEstate>> getAllFoundListingsLiveData(boolean found);

    @Query("SELECT * FROM realestate ORDER BY type")
    List<RealEstate> getAllListingsOrderedByType();

    @Query("SELECT * FROM realestate WHERE found=:found ORDER BY type")
    List<RealEstate> getAllFoundListings(boolean found);

    // -------------------
    // INSERT
    @Insert
    long insertRealEstate (RealEstate realEstate);

    // -------------------
    // UPDATE
    @Update (onConflict = OnConflictStrategy.REPLACE)
    int updateRealEstate (RealEstate realEstate);

    // -------------------
    // DELETE


}












