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
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import com.example.trafficsignsdetection.GeoLocation.Const;
import com.example.trafficsignsdetection.GeoLocation.FusedLocationSingleton;

import java.util.ArrayList;

/**
 * Created by helde on 21/03/2017.
 */

public class SensorActivityTest extends Activity implements SensorEventListener {

    FusedLocationSingleton fusedInstance;

    TextView Az;
    TextView X;
    TextView Y;
    TextView Z;
    TextView speedarinho;

    String speed = "0 km/h";

    double[] linear_acceleration = new double[3];
    double[] gravity = new double[3];
    double azimuth;  // View to draw a compass

    double interval = 0.0;
    double lastEvent = 0.0;
    double v0 = 0.0;
    double lastX = 0.0;
    double lastY = 0.0;
    double lastZ = 0.0;

    ArrayList<Double> velocities = new ArrayList<>();

    private Sensor accelerometer, magnetometer;
    private SensorManager SM;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_layout);

        fusedInstance = FusedLocationSingleton.getInstance();
        fusedInstance.startLocationUpdates();

        /*X = (TextView) findViewById(R.id.textX);
        Y = (TextView) findViewById(R.id.textY);
        Z = (TextView) findViewById(R.id.textZ);*/
        Az = (TextView) findViewById(R.id.textAz);
        speedarinho = (TextView) findViewById(R.id.textVelocidade);

        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = SM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    private void calculateVelocity(double x, double y, double z){
        long now = System.currentTimeMillis();
        interval = (now - lastEvent);
        if(interval > 100){
            lastEvent = now;
            double acceleration = x+y+z-lastX-lastY-lastZ;
            double velocity = v0 + (acceleration*(interval/(double)1000));
            //velocities.add(Math.abs(velocity));
            v0 = velocity;
            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

    private BroadcastReceiver mLocationUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        try {
            Location location = (Location) intent.getParcelableExtra(Const.LBM_EVENT_LOCATION_UPDATE);
            double velocidade = 0.0;

            if(location.hasSpeed())
                velocidade = (location.getSpeed()*3600/1000);

            speed = velocidade + " km/h";
            //Toast.makeText(getApplicationContext(), coordinates, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //
        }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        SM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        SM.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
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
        // stop location updates
        fusedInstance.stopLocationUpdates();
        // unregister observer
        LocalBroadcastManager.getInstance(SensorActivityTest.this).unregisterReceiver(mLocationUpdated);
    }

    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = - orientation[0] * 360 / (2 * 3.14159f);; // orientation contains: azimut, pitch and roll
            }
        }

        Az.setText(String.valueOf(Math.round(azimuth * 100.0) / 100.0));

        speedarinho.setText(speed);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
