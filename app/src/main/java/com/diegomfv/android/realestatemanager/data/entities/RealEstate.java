package com.diegomfv.android.realestatemanager.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.diegomfv.android.realestatemanager.data.datamodels.AddressRealEstate;
import com.diegomfv.android.realestatemanager.data.datamodels.RoomsRealEstate;

import java.util.List;

/**
 * Created by Diego Fajardo on 05/08/2018.
 */

@Entity(tableName = "realestate")
public class RealEstate implements Parcelable {

    @PrimaryKey
    @NonNull
    private String id;

    private String type;

    @ColumnInfo(name = "surface_area")
    private float surfaceArea;

    private float price;

    @ColumnInfo(name = "rooms")
    private RoomsRealEstate rooms;

    private String description;

    @ColumnInfo(name = "images")
    private List<String> listOfImagesIds;

    private AddressRealEstate address;

    private double latitude;

    private double longitude;

    @ColumnInfo(name = "nearby_points_of_interest")
    private List<String> listOfNearbyPointsOfInterestIds;

    @ColumnInfo(name = "date_put")
    private String datePut;

    //If the property has not been sold, this would be ""
    @ColumnInfo(name = "date_sale")
    private String dateSale;

    //Email of the agent
    private String agent;

    //Found (true if a field was found using the Search Engine)
    private boolean found;

    //////////////////////////////////////////////////////

    /**
     * Used for when reading from the table
     */
    public RealEstate(@NonNull String id, String type, float surfaceArea, float price, RoomsRealEstate rooms,
                      String description, List<String> listOfImagesIds, AddressRealEstate address,
                      double latitude, double longitude, List<String> listOfNearbyPointsOfInterestIds,
                      String datePut, String dateSale, String agent, boolean found) {
        this.id = id;
        this.type = type;
        this.surfaceArea = surfaceArea;
        this.price = price;
        this.rooms = rooms;
        this.description = description;
        this.listOfImagesIds = listOfImagesIds;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.listOfNearbyPointsOfInterestIds = listOfNearbyPointsOfInterestIds;
        this.datePut = datePut;
        this.dateSale = dateSale;
        this.agent = agent;
        this.found = found;
    }

    /**
     * Used to insert a new object in the Object Relational Mapping Library
     */
    // Use the Ignore annotation so Room knows that it has to use the other constructor instead
    @Ignore
    private RealEstate(final Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.surfaceArea = builder.surfaceArea;
        this.price = builder.price;
        this.rooms = builder.rooms;
        this.description = builder.description;
        this.listOfImagesIds = builder.listOfImages;
        this.address = builder.address;
        this.listOfNearbyPointsOfInterestIds = builder.listOfNearbyPointsOfInterestIds;
        this.datePut = builder.datePut;
        this.dateSale = builder.dateSale;
        this.agent = builder.agent;
        this.found = builder.found;

    }

    @Ignore
    //Copy Constructor
    public RealEstate(RealEstate other) {
        this.id = other.id;
        this.type = other.type;
        this.surfaceArea = other.surfaceArea;
        this.price = other.price;
        this.rooms = other.rooms;
        this.description = other.description;
        this.listOfImagesIds = other.listOfImagesIds;
        this.address = other.address;
        this.latitude = other.latitude;
        this.longitude = other.longitude;
        this.listOfNearbyPointsOfInterestIds = other.listOfNearbyPointsOfInterestIds;
        this.datePut = other.datePut;
        this.dateSale = other.dateSale;
        this.agent = other.agent;
        this.found = other.found;
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

    public float getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(float surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public RoomsRealEstate getRooms() {
        return rooms;
    }

    public void setRooms(RoomsRealEstate roomsRealEstate) {
        this.rooms = roomsRealEstate;
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

    public AddressRealEstate getAddress() {
        return address;
    }

    public void setAddress(AddressRealEstate address) {
        this.address = address;
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

    public List<String> getListOfNearbyPointsOfInterestIds() {
        return listOfNearbyPointsOfInterestIds;
    }

    public void setListOfNearbyPointsOfInterestIds(List<String> listOfNearbyPointsOfInterest) {
        this.listOfNearbyPointsOfInterestIds = listOfNearbyPointsOfInterest;
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

    public boolean getFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public static class Builder {

        private String id;
        private String type;
        private float surfaceArea;
        private float price;
        private RoomsRealEstate rooms;
        private int bedrooms;
        private int bathrooms;
        private int otherRooms;
        private String description;
        private List<String> listOfImages;
        private AddressRealEstate address;
        private double latitude;
        private double longitude;
        private List<String> listOfNearbyPointsOfInterestIds;
        private String datePut;
        private String dateSale;
        private String agent;
        private boolean found;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setSurfaceArea(float surfaceArea) {
            this.surfaceArea = surfaceArea;
            return this;
        }

        public Builder setPrice(float price) {
            this.price = price;
            return this;
        }

        public Builder setBedrooms(int bedrooms) {
            this.bedrooms = bedrooms;
            return this;
        }

        public Builder setBathrooms(int bathrooms) {
            this.bathrooms = bathrooms;
            return this;
        }

        public Builder setOtherRooms(int otherRooms) {
            this.otherRooms = otherRooms;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setImages(List<String> listOfImages) {
            this.listOfImages = listOfImages;
            return this;
        }

        public Builder setAddress(AddressRealEstate address) {
            this.address = address;
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setNearbyPointsOfInterestIds(List<String> listOfNearbyPointsOfInterestIds) {
            this.listOfNearbyPointsOfInterestIds = listOfNearbyPointsOfInterestIds;
            return this;
        }

        public Builder setDatePut(String datePut) {
            this.datePut = datePut;
            return this;
        }

        public Builder setDateSale(String dateSale) {
            this.dateSale = dateSale;
            return this;
        }

        public Builder setAgent(String agent) {
            this.agent = agent;
            return this;
        }

        public Builder setFound(boolean found) {
            this.found = found;
            return this;
        }

        public RealEstate build() {
            this.rooms = new RoomsRealEstate(bedrooms, bathrooms, otherRooms);
            return new RealEstate(this);
        }
    }

    @Override
    public String toString() {
        return "RealEstate{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", surfaceArea=" + surfaceArea +
                ", price=" + price +
                ", rooms=" + rooms +
                ", description='" + description + '\'' +
                ", listOfImagesIds=" + listOfImagesIds +
                ", address=" + address +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", listOfNearbyPointsOfInterestIds=" + listOfNearbyPointsOfInterestIds +
                ", datePut='" + datePut + '\'' +
                ", dateSale='" + dateSale + '\'' +
                ", agent='" + agent + '\'' +
                ", found=" + found +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeFloat(this.surfaceArea);
        dest.writeFloat(this.price);
        dest.writeParcelable(this.rooms, flags);
        dest.writeString(this.description);
        dest.writeStringList(this.listOfImagesIds);
        dest.writeParcelable(this.address, flags);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeStringList(this.listOfNearbyPointsOfInterestIds);
        dest.writeString(this.datePut);
        dest.writeString(this.dateSale);
        dest.writeString(this.agent);
        dest.writeByte(this.found ? (byte) 1 : (byte) 0);
    }

    protected RealEstate(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        this.surfaceArea = in.readFloat();
        this.price = in.readFloat();
        this.rooms = in.readParcelable(RoomsRealEstate.class.getClassLoader());
        this.description = in.readString();
        this.listOfImagesIds = in.createStringArrayList();
        this.address = in.readParcelable(AddressRealEstate.class.getClassLoader());
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.listOfNearbyPointsOfInterestIds = in.createStringArrayList();
        this.datePut = in.readString();
        this.dateSale = in.readString();
        this.agent = in.readString();
        this.found = in.readByte() != 0;
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
