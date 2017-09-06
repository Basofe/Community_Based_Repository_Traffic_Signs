package com.example.trafficsignsdetection.Database;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by helde on 01/09/2017.
 */

public class Sign {

    @SerializedName("signName")
    @Expose
    private String signName;
    @SerializedName("orientation")
    @Expose
    private String orientation;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("type")
    @Expose
    private String type;

    public Sign(){
        this.setSignName("");
        this.setOrientation("");
        this.setLatitude("");
        this.setLongitude("");
        this.setType("");
    }

    public Sign(Sign s){
        this.signName = s.getSignName();
        this.orientation = s.getOrientation();
        this.latitude = s.getLatitude();
        this.longitude = s.getLongitude();
        this.type = s.getType();
    }


    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
