package com.diegomfv.android.realestatemanager.data.typeconverters;

import android.arch.persistence.room.TypeConverter;

import com.diegomfv.android.realestatemanager.data.entities.Image;
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
    public static List<Image> stringToImageList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Image>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ImageListToString(List<Image> someObjects) {
        return gson.toJson(someObjects);
    }


}
