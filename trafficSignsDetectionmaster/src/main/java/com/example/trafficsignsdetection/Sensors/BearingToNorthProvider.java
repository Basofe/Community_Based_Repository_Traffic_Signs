package com.example.trafficsignsdetection.Sensors;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.trafficsignsdetection.GeoLocation.FusedLocationSingletonAPP;

/**
 * Utility class that provides bearing values to true north.
 */
public class BearingToNorthProvider implements SensorEventListener, LocationListener {
    public static final String TAG = "BearingToNorthProvider";

    /**
     * Interface definition for a callback to be invoked when the bearing changes.
     */
    public static interface ChangeEventListener {
        /**
         * Callback method to be invoked when the bearing changes.
         * @param bearing the new bearing value
         */
        void onBearingChanged(double bearing);
    }

    private final SensorManager mSensorManager;
    private final LocationManager mLocationManager;
    //private final Sensor mSensorAccelerometer;
    //private final Sensor mSensorMagneticField;
    private Sensor rotationVector;

    // some arrays holding intermediate values read from the sensors, used to calculate our azimuth
    // value

    /*private float[] mValuesAccelerometer;
    private float[] mValuesMagneticField;
    private float[] mMatrixR;
    private float[] mMatrixI;*/
    private float[] mMatrixValues;

    /**
     * minimum change of bearing (degrees) to notify the change listener
     */
    private final double mMinDiffForEvent;

    /**
     * minimum delay (millis) between notifications for the change listener
     */
    private final double mThrottleTime;

    /**
     * the change event listener
     */
    private ChangeEventListener mChangeEventListener;

    /**
     * angle to magnetic north
     */
    private AverageAngle mAzimuthRadians;

    /**
     * smoothed angle to magnetic north
     */
    private double mAzimuth = Double.NaN;

    /**
     * angle to true north
     */
    private double mBearing = Double.NaN;

    /**
     * last notified angle to true north
     */
    private double mLastBearing = Double.NaN;

    /**
     * Current GPS/WiFi location
     */
    private Location mLocation;

    /**
     * when we last dispatched the change event
     */
    private long mLastChangeDispatchedAt = -1;

    /**
     * Default constructor.
     *
     * @param context Application Context
     */
    public BearingToNorthProvider(Context context) {
        this(context, 5, 0.5, 50);
    }

    /**
     * @param context Application Context
     * @param smoothing the number of measurements used to calculate a mean for the azimuth. Set
     *                      this to 1 for the smallest delay. Setting it to 5-10 to prevents the
     *                      needle from going crazy
     * @param minDiffForEvent minimum change of bearing (degrees) to notify the change listener
     * @param throttleTime minimum delay (millis) between notifications for the change listener
     */
    public BearingToNorthProvider(Context context, int smoothing, double minDiffForEvent, int throttleTime) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        rotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        //mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //mSensorMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        /*mValuesAccelerometer = new float[3];
        mValuesMagneticField = new float[3];

        mMatrixR = new float[9];
        mMatrixI = new float[9];*/
        mMatrixValues = new float[4];

        mMinDiffForEvent = minDiffForEvent;
        mThrottleTime = throttleTime;

        mAzimuthRadians = new AverageAngle(smoothing);
    }

    //==============================================================================================
    // Public API
    //==============================================================================================

    /**
     * Call this method to start bearing updates.
     */
    public void start() {
        /*mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorMagneticField, SensorManager.SENSOR_DELAY_GAME);*/
        mSensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_GAME);

        for (final String provider : mLocationManager.getProviders(true)) {
            if (LocationManager.GPS_PROVIDER.equals(provider)
                    || LocationManager.PASSIVE_PROVIDER.equals(provider)
                    || LocationManager.NETWORK_PROVIDER.equals(provider)) {
                if (mLocation == null) {
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
                    mLocation = mLocationManager.getLastKnownLocation(provider);
                }
                mLocationManager.requestLocationUpdates(provider, 0, 100.0f, this);
            }
        }
    }

    /**
     * call this method to stop bearing updates.
     */
    public void stop() {
        //mSensorManager.unregisterListener(this, mSensorAccelerometer);
        //mSensorManager.unregisterListener(this, mSensorMagneticField);
        mSensorManager.unregisterListener(this, rotationVector);
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
        mLocationManager.removeUpdates(this);
    }

    /**
     * @return current bearing
     */
    public double getBearing()
    {
        return mBearing;
    }

    /**
     * Returns the bearing event listener to which bearing events must be sent.
     * @return the bearing event listener
     */
    public ChangeEventListener getChangeEventListener()
    {
        return mChangeEventListener;
    }

    /**
     * Specifies the bearing event listener to which bearing events must be sent.
     * @param changeEventListener the bearing event listener
     */
    public void setChangeEventListener(ChangeEventListener changeEventListener)
    {
        this.mChangeEventListener = changeEventListener;
    }

    //==============================================================================================
    // SensorEventListener implementation
    //==============================================================================================

    @Override
    public void onSensorChanged(SensorEvent event)
    {

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Convert the rotation-vector to a 4x4 matrix.
            float[] mRotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(mRotationMatrix,
                    event.values);

            SensorManager.remapCoordinateSystem(mRotationMatrix,
                    SensorManager.AXIS_X, SensorManager.AXIS_Z,
                    mRotationMatrix);

            SensorManager.getOrientation( mRotationMatrix, mMatrixValues );
            mAzimuthRadians.putValue(mMatrixValues[0]);
            //mAzimuth = Math.toDegrees(mAzimuthRadians.getAverage());
            mAzimuth = Math.toDegrees(mAzimuthRadians.getAverage());
        }

        // update mBearing
        updateBearing();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {   }

    //==============================================================================================
    // LocationListener implementation
    //==============================================================================================

    @Override
    public void onLocationChanged(Location location)
    {
        // set the new location
        this.mLocation = location;

        // update mBearing
        updateBearing();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {   }

    @Override
    public void onProviderEnabled(String s) {   }

    @Override
    public void onProviderDisabled(String s) {   }

    //==============================================================================================
    // Private Utilities
    //==============================================================================================

    private void updateBearing()
    {
        if (!Double.isNaN(this.mAzimuth)) {
            if(this.mLocation == null) {
                Log.w(TAG, "Location is NULL bearing is not true north!");
                mBearing = mAzimuth;
            } else {
                mBearing = getBearingForLocation(this.mLocation);
            }

            // Throttle dispatching based on mThrottleTime and minDiffForEvent
            if( System.currentTimeMillis() - mLastChangeDispatchedAt > mThrottleTime &&
                    (Double.isNaN(mLastBearing) || Math.abs(mLastBearing - mBearing) >= mMinDiffForEvent)) {
                mLastBearing = mBearing;
                if(mChangeEventListener != null) {
                    mChangeEventListener.onBearingChanged(mBearing);
                }
                mLastChangeDispatchedAt = System.currentTimeMillis();
            }
        }
    }

    private double getBearingForLocation(Location location)
    {
        return mAzimuth + getGeomagneticField(location).getDeclination();
    }

    private GeomagneticField getGeomagneticField(Location location)
    {
        GeomagneticField geomagneticField = new GeomagneticField(
                (float)location.getLatitude(),
                (float)location.getLongitude(),
                (float)location.getAltitude(),
                System.currentTimeMillis());
        return geomagneticField;
    }
}