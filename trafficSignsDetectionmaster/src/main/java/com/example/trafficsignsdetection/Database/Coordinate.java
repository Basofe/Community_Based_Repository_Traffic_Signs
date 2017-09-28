package com.example.trafficsignsdetection.Database;

/**
 * Created by helde on 17/07/2017.
 */

public class Coordinate {

    private double latitude;
    private double longitude;

    public Coordinate() {
        this.latitude = 0.0;
        this.longitude = 0.0;
    }

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinate(Coordinate c) {
        this.latitude = c.getLatitude();
        this.longitude = c.getLongitude();
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
