package com.example.trafficsignsdetection.Detection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;

import com.example.trafficsignsdetection.R;

public class Detector {
	private Activity activity;
	//private CascadeClassifier cascadeClassifier;

    public Detector() {}

	public Detector(Activity activity){
		this.activity = activity;
	}
	public void Detect(Mat mGray,MatOfRect signs, CascadeClassifier c){
		//loadCascadeFile(type);
		if (c != null) {
            c.detectMultiScale(mGray, signs, 1.1, 3, 0, new Size(30,30), new Size());
        }
	}

	public CascadeClassifier loadCascadeFile(int type){
		try {
            CascadeClassifier cc;
			InputStream is = null;
			File cascadeDir = activity.getDir("cascade", Context.MODE_PRIVATE);
			File cascadeFile=null;

			switch (type) {
			case 1:
				is = activity.getResources().openRawResource(R.raw.bienbaocam);

				cascadeFile = new File(cascadeDir, "bienbaocam.xml");
				break;

			case 2:
				is = activity.getResources().openRawResource(R.raw.biennguyhiem);

				cascadeFile = new File(cascadeDir, "biennguyhiem.xml");
				break;

				case 3:
					is = activity.getResources().openRawResource(R.raw.stopsigndetector2);

					cascadeFile = new File(cascadeDir, "stopsigndetector2.xml");
					break;

				case 4:
					is = activity.getResources().openRawResource(R.raw.mandatory);

					cascadeFile = new File(cascadeDir, "mandatory.xml");
					break;
			}
			
			FileOutputStream os = new FileOutputStream(cascadeFile);
			byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            cc = new CascadeClassifier(cascadeFile.getAbsolutePath());
            //cc.load(cascadeFile.getAbsolutePath());

            return cc;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
}
