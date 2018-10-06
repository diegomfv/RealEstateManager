package com.diegomfv.android.realestatemanager.data;

/**
 * Created by Diego Fajardo on 05/08/2018.
 * Room database point of entry
 * version -> should be updated when we updateItem our database
 * exportSchema -> writes the database info to a folder
 * Room database point of entry
 * version -> should be updated when we updateItem our database
 * exportSchema -> writes the database info to a folder
 */
/** Room database point of entry
 * */

/** version -> should be updated when we updateItem our database
 * exportSchema -> writes the database info to a folder
 * */

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.diegomfv.android.realestatemanager.data.dao.AgentDao;
import com.diegomfv.android.realestatemanager.data.dao.ImageRealEstateDao;
import com.diegomfv.android.realestatemanager.data.dao.PlaceRealEstateDao;
import com.diegomfv.android.realestatemanager.data.dao.RealEstateDao;
import com.diegomfv.android.realestatemanager.data.entities.Agent;
import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.PlaceRealEstate;
import com.diegomfv.android.realestatemanager.data.entities.RealEstate;
import com.diegomfv.android.realestatemanager.data.typeconverters.AddressTypeConverter;
import com.diegomfv.android.realestatemanager.data.typeconverters.RoomsRealEstateTypeConverter;
import com.diegomfv.android.realestatemanager.data.typeconverters.StringListConverter;

/** Room cannot automatically map complex extractors like DATE. In this cases,
 * you'll need a "type converter" (See Android Architecture Components,
 * Android Development Course, UDACITY)
 * */
@Database(entities = {RealEstate.class, ImageRealEstate.class, PlaceRealEstate.class, Agent.class}, version = 1, exportSchema = false)
@TypeConverters({StringListConverter.class, AddressTypeConverter.class, RoomsRealEstateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();

    /////////////////////////////////////

    private static AppDatabase sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "real-state-manager";

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    /////////////////////////////////////

    /**
     * Abstract methods that return the DAOs
     */
    public abstract RealEstateDao realStateDao();

    public abstract ImageRealEstateDao imageRealEstateDao();

    public abstract PlaceRealEstateDao placeRealEstateDao();

    public abstract AgentDao agentDao();

    /////////////////////////////////////

    /**
     * We use the Singleton pattern.
     * Returns the instantiation of the class to
     * only one object.
     */
    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * This method is used to build the database (we left the .addCallback() method as an example
     * of an interesting possibility).
     */
    private static AppDatabase buildDatabase(final Context appContext,
                                             final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                //.allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                //Just in case a we want to do sth in the callback

                            }
                        });
                    }
                }).build();
    }

    /**
     * Method that updates the created database.
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    /**
     * Method to set the boolean value that stores if the database has been created.
     */
    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    /**
     * Getter for mIsDatabaseCreated.
     */
    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

}