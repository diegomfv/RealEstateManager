package com.diegomfv.android.realestatemanager.network.models.placefindplacefromtext;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
public class Candidate {

    @SerializedName("place_id")
    @Expose
    private String placeId;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

}
