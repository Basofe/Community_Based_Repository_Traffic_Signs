package com.example.trafficsignsdetection.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import static org.opencv.imgproc.Imgproc.INTER_AREA;
import static org.opencv.imgproc.Imgproc.resize;

public class Utilities {

	private static final String TAG = "UTILITIES";

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
	            Log.d("MyCameraApp", "failed to create directory");
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



	public static void storeImage(Bitmap image, int counter) {
		File pictureFile = getOutputMediaFile2(counter);
		if (pictureFile == null) {
			Log.d(TAG,
					"Error creating media file, check storage permissions: ");// e.getMessage());
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}

	public static File getOutputMediaFile2(int counter) {
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
		String mImageName = "MI_" + counter + ".jpg";

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
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
	
}