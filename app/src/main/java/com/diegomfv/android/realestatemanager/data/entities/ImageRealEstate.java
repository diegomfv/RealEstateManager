package com.diegomfv.android.realestatemanager.data.entities;

/**
 * Created by Diego Fajardo on 18/08/2018.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "image")
public class ImageRealEstate implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.description);
    }

    protected ImageRealEstate(Parcel in) {
        this.id = in.readString();
        this.description = in.readString();
    }

    public static final Creator<ImageRealEstate> CREATOR = new Creator<ImageRealEstate>() {
        @Override
        public ImageRealEstate createFromParcel(Parcel source) {
            return new ImageRealEstate(source);
        }

        @Override
        public ImageRealEstate[] newArray(int size) {
            return new ImageRealEstate[size];
        }
    };
}
