package com.diegomfv.android.realestatemanager.network.models.placebynearby;

/**
 * Created by Diego Fajardo on 12/05/2018.
 */
public class LatLngForRetrofit {

    private double lat;
    private double lng;

    public LatLngForRetrofit(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override public String toString() {
        return String.format("%.7f,%.7f", lat, lng);
    }

}
