package com.example.trafficsignsdetection.Communication;

/**
 * Created by helde on 18/04/2017.
 */

public class SignInfo {

    private String name;
    private String latitude;
    private String longitude;

    public SignInfo(String name, String latitude, String longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SignInfo(SignInfo s){
        this.name = s.getName();
        this.latitude = s.getLatitude();
        this.longitude = s.getLongitude();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
