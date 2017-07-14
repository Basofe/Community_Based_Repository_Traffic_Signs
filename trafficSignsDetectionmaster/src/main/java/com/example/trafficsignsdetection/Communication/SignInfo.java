package com.example.trafficsignsdetection.Communication;

/**
 * Created by helde on 18/04/2017.
 */

public class SignInfo {

    private String nameN;
    private String nameEq;
    private String latitude;
    private String longitude;
    private String Signlatitude;
    private String Signlongitude;
    private String orientation;
    private String roiSize;

    public SignInfo(String nameN, String orientation, String latitude, String longitude){
        this.nameN = nameN;
        this.orientation = orientation;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public SignInfo(SignInfo s){
        this.nameN = s.getNameN();
        this.nameEq = s.getNameEq();
        this.latitude = s.getLatitude();
        this.longitude = s.getLongitude();
        this.Signlatitude = s.getSignlatitude();
        this.Signlongitude = s.getSignlongitude();
        this.orientation = s.getOrientation();
        this.roiSize = s.getRoiSize();
    }



    public String getNameN() {
        return this.nameN;
    }

    public void setName(String name) {
        this.nameN = name;
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

    public String getNameEq() {
        return nameEq;
    }

    public void setNameEq(String nameEq) {
        this.nameEq = nameEq;
    }

    public String getSignlatitude() {
        return Signlatitude;
    }

    public void setSignlatitude(String signlatitude) {
        Signlatitude = signlatitude;
    }

    public String getSignlongitude() {
        return Signlongitude;
    }

    public void setSignlongitude(String signlongitude) {
        Signlongitude = signlongitude;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getRoiSize() {
        return roiSize;
    }

    public void setRoiSize(String roiSize) {
        this.roiSize = roiSize;
    }
}
