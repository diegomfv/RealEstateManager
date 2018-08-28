package com.diegomfv.android.realestatemanager.data.datamodels;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Diego Fajardo on 24/08/2018.
 */
public class AddressRealEstate implements Parcelable {

    private String street;

    private String locality;

    private String city;

    private String postcode;

    @Ignore
    public AddressRealEstate () {
        this.street = "";
        this.locality = "";
        this.city = "";
        this.postcode = "";
    }

    public AddressRealEstate(String street, String locality, String city, String postcode) {
        this.street = street;
        this.locality = locality;
        this.city = city;
        this.postcode = postcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String toString() {
        return "AddressRealEstate{" +
                "street='" + street + '\'' +
                ", locality='" + locality + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.street);
        dest.writeString(this.locality);
        dest.writeString(this.city);
        dest.writeString(this.postcode);
    }

    protected AddressRealEstate(Parcel in) {
        this.street = in.readString();
        this.locality = in.readString();
        this.city = in.readString();
        this.postcode = in.readString();
    }

    public static final Creator<AddressRealEstate> CREATOR = new Creator<AddressRealEstate>() {
        @Override
        public AddressRealEstate createFromParcel(Parcel source) {
            return new AddressRealEstate(source);
        }

        @Override
        public AddressRealEstate[] newArray(int size) {
            return new AddressRealEstate[size];
        }
    };
}

