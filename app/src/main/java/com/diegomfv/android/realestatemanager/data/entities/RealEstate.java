package com.diegomfv.android.realestatemanager.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

@Entity (tableName = "realestate")
public class RealEstate implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;

    private String type;

    @ColumnInfo(name = "surface_area")
    private int surfaceArea;

    private int price;

    @ColumnInfo(name = "number_or_rooms")
    private int numberOfRooms;

    private String description;

    @ColumnInfo(name = "images")
    private List<String> listOfImagesIds;

    private String address;

    @ColumnInfo(name = "nearby_points_of_interest")
    private List<String> listOfNearbyPointsOfInterestIds;

    //true: available; false: sold
    private boolean status;

    @ColumnInfo(name = "date_put")
    private String datePut;

    //If the propery has not been sold, this would be ""
    @ColumnInfo(name = "date_sale")
    private String dateSale;

    //email of the agent
    private String agent;

    //////////////////////////////////////////////////////

    /** Used for when reading from the table
     * */
    public RealEstate(String id, String type, int surfaceArea, int price, int numberOfRooms, String description,
                      List<String> listOfImagesIds, String address, boolean status, List<String> listOfNearbyPointsOfInterestIds,
                      String datePut, String dateSale, String agent) {
        this.id = id;
        this.type = type;
        this.surfaceArea = surfaceArea;
        this.price = price;
        this.numberOfRooms = numberOfRooms;
        this.description = description;
        this.listOfImagesIds = listOfImagesIds;
        this.address = address;
        this.status = status;
        this.listOfNearbyPointsOfInterestIds = listOfNearbyPointsOfInterestIds;
        this.datePut = datePut;
        this.dateSale = dateSale;
        this.agent = agent;
    }

    /** Used to insert a new object in the Object Relational Mapping Library
     * */
    // Use the Ignore annotation so Room knows that it has to use the other constructor instead
    @Ignore
    private RealEstate(final Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.surfaceArea = builder.surfaceArea;
        this.price = builder.price;
        this.numberOfRooms = builder.numberOfRooms;
        this.description = builder.description;
        this.listOfImagesIds = builder.listOfImages;
        this.address = builder.address;
        this.listOfNearbyPointsOfInterestIds = builder.listOfNearbyPointsOfInterestIds;
        this.status = builder.status;
        this.datePut = builder.datePut;
        this.dateSale = builder.dateSale;
        this.agent = builder.agent;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(int surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public int getPrice () { return price; }

    public void setPrice (int price) { this.price = price; }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getListOfImagesIds() {
        return listOfImagesIds;
    }

    public void setListOfImagesIds(List<String> listOfImagesIds) {
        this.listOfImagesIds = listOfImagesIds;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getListOfNearbyPointsOfInterestIds() {
        return listOfNearbyPointsOfInterestIds;
    }

    public void setListOfNearbyPointsOfInterestIds(List<String> listOfNearbyPointsOfInterest) {
        this.listOfNearbyPointsOfInterestIds = listOfNearbyPointsOfInterest;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDatePut() {
        return datePut;
    }

    public void setDatePut(String datePut) {
        this.datePut = datePut;
    }

    public String getDateSale() {
        return dateSale;
    }

    public void setDateSale(String dateSale) {
        this.dateSale = dateSale;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public static class Builder {

        private String id;
        private String type;
        private int surfaceArea;
        private int price;
        private int numberOfRooms;
        private String description;
        private List<String> listOfImages;
        private String address;
        private List<String> listOfNearbyPointsOfInterestIds;
        private boolean status;
        private String datePut;
        private String dateSale;
        private String agent;

        public Builder setId (String id) {
            this.id = id;
            return this;
        }

        public Builder setType (String type) {
            this.type = type;
            return this;
        }

        public Builder setSurfaceArea (int surfaceArea) {
            this.surfaceArea = surfaceArea;
            return this;
        }

        public Builder setPrice (int price) {
            this.price = price;
            return this;
        }

        public Builder setNumberOfRooms (int numberOfRooms) {
            this.numberOfRooms = numberOfRooms;
            return this;
        }

        public Builder setDescription (String description) {
            this.description = description;
            return this;
        }

        public Builder setImages (List<String> listOfImages) {
            this.listOfImages = listOfImages;
            return this;
        }

        public Builder setAddress (String address) {
            this.address = address;
            return this;
        }

        public Builder setNearbyPointsOfInterestIds (List<String> listOfNearbyPointsOfInterestIds){
            this.listOfNearbyPointsOfInterestIds = listOfNearbyPointsOfInterestIds;
            return this;
        }

        public Builder setStatus (boolean status) {
            this.status = status;
            return this;
        }

        public Builder setDatePut (String datePut) {
            this.datePut = datePut;
            return this;
        }

        public Builder setDateSale (String dateSale) {
            this.dateSale = dateSale;
            return this;
        }

        public Builder setAgent (String agent) {
            this.agent = agent;
            return this;
        }

        public RealEstate build() {
            return new RealEstate(this);
        }

    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeInt(this.surfaceArea);
        dest.writeInt(this.price);
        dest.writeInt(this.numberOfRooms);
        dest.writeString(this.description);
        dest.writeStringList(this.listOfImagesIds);
        dest.writeString(this.address);
        dest.writeStringList(this.listOfNearbyPointsOfInterestIds);
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeString(this.datePut);
        dest.writeString(this.dateSale);
        dest.writeString(this.agent);
    }

    protected RealEstate(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        this.surfaceArea = in.readInt();
        this.price = in.readInt();
        this.numberOfRooms = in.readInt();
        this.description = in.readString();
        this.listOfImagesIds = in.createStringArrayList();
        this.address = in.readString();
        this.listOfNearbyPointsOfInterestIds = in.createStringArrayList();
        this.status = in.readByte() != 0;
        this.datePut = in.readString();
        this.dateSale = in.readString();
        this.agent = in.readString();
    }

    public static final Creator<RealEstate> CREATOR = new Creator<RealEstate>() {
        @Override
        public RealEstate createFromParcel(Parcel source) {
            return new RealEstate(source);
        }

        @Override
        public RealEstate[] newArray(int size) {
            return new RealEstate[size];
        }
    };
}
