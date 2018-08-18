package com.diegomfv.android.realestatemanager.data.typeconverters;

import android.arch.persistence.room.TypeConverter;
import android.media.Image;

import com.diegomfv.android.realestatemanager.data.entities.ImageRealEstate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */
public class ImageTypeConverter {

    private static final String TAG = ImageTypeConverter.class.getSimpleName();

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<String> stringToImageIdsList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ImageIdsListToString(List<String> someObjects) {
        return gson.toJson(someObjects);
    }


}
