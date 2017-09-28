package com.example.trafficsignsdetection.Communication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by helde on 28/08/2017.
 */

public class SignData {
    @SerializedName("signName")
    @Expose
    private String signName;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("orientation")
    @Expose
    private String orientation;
    @SerializedName("type")
    @Expose
    private String type;

    public SignData() {
        this.signName = "";
        this.latitude = "";
        this.longitude = "";
        this.orientation = "";
        this.type = "";
    }

    public SignData(String signName, String latitude, String longitude, String orientation, String type) {
        this.signName = signName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orientation = orientation;
        this.type = type;
    }

    public SignData(SignData c) {
        this.signName = c.getSignName();
        this.latitude = c.getLatitude();
        this.longitude = c.getLongitude();
        this.orientation = c.getOrientation();
        this.type = c.getType();
    }

    /**
     * @return the latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @return the longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{"
                + "name='" + signName + '\''
                + ", latitude='" + latitude + '\''
                + ", longitude='" + longitude + '\''
                + ", orientation='" + orientation + '\''
                + ", type='" + type + '\''
                + '}';
    }
}
