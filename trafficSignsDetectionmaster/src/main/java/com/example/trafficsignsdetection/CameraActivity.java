package com.example.trafficsignsdetection;

import java.util.ArrayList;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


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
import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.trafficsignsdetection.Communication.RetrofitMethods;
import com.example.trafficsignsdetection.Communication.SignInfo;
import com.example.trafficsignsdetection.Detection.Detector;
import com.example.trafficsignsdetection.Detection.Sign;
import com.example.trafficsignsdetection.Detection.itemAdapter;
import com.example.trafficsignsdetection.Classification.TensorFlowImageListener;
import com.example.trafficsignsdetection.GeoLocation.Const;
import com.example.trafficsignsdetection.GeoLocation.FusedLocationSingleton;
import com.example.trafficsignsdetection.Utils.Utilities;

public class CameraActivity extends Activity implements CvCameraViewListener2, SensorEventListener{

	FusedLocationSingleton fusedInstance;
	private TensorFlowImageListener tfPreviewListener;
	Utilities utils;
	SignInfo signInfo;
	String coordinates;
	double lat = 0.0, lon = 0.0;

	String signRecognized = "";
	String signEq = "";
	String str = "";

	float speed = 0.0f;
	float[] orientationVals;
	double[] coords;
	float azimuth;
	float azAtual;

	private Sensor rotationVector;
	private SensorManager SM;

	private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
	private CameraBridgeViewBase mCameraView;
	private ListView listDetectedSigns;
	private RelativeLayout listRelativeLayout;
	private CascadeClassifier cascadeClassifier = null;
	private CascadeClassifier cascadeClassifier2 = null;
	private CascadeClassifier cascadeClassifier3 = null;
	private CascadeClassifier cascadeClassifier4 = null;
	private ArrayList<Sign> listSign;
	private RetrofitMethods retrofit;
	private Detector detector;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	        @Override
	        public void onManagerConnected(int status) {
	            switch (status) {
	                case LoaderCallbackInterface.SUCCESS:
	                	mCameraView.enableView();
	                	detector = new Detector(CameraActivity.this);
						cascadeClassifier = detector.loadCascadeFile(1);
						cascadeClassifier2 = detector.loadCascadeFile(2);
						//cascadeClassifier3 = detector.loadCascadeFile(3);
						//cascadeClassifier4 = detector.loadCascadeFile(4);
	                    break;
	                default:
	                    super.onManagerConnected(status);
	                    break;
	            }
	        }
	    };
	private Mat mRgba;
	private Mat mGray;
	private int counter = 0;

		//detector = new Detector(CameraActivity.this);
	private void Initialize(){
		//OPENCV
		mCameraView = (CameraBridgeViewBase)findViewById(R.id.mCameraView);
		listDetectedSigns = (ListView)findViewById(R.id.listView1);
		listRelativeLayout = (RelativeLayout)findViewById(R.id.listViewLayout);
		mCameraView.setCvCameraViewListener(this);
		mCameraView.setMaxFrameSize(640, 480);
		mCameraView.enableFpsMeter();
		listRelativeLayout.setVisibility(View.GONE);

		//FUSED LOCATION
		fusedInstance = FusedLocationSingleton.getInstance();
		fusedInstance.startLocationUpdates();

		//TENSORFLOW
		tfPreviewListener = new TensorFlowImageListener();
		tfPreviewListener.initialize(this.getAssets());

		//SENSORS
		SM = (SensorManager)getSystemService(SENSOR_SERVICE);
		rotationVector = SM.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		//UTILITIES
		utils = new Utilities();
		retrofit = new RetrofitMethods();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.camera_preview);
		orientationVals = new float[4];
		Initialize();
	}
	@Override
    public void onResume() {
        super.onResume();
		if (!OpenCVLoader.initDebug()) {
			Log.d("OLE", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
		} else {
			Log.d("OLE", "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}

		// start location updates
		fusedInstance.startLocationUpdates();
		// register observer for location updates
		LocalBroadcastManager.getInstance(CameraActivity.this).registerReceiver(mLocationUpdated,
				new IntentFilter(Const.INTENT_FILTER_LOCATION_UPDATE));

		SM.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause() {
		super.onPause();

		SM.unregisterListener(this);

		// stop location updates
		fusedInstance.stopLocationUpdates();
		// unregister observer
		LocalBroadcastManager.getInstance(CameraActivity.this).unregisterReceiver(mLocationUpdated);
	}

	/***********************************************************************************************
	 * local broadcast receiver
	 **********************************************************************************************/
	/**
	 * handle new location
	 */
	private BroadcastReceiver mLocationUpdated = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				Location location = (Location) intent.getParcelableExtra(Const.LBM_EVENT_LOCATION_UPDATE);
				lat = location.getLatitude();
				lon = location.getLongitude();

				coordinates = lat + ", " + lon;

				if(location.hasSpeed())
					speed = Math.round((location.getSpeed()*3600/1000) * 10.0 / 10.0);

			} catch (Exception e) {
				//
			}
		}
	};
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		//TODO Auto-generated method stub

		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();

		if(speed >= 0.0){
			Imgproc.equalizeHist(mGray, mGray);
			MatOfRect signs = new MatOfRect();
			listSign = new ArrayList<Sign>();

			switch(counter){

				case 1:
					detector.Detect(mGray, signs, cascadeClassifier);
					Rect[] prohibitionArray = signs.toArray();
					Draw(prohibitionArray, 1);
					break;

				case 2:
					detector.Detect(mGray, signs, cascadeClassifier2);
					Rect[] dangerArray = signs.toArray();
					Draw(dangerArray,2);
					break;

				case 3:
					/*detector.Detect(mGray, signs, cascadeClassifier3);
					Rect[] stopArray = signs.toArray();
					Draw(stopArray,3);
					break;*/

				case 4:
					/*detector.Detect(mGray, signs, cascadeClassifier4);
					Rect[] mandatoryArray = signs.toArray();
					Draw(mandatoryArray,4);
					break;*/
			}

			counterAdder();
			}
        //Core.rectangle(inputFrame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);*/
        return mRgba;
	}

	private void counterAdder(){
		if(counter > 2){
			counter = 0;
		}
		counter++;
	}

	public void Draw(Rect[] facesArray, int type){
		int len = facesArray.length;
		if(len<=0){
        	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					listRelativeLayout.setVisibility(View.GONE);
				}
			});
        	
        }
        for (int i = 0; i < len; i++){
			Bitmap bt;
			Bitmap eq;
        	final int ii = i;
			final double x = facesArray[i].tl().x;
			//double y = facesArray[i].tl().y;
            final int width = facesArray[i].width;
			final int typee = type;
			int p = x >= 320 ? -1:1;
        	Mat subMat;
        	subMat = mRgba.submat(facesArray[i]);
			if(typee == 1){
				azAtual = azimuth;
				bt = Utilities.convertMatToBitmap(subMat);
				Sign.myMap.put("Prohibition sign - Width "+width, bt);
				coords = utils.distanceToCoordinateAzimuth(lat, lon, 0.00005, 0.00005, azAtual, -1);
				eq = Utilities.equalizeBitmap(bt);
				signRecognized = tfPreviewListener.recognizeSign(bt);
				signEq = tfPreviewListener.recognizeSign(eq);
				//signInfo = new SignInfo(signRecognized, String.valueOf(coords[1]), String.valueOf(coords[0]));
				//retrofit.uploadSignInfo(signInfo);
//				str = "Prohibition\n---------------------------------\nN - " + signRecognized + " || Eq - " + signEq;
//				utils.writeToFile(str + " - " + coordinates + " | " + coords[1] + ", " + coords[0] + " - " + azAtual
//						+ "\n---------------------------------\n", getApplicationContext());
				//utils.storeImage(bt, counter++);
			}
			else if(typee == 2){
				azAtual = azimuth;
				bt = Utilities.convertMatToBitmap(subMat);
				Sign.myMap.put("Danger sign - Width "+width, bt);
				coords = utils.distanceToCoordinateAzimuth(lat, lon, 0.00005, 0.00005, azAtual, -1);
				eq = Utilities.equalizeBitmap(bt);
				signRecognized = tfPreviewListener.recognizeSign(bt);
				signEq = tfPreviewListener.recognizeSign(eq);
				//signInfo = new SignInfo(signRecognized, String.valueOf(coords[1]), String.valueOf(coords[0]));
				//retrofit.uploadSignInfo(signInfo);
//				str = "Danger\n---------------------------------\nN - " + signRecognized + " || Eq - " + signEq;
//				utils.writeToFile(str + " - " + coordinates + " | " + coords[1] + ", " + coords[0] + " - " + azAtual
//						+ "\n---------------------------------\n", getApplicationContext());
				//utils.storeImage(bt, counter++);
			}
			else if(typee == 3)
				Sign.myMap.put("Stop sign - Width "+width, Utilities.convertMatToBitmap(subMat));
            else
				Sign.myMap.put("Mandatory sign - Width "+width, Utilities.convertMatToBitmap(subMat));

        	Core.rectangle(mRgba,facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 2);
        	
        	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Sign sign;
					if(typee == 1) {
						str = "N - " + signRecognized + " || Eq - " + signEq;
						sign = new Sign(str, "Prohibition sign - Width " + width);
					} else if(typee == 2) {
						str = "N - " + signRecognized + " || Eq - " + signEq;
						sign = new Sign(str, "Danger sign - Width " + width);
					} else if(typee == 3)
						sign = new Sign("unknown", "Stop sign - Width "+width);
                    else
						sign = new Sign("unknown", "Mandatory sign - Width "+width);
		        	listSign.add(sign);
					listRelativeLayout.setVisibility(View.VISIBLE);
					itemAdapter adapter= new itemAdapter(listSign, CameraActivity.this);
					adapter.notifyDataSetChanged();
					listDetectedSigns.setAdapter(adapter);
				}
			});
        	
        }
	}



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

			// Optionally convert the result from radians to degrees
			azimuth = SensorManager.getOrientation( mRotationMatrix, orientationVals )[0];
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
