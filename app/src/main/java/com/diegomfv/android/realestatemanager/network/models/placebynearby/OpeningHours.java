package com.diegomfv.android.realestatemanager.network.models.placebynearby;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Diego Fajardo on 11/05/2018.
 */
public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;
    @SerializedName("weekday_text")
    @Expose
    private List<Object> weekdayText = null;

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public List<Object> getWeekdayText() {
        return weekdayText;
    }

    public void setWeekdayText(List<Object> weekdayText) {
        this.weekdayText = weekdayText;
    }

}