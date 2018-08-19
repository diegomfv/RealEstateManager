package com.diegomfv.android.realestatemanager.network.models.placefindplacefromtext;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Diego Fajardo on 19/08/2018.
 */
public class DebugLog {

    @SerializedName("line")
    @Expose
    private List<Object> line = null;

    public List<Object> getLine() {
        return line;
    }

    public void setLine(List<Object> line) {
        this.line = line;
    }

}