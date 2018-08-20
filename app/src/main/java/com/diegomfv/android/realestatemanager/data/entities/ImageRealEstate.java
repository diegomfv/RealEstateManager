package com.diegomfv.android.realestatemanager.data.entities;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "image")
public class ImageRealEstate {

    @PrimaryKey
    @NonNull
    private String id;

    private String description;

    public ImageRealEstate(@NonNull String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ImageRealEstate{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
