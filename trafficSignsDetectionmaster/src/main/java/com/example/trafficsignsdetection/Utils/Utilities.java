package com.example.trafficsignsdetection.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.Core.mean;
import static org.opencv.core.Core.merge;
import static org.opencv.core.Core.normalize;
import static org.opencv.core.Core.split;
import static org.opencv.imgproc.Imgproc.INTER_AREA;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.equalizeHist;
import static org.opencv.imgproc.Imgproc.resize;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.trafficsignsdetection.Communication.SignData;
import com.example.trafficsignsdetection.Database.Sign;
import com.google.gson.Gson;
//import android.util.Log;

public class Utilities {

	private static final String TAG = "UTILITIES";

	public Utilities(){}

	public static Bitmap convertMatToBitmap(Mat src){
		Mat dst = new Mat();

		resize(src, dst, new Size(32,32), 0, 0, INTER_AREA); //INTER_AREA é mais lento mas melhor
		Bitmap bm = Bitmap.createBitmap(dst.cols(),
				dst.rows(),Bitmap.Config.ARGB_4444); //ARGB_8888
        Utils.matToBitmap(dst, bm);
        //Imgproc.cvtColor(src, dst, code, dstCn)
        return bm;
	}

	public static String getRealPathFromURI(Uri contentURI,Activity activity) {
	    String path;
	    Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) { // Source is Dropbox or other similar local file path
	        path = contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        path = cursor.getString(idx);
	        cursor.close();
	    }
	    return path;
	}
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}
	/** Create a File for saving an image or video */
	@SuppressLint("SimpleDateFormat")
	public static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.
	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            //Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}



	public static void storeImage(Bitmap image, String name) {
		File pictureFile = getOutputMediaFile2(name);
		if (pictureFile == null) {
			//Log.d(TAG,"Error creating media file, check storage permissions: ");// e.getMessage());
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			//Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			//Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}

	public static File getOutputMediaFile2(String name) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
				+ "/Android/data/"
				+ "TSR"
				+ "/Files");

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		// Create a media file name
		File mediaFile;
		String mImageName = "MI_" + name + ".jpg";

		mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
		return mediaFile;
	}

	private static File getOutputMediaFile(Context context) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
				+ "/Android/data/"
				+ context.getPackageName()
				+ "/Files");

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		// Create a media file name
		File mediaFile;
		String filename = "Signs_Georeferenced.txt";

		mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
		return mediaFile;
	}

	public static void writeToFile(String data, Context context) {
		try {
			File file = getOutputMediaFile(context);
			FileOutputStream stream = new FileOutputStream(file,true);
			try {
				stream.write(data.getBytes());
			} finally {
				stream.close();
			}
		}
		catch (IOException e) {
			//Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public static void writeJson(SignData sign, Context context) {
		try {
			Gson gson = new Gson();
			String data = gson.toJson(sign);

			File file = getOutputMediaFile(context);
			FileOutputStream stream = new FileOutputStream(file,true);
			try {
				stream.write(data.getBytes());
			} finally {
				stream.close();
			}
		}
		catch (IOException e) {
			//Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public static int averageImageIntensity(Bitmap bitmap) {
		double averageIntensity = 0;

		Mat mat = new Mat();
		Utils.bitmapToMat(bitmap, mat);

		Scalar tempVal = mean(mat);
		averageIntensity = (tempVal.val[0] + tempVal.val[1] + tempVal.val[2]) / 3;

		return (int) averageIntensity;
	}

	public static Bitmap equalizeBitmap(Bitmap bitmap){
		Mat img = new Mat(bitmap.getHeight(), bitmap.getHeight(), CvType.CV_8UC4);

		Utils.bitmapToMat(bitmap, img); //converts bitmap to mat

		Bitmap eqBitmap = null;
		Mat eqImg = new Mat();
		ArrayList<Mat> channels = new ArrayList<>();

		cvtColor(img, eqImg, Imgproc.COLOR_BGR2YCrCb); //change the color image from BGR to YCrCb format

		split(eqImg,channels); //split the image into channels

		equalizeHist(channels.get(0), channels.get(0)); //equalize histogram on the 1st channel (Y)

		merge(channels,eqImg); //merge 3 channels including the modified 1st channel into one image

		cvtColor(eqImg, eqImg, Imgproc.COLOR_YCrCb2BGR); //change the color image from YCrCb to BGR format (to display image properly)

		eqBitmap = convertMatToBitmap(eqImg); //converts mat to bitmap

		return eqBitmap;
	}


	public static Bitmap getBitmapFromAsset(Context context, String filePath) {
		AssetManager assetManager = context.getAssets();
        //String label = "file:///android_asset/"+filePath;

		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open("images/"+filePath);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			// handle exception
		}

		return bitmap;
	}

	static int R_EARTH = 6378;

	public static double[] distanceToCoordinateAzimuth(double latitude, double longitude, double cy, double cx, double azimuth, int p){
		double[] coordinates = new double[2];

		double new_longitude  = longitude  + ((p*cx*Math.cos(azimuth) - cy*Math.sin(azimuth)) / Math.cos(latitude));
		double new_latitude = latitude + p*cx*Math.sin(azimuth) + cy*Math.cos(azimuth);

		coordinates[0] = Math.round(new_longitude*10000000.0)/10000000.0;
		coordinates[1] = Math.round(new_latitude*10000000.0)/10000000.0;

		return coordinates;
	}

	public static double calculateShift(int pixelWidth){
		//Latitude: 1 deg = 110.574 km
		//Longitude: 1 deg = 111.320*cos(latitude) km

		double distance = 0.0;
		double angle = 0.0;
		double latitudeShiftInDegrees = 0.0;
		double focalLenght = 521;
		double realLenght = 0.7;

		distance = (realLenght * focalLenght) / pixelWidth;

		angle = Math.atan(5.527/distance);

		distance = distance/Math.cos(angle);

		latitudeShiftInDegrees = distance / 111320;

		latitudeShiftInDegrees = Math.round(latitudeShiftInDegrees*10000000.0)/10000000.0;

		return latitudeShiftInDegrees;
	}

	public static float overspeed(String signRecognized, float speed){

		String str = "";
		float speedLimit = 0.0f;

		if(signRecognized.equals("20\r") || signRecognized.equals("30\r")
				|| signRecognized.equals("50\r") || signRecognized.equals("60\r")
				|| signRecognized.equals("70\r") || signRecognized.equals("80\r")
				|| signRecognized.equals("100\r") || signRecognized.equals("120\r")){
			speedLimit = Float.parseFloat(signRecognized);
		}

		return speedLimit;
	}

}