package com.example.trafficsignsdetection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trafficsignsdetection.GeoLocation.Const;
import com.example.trafficsignsdetection.GeoLocation.FusedLocationSingleton;
import com.example.trafficsignsdetection.Sensors.BearingToNorthProvider;
import com.example.trafficsignsdetection.Sensors.TurnGPSOn;

/**
 * Created by helde on 21/03/2017.
 */

public class SensorActivityTest extends Activity implements SensorEventListener, BearingToNorthProvider.ChangeEventListener {

    FusedLocationSingleton fusedInstance;
    private BearingToNorthProvider mBearingProvider;

    TurnGPSOn gpsOn;

    TextView Az;
    TextView speedarinho;
    Button toggleGPS;
    Intent intent;

    String speed = "0 km/h";

    double azimuth = 0.0;
    float[] orientationVals;

    private Sensor accelerometer, magnetometer, rotationVector;
    private SensorManager SM;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_layout);

        fusedInstance = FusedLocationSingleton.getInstance();
        gpsOn = new TurnGPSOn();
        fusedInstance.startLocationUpdates();

        Az = (TextView) findViewById(R.id.textAz);
        speedarinho = (TextView) findViewById(R.id.textVelocidade);
        toggleGPS = (Button) findViewById(R.id.buttonGPS);

        orientationVals = new float[4];

        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = SM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        rotationVector = SM.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mBearingProvider = new BearingToNorthProvider(this);
        mBearingProvider.setChangeEventListener(this);

    }

    private BroadcastReceiver mLocationUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        try {
            /*Location location = (Location) intent.getParcelableExtra(Const.LBM_EVENT_LOCATION_UPDATE);
            double velocidade = 0.0;

            //Method getSpeed() gives the velocity in m/s. We need to multiple by 3600 and divide that by 1000 to convert it to km/h.
            if(location.hasSpeed())
                velocidade = Math.round((location.getSpeed()*3600/1000) * 10.0 / 10.0);

            speed = velocidade + " km/h";

            speedarinho.setText(speed);*/

        } catch (Exception e) {
            //
        }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        SM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        SM.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        SM.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_GAME);

        mBearingProvider.start();
        // start location updates
        fusedInstance.startLocationUpdates();
        // register observer for location updates
        LocalBroadcastManager.getInstance(SensorActivityTest.this).registerReceiver(mLocationUpdated,
                new IntentFilter(Const.INTENT_FILTER_LOCATION_UPDATE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SM.unregisterListener(this);

        mBearingProvider.stop();

        // stop location updates
        fusedInstance.stopLocationUpdates();
        // unregister observer
        LocalBroadcastManager.getInstance(SensorActivityTest.this).unregisterReceiver(mLocationUpdated);
    }

    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    public void onSensorChanged(SensorEvent event) {

        // It is good practice to check that we received the proper sensor event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Convert the rotation-vector to a 4x4 matrix.
            float[] mRotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(mRotationMatrix,
                    event.values);

            SensorManager.remapCoordinateSystem(mRotationMatrix,
                            SensorManager.AXIS_X, SensorManager.AXIS_Z,
                            mRotationMatrix);

            SensorManager.getOrientation(mRotationMatrix, orientationVals);

            // Optionally convert the result from radians to degrees
            orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
            orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
            orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);

            orientationVals[0] = Math.round(orientationVals[0] * 10.0 / 10.0);

            Az.setText(String.valueOf(orientationVals[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onBearingChanged(double bearing) {
        speedarinho.setText(String.valueOf(Math.round(bearing * 10.0) / 10.0));
    }
}
