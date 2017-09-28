package com.example.trafficsignsdetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trafficsignsdetection.Communication.RetrofitMethods;
import com.example.trafficsignsdetection.Communication.SignData;
import com.example.trafficsignsdetection.Communication.SignsData;
import com.example.trafficsignsdetection.Database.Controller;
import com.example.trafficsignsdetection.Database.Coordinate;
import com.example.trafficsignsdetection.Detection.CountDownAdapter;
import com.example.trafficsignsdetection.Detection.Detector;
import com.example.trafficsignsdetection.Detection.Sign;
import com.example.trafficsignsdetection.Detection.itemAdapter;
import com.example.trafficsignsdetection.Classification.TensorFlowImageListener;
import com.example.trafficsignsdetection.GeoLocation.Const;
import com.example.trafficsignsdetection.GeoLocation.FusedLocationSingleton;
import com.example.trafficsignsdetection.Sensors.BearingToNorthProvider;
import com.example.trafficsignsdetection.Utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.example.trafficsignsdetection.Utils.Utilities.averageImageIntensity;
import static com.example.trafficsignsdetection.Utils.Utilities.calculateShift;
import static com.example.trafficsignsdetection.Utils.Utilities.equalizeBitmap;
import static com.example.trafficsignsdetection.Utils.Utilities.getBitmapFromAsset;
import static com.example.trafficsignsdetection.Utils.Utilities.overspeed;

public class CameraActivity extends Activity implements CvCameraViewListener2, BearingToNorthProvider.ChangeEventListener{

	private FusedLocationSingleton fusedInstance;
	private TensorFlowImageListener tfPreviewListener;
	private BearingToNorthProvider mBearingProvider;
	private Controller controller;
	private Utilities utils;

	private Coordinate c;
	private SignData sign;

	//String coordinates;
	double lat = 0.0, lon = 0.0;
	String[] signRecognized = new String[2];
	//String signEq = "";
	String str = "";
	boolean flag = false;
    boolean proximityFlag = false;
	int averageIntensity = 0;

	float speed = 0.0f;
	float speedLimit = 120.0f;
	float[] orientationVals;
	double[] coords;
	float azimuth;
	float azAtual;

    ArrayList<SignData> proximityArray;

	private HashMap<Coordinate, SignData> stopMap;
	private HashMap<Coordinate, SignData> dangerMap;
	private HashMap<Coordinate, SignData> prohibitionMap;
	private HashMap<Coordinate, SignData> mandatoryMap;

	private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
	private CameraBridgeViewBase mCameraView;
	private ListView listDetectedSigns;
	private TextView textVelocity;
    private TextView proximitySign;
	private RelativeLayout listRelativeLayout;
	private CascadeClassifier cascadeClassifier = null;
	private CascadeClassifier cascadeClassifier2 = null;
	private CascadeClassifier cascadeClassifier3 = null;
	private CascadeClassifier cascadeClassifier4 = null;
	private ArrayList<Sign> listSign = new ArrayList<Sign>();
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
						cascadeClassifier3 = detector.loadCascadeFile(3);
						cascadeClassifier4 = detector.loadCascadeFile(4);
	                    break;
	                default:
	                    super.onManagerConnected(status);
	                    break;
	            }
	        }
	    };
	private Mat mRgba;
	private Mat mGray;
	private int counter = 1;

		//detector = new Detector(CameraActivity.this);
	private void Initialize(){
		//OPENCV
		mCameraView = (CameraBridgeViewBase)findViewById(R.id.mCameraView);
		listDetectedSigns = (ListView)findViewById(R.id.listView1);
		listRelativeLayout = (RelativeLayout)findViewById(R.id.listViewLayout);
		textVelocity = (TextView)findViewById(R.id.textVelocity);
		textVelocity.setText(speed + "\nkm/h");
        proximitySign = (TextView)findViewById(R.id.proximity);
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
		mBearingProvider = new BearingToNorthProvider(this);
		mBearingProvider.setChangeEventListener(this);

		//COMMUNICATION
		retrofit = new RetrofitMethods(getApplicationContext());

		//DATABASE
		controller = retrofit.getController();

		//UTILITIES
		utils = new Utilities();
        proximityArray = new ArrayList<>();
		stopMap = new HashMap<>();
		dangerMap = new HashMap<>();
		prohibitionMap = new HashMap<>();
		mandatoryMap = new HashMap<>();

		//ASYNCTASKS
		final Handler handler = new Handler();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						new uploadSignsTask().execute();
					}
				});
			}
		};
		timer.schedule(task, 60000, 600000);

        final Handler handler2 = new Handler();
        Timer timer2 = new Timer();
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                handler2.post(new Runnable() {
                    public void run() {
                        new getSignsTask().execute();
                    }
                });
            }
        };
        timer2.schedule(task2, 60000, 20000);
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
			//Log.d("OLE", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
		} else {
			//Log.d("OLE", "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}

		// start location updates
		fusedInstance.startLocationUpdates();
		// register observer for location updates
		LocalBroadcastManager.getInstance(CameraActivity.this).registerReceiver(mLocationUpdated,
				new IntentFilter(Const.INTENT_FILTER_LOCATION_UPDATE));

		mBearingProvider.start();

	}

	@Override
	protected void onPause() {
		super.onPause();

		mBearingProvider.stop();
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

				//coordinates = lat + ", " + lon;
				if(!flag){
					retrofit.getSigns(String.valueOf(lat), String.valueOf(lon));
					flag = true;
				}

				if(location.hasSpeed()){
					speed = Math.round((location.getSpeed()*3600/1000) * 10.0 / 10.0);
					if(speed > speedLimit){
						textVelocity.setText(speed + "\nkm/h");
						textVelocity.setTextColor(Color.RED);
					}
					else{
						textVelocity.setText(speed + "\nkm/h");
						textVelocity.setTextColor(Color.GREEN);
					}
				}

                if(!proximityFlag)
                    proximitySign.setText("");
                else
                    proximitySign.setText(proximityArray.get(0).getSignName());

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
		final MatOfRect signs = new MatOfRect();

		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();

		if(speed >= 0.0){
			Imgproc.equalizeHist(mGray, mGray);
			listSign = new ArrayList<Sign>();

			switch(counter){

				case 0:
					Thread prohibitionThread = new Thread(new Runnable() {
						@Override
						public void run() {
							detector.Detect(mGray, signs, cascadeClassifier);
							Rect[] prohibitionArray = signs.toArray();
							Draw(prohibitionArray, 1);
						}
					});

					Thread dangerThread = new Thread(new Runnable() {
						@Override
						public void run() {
							detector.Detect(mGray, signs, cascadeClassifier2);
							Rect[] dangerArray = signs.toArray();
							Draw(dangerArray, 2);
						}
					});

					prohibitionThread.start();
					dangerThread.start();


					try {
						prohibitionThread.join();
						dangerThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;

				case 1:
					Thread stopThread = new Thread(new Runnable() {
						@Override
						public void run() {
							detector.Detect(mGray, signs, cascadeClassifier3);
							Rect[] stopArray = signs.toArray();
							Draw(stopArray, 3);
						}
					});

					Thread mandatoryThread = new Thread(new Runnable() {
						@Override
						public void run() {
							detector.Detect(mGray, signs, cascadeClassifier4);
							Rect[] mandatoryArray = signs.toArray();
							Draw(mandatoryArray, 4);
						}
					});

					stopThread.start();
					mandatoryThread.start();

					try {
						stopThread.join();
						mandatoryThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
			}
			counterAdder();
		}

        return mRgba;
	}

	private void counterAdder(){
		counter = (counter+1)%2;
	}

	public void recognizeAndGeoreference(int type, Mat subMat, int width, int p){
        Bitmap bt = Utilities.convertMatToBitmap(subMat);
        Bitmap eq;
        double cy;
        String signType = "";
        if(type == 1){
            signType = "Prohibition";
        } else if (type == 2){
            signType = "Danger";
        } else if (type == 3){
            signType = "Stop";
        } else if (type == 4){
            signType = "Mandatory";
        }
		averageIntensity = averageImageIntensity(bt);

		if(averageIntensity < 70 || averageIntensity > 220){
			eq = equalizeBitmap(bt);
			signRecognized = tfPreviewListener.recognizeSign(eq);
			//signEq = tfPreviewListener.recognizeSign(eq);
		}
		else{
			signRecognized = tfPreviewListener.recognizeSign(bt);
		}

		Bitmap label = getBitmapFromAsset(getApplicationContext(), signRecognized[1]+".png");
		Sign.myMap.put(signType + " sign - Width "+width, label);

		azAtual = (float) (azimuth + 0.6);
		cy = calculateShift(width);
		coords = utils.distanceToCoordinateAzimuth(lat, lon, cy, 0.00005, azAtual, p);
		c = new Coordinate(lat,lon);
		sign = new SignData(signRecognized[0], String.valueOf(lat), String.valueOf(lon), String.valueOf(azAtual), signType);
	}

	public void Draw(Rect[] facesArray, int type) {
		int len = facesArray.length;
		if (len <= 0) {
			runOnUiThread(new Runnable() {
                @Override
                public void run() {
			    listRelativeLayout.setVisibility(View.GONE);
		    }});
		}

        for (int i = 0; i < len; i++){
        	final int ii = i;
            final int width = facesArray[i].width;
			final int typee = type;
			final double x = facesArray[i].tl().x;
			int p = x >= 320 ? -1:1;
        	Mat subMat;
        	subMat = mRgba.submat(facesArray[i]);
			if(typee == 1){
				recognizeAndGeoreference(typee, subMat,width, p);
				speedLimit = overspeed(signRecognized[0], speed);
				if(!prohibitionMap.containsKey(c)){
					prohibitionMap.put(c, sign);
				}

//				str = "Prohibition\n---------------------------------\nN - " + signRecognized[0];
//				utils.writeToFile(str + " - " + coordinates + " | " + coords[1] + ", " + coords[0] + " - " + azAtual
//						+ " - " + width + "\n---------------------------------\n", getApplicationContext());

//				utils.writeJson(sign, getApplicationContext());
			}
			else if(typee == 2){
                recognizeAndGeoreference(typee,subMat,width, p);
				if(!dangerMap.containsKey(c)){
					dangerMap.put(c,sign);
				}
//				str = "Danger\n---------------------------------\nN - " + signRecognized[0];
//				utils.writeToFile(str + " - " + coordinates + " | " + coords[1] + ", " + coords[0] + " - " + azAtual
//						+ " - " + width + "\n---------------------------------\n", getApplicationContext());
//				utils.writeJson(sign, getApplicationContext());
			}
			else if(typee == 3){
                recognizeAndGeoreference(typee,subMat,width, p);
				if(!stopMap.containsKey(c)){
					stopMap.put(c, sign);
				}
//				str = "Stop\n---------------------------------\nN - " + signRecognized[0];
//				utils.writeToFile(str + " - " + coordinates + " | " + coords[1] + ", " + coords[0] + " - " + azAtual
//						+ " - " + width + "\n---------------------------------\n", getApplicationContext());
//				utils.writeJson(sign, getApplicationContext());
			}
            else {
                recognizeAndGeoreference(typee,subMat,width, p);
				if(!mandatoryMap.containsKey(c)){
					mandatoryMap.put(c, sign);
				}
//				str = "Mandatory\n---------------------------------\nN - " + signRecognized[0];
//				utils.writeToFile(str + " - " + coordinates + " | " + coords[1] + ", " + coords[0] + " - " + azAtual
//						+ " - " + width + "\n---------------------------------\n", getApplicationContext());
//				utils.writeJson(sign, getApplicationContext());
            }
        	Core.rectangle(mRgba,facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 2);
        	
        	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Sign sign;
					if(typee == 1) {
						str = "N - " + signRecognized[0] + " || Intensity - " + averageIntensity;
						sign = new Sign(str, "Prohibition sign - Width " + width, System.currentTimeMillis() + 2000);
					} else if(typee == 2) {
						str = "N - " + signRecognized[0] + " || Intensity - " + averageIntensity;
						sign = new Sign(str, "Danger sign - Width " + width, System.currentTimeMillis() + 2000);
					} else if(typee == 3){
						str = "N - " + signRecognized[0] + " || Intensity - " + averageIntensity;
						sign = new Sign(str, "Stop sign - Width "+width, System.currentTimeMillis() + 2000);
					}
                    else{
						str = "N - " + signRecognized[0] + " || Intensity - " + averageIntensity;
						sign = new Sign(str, "Mandatory sign - Width "+width, System.currentTimeMillis() + 2000);
                    }
					
		        	listSign.add(sign);
					listRelativeLayout.setVisibility(View.VISIBLE);
//					CountDownAdapter adapter = new CountDownAdapter(CameraActivity.this, listSign);
					itemAdapter adapter = new itemAdapter(listSign, CameraActivity.this);
					adapter.notifyDataSetChanged();
					listDetectedSigns.setAdapter(adapter);
				}
			});
        	
        }
	}

	private class getSignsTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {

            String result = String.valueOf(azimuth);
            char index = result.charAt(0);
            int key = Character.getNumericValue(index);

            proximityArray = controller.getSignsFromOrientation(key);

            if(proximityArray.isEmpty())
                proximityFlag = false;
            else
                proximityFlag = true;

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}


	private class uploadSignsTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			ArrayList<SignData> arrayList = new ArrayList<>();

			for (SignData s : prohibitionMap.values()) {
				arrayList.add(s);
			}

			prohibitionMap.clear();

			for (SignData s : dangerMap.values()) {
				arrayList.add(s);
			}

			dangerMap.clear();

			for (SignData s : stopMap.values()) {
				arrayList.add(s);
			}

			stopMap.clear();

			for (SignData s : mandatoryMap.values()) {
				arrayList.add(s);
			}

			mandatoryMap.clear();

			if(!arrayList.isEmpty()){
				SignsData coords = new SignsData(arrayList);

				Gson gson = new Gson();
				String json = gson.toJson(coords);

				System.out.println("JSON: " + json);

				//retrofit.uploadSignArray(coords);
			}

			return "Executed";
		}

		@Override
		protected void onPostExecute(String result) {
			// might want to change "executed" for the returned string passed
			// into onPostExecute() but that is upto you
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected void onProgressUpdate(Void... values) {}
	}



	@Override
	public void onBearingChanged(double bearing) {
		azimuth = (float) Math.toRadians((int)(bearing + 360 ) % 360);
	}
}
