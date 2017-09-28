package com.example.trafficsignsdetection.Sensors;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by hp on 26/03/2017.
 */

public class TurnGPSOn {

    public TurnGPSOn(){}

    LocationManager locationManager ;

    boolean GpsStatus = false;

    public boolean CheckGpsStatus(Context context){

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return GpsStatus;
    }

}
