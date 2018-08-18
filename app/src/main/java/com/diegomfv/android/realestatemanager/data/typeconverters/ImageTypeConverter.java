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

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<ImageRealEstate> stringToImageList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<ImageRealEstate>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ImageListToString(List<ImageRealEstate> someObjects) {
        return gson.toJson(someObjects);
    }


}
