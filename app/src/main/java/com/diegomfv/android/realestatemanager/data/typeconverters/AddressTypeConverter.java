package com.diegomfv.android.realestatemanager.data.typeconverters;

import android.arch.persistence.room.TypeConverter;

import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Diego Fajardo on 24/08/2018.
 */

public class AddressTypeConverter {

    private static final String TAG = AddressTypeConverter.class.getSimpleName();

    @TypeConverter
    public static AddressRealEstate fromString (String value) {
        Type listType = new TypeToken<AddressRealEstate>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromAddress (AddressRealEstate addressRealEstate) {
        Gson gson = new Gson();
        return gson.toJson(addressRealEstate);
    }


}
