package com.diegomfv.android.realestatemanager.data.typeconverters;

import android.arch.persistence.room.TypeConverter;

import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.diegomfv.android.realestatemanager.data.datamodels.RoomsRealEstate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Diego Fajardo on 27/08/2018.
 */
public class RoomsRealEstateTypeConverter {

    private static final String TAG = RoomsRealEstateTypeConverter.class.getSimpleName();

    @TypeConverter
    public static RoomsRealEstate fromString (String value) {
        Type listType = new TypeToken<RoomsRealEstate>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromAddress (RoomsRealEstate roomsRealEstate) {
        Gson gson = new Gson();
        return gson.toJson(roomsRealEstate);
    }
}
