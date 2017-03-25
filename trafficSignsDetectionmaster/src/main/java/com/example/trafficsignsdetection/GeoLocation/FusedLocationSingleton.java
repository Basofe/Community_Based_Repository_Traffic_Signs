package com.example.trafficsignsdetection.GeoLocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by helde on 09/03/2017.
 */

public class FusedLocationSingleton implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /***********************************************************************************************
     * properties
     **********************************************************************************************/
    private static FusedLocationSingleton mInstance = null;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public final static int FAST_LOCATION_FREQUENCY = 200;
    public final static int LOCATION_FREQUENCY = 500;
    public final static int DISPLACEMENT = 1;

    /***********************************************************************************************
     * methods
     **********************************************************************************************/
    /**
     * constructor
     */
    public FusedLocationSingleton() {
        buildGoogleApiClient();
    }

    /**
     * destructor
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stopLocationUpdates();
    }

    public static FusedLocationSingleton getInstance() {
        if (null == mInstance) {
            mInstance = new FusedLocationSingleton();
        }
        return mInstance;
    }

    ///////////// 1

    /**
     * builds a GoogleApiClient
     */
    private synchronized void buildGoogleApiClient() {
        // setup googleapi client
        mGoogleApiClient = new GoogleApiClient.Builder(FusedLocationSingletonAPP.getAppContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // setup location updates
        configRequestLocationUpdate();
    }

    ///////////// 2

    /**
     * config request location update
     */
    private void configRequestLocationUpdate() {
        mLocationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_FREQUENCY)
                .setFastestInterval(FAST_LOCATION_FREQUENCY)
                .setSmallestDisplacement(DISPLACEMENT);
    }

    ///////////// 3

    /**
     * request location updates
     */
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(FusedLocationSingletonAPP.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FusedLocationSingletonAPP.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        );
    }

    /**
     * start location updates
     */
    public void startLocationUpdates() {
        // connect and force the updates
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    /**
     * removes location updates from the FusedLocationApi
     */
    public void stopLocationUpdates() {
        // stop updates, disconnect from google api
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

    }

    /**
     * get last available location
     * @return last known location
     */
    public Location getLastLocation() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        else {
            startLocationUpdates(); // start the updates
            return null;
        }
    }

    /***********************************************************************************************
     * GoogleApiClient Callbacks
     **********************************************************************************************/
    @Override
    public void onConnected(Bundle bundle) {
        // do location updates
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // connection to Google Play services was lost for some reason
        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect(); // attempt to establish a new connection
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /***********************************************************************************************
     * Location Listener Callback
     **********************************************************************************************/
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // send location in broadcast
            Intent intent = new Intent(Const.INTENT_FILTER_LOCATION_UPDATE);
            intent.putExtra(Const.LBM_EVENT_LOCATION_UPDATE, location);
            LocalBroadcastManager.getInstance(FusedLocationSingletonAPP.getAppContext()).sendBroadcast(intent);
        }
    }

}