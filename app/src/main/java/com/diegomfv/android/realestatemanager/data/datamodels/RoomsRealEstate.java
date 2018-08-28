package com.diegomfv.android.realestatemanager.data.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Diego Fajardo on 27/08/2018.
 */
public class RoomsRealEstate implements Parcelable {

    private static final String TAG = RoomsRealEstate.class.getSimpleName();

    int bedrooms;

    int bathrooms;

    int otherRooms;

    public RoomsRealEstate () {}

    public RoomsRealEstate(int bedrooms, int bathrooms, int otherRooms) {
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.otherRooms = otherRooms;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getOtherRooms() {
        return otherRooms;
    }

    public void setOtherRooms(int otherRooms) {
        this.otherRooms = otherRooms;
    }

    public int getTotalNumberOfRooms () {
        return bedrooms + bathrooms + otherRooms;
    }

    @Override
    public String toString() {
        return "RoomsRealEstate{" +
                "bedrooms=" + bedrooms +
                ", bathrooms=" + bathrooms +
                ", otherRooms=" + otherRooms +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bedrooms);
        dest.writeInt(this.bathrooms);
        dest.writeInt(this.otherRooms);
    }

    protected RoomsRealEstate(Parcel in) {
        this.bedrooms = in.readInt();
        this.bathrooms = in.readInt();
        this.otherRooms = in.readInt();
    }

    public static final Creator<RoomsRealEstate> CREATOR = new Creator<RoomsRealEstate>() {
        @Override
        public RoomsRealEstate createFromParcel(Parcel source) {
            return new RoomsRealEstate(source);
        }

        @Override
        public RoomsRealEstate[] newArray(int size) {
            return new RoomsRealEstate[size];
        }
    };
}
