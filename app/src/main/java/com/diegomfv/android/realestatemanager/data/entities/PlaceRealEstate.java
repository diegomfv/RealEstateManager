package com.diegomfv.android.realestatemanager.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

@Entity (tableName = "place")
public class PlaceRealEstate {

    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo (name = "place_id")
    private String placeId;

    private String name;

    private String address;

    private List<String> typesList;

    private double latitude;

    private double longitude;

    public PlaceRealEstate(@NonNull String id, String placeId, String name, String address, List<String> typesList, double latitude, double longitude) {
        this.id = id;
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.typesList = typesList;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getTypesList() {
        return typesList;
    }

    public void setTypesList(List<String> typesList) {
        this.typesList = typesList;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "PlaceRealEstate{" +
                "id='" + id + '\'' +
                ", placeId='" + placeId + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", typesList=" + typesList +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
