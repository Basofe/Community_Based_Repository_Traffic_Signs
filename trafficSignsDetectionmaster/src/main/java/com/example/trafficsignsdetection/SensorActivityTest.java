package com.example.trafficsignsdetection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Size;
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

    Camera mcamera;
    Camera.Parameters params;

    double azimuth = 0.0;
    double bearing = 0.0;
    float[] orientationVals;
    double focal_length_pix;
    Camera.Size previewSize;
    double horizontalAngleView;

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

        mcamera = Camera.open();

        previewSize = mcamera.getParameters().getPreviewSize();
        horizontalAngleView = mcamera.getParameters().getHorizontalViewAngle();
        focal_length_pix = (640 * 0.5) / Math.tan(horizontalAngleView * 0.5 * Math.PI/180);
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

        //SM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        //SM.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
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

            orientationVals[0] = SensorManager.getOrientation( mRotationMatrix, orientationVals )[0];

            azimuth = (int) ( Math.toDegrees( SensorManager.getOrientation( mRotationMatrix, orientationVals )[0] ) + 360 ) % 360;

            Az.setText(String.valueOf(orientationVals[0]) + " - " + String.valueOf(azimuth));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onBearingChanged(double bearing) {
        //speedarinho.setText(String.valueOf(Math.round(bearing * 10.0) / 10.0));
        //speedarinho.setText(String.valueOf(focal_length_pix));
        bearing = (int) (mBearingProvider.getBearing() + 360 ) % 360;

        speedarinho.setText(String.valueOf((float)Math.toRadians(bearing)) + " - " + String.valueOf(bearing));
    }
}
