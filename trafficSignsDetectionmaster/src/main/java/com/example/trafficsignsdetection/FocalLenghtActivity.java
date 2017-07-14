package com.example.trafficsignsdetection;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by helde on 13/06/2017.
 */

public class FocalLenghtActivity extends Activity{

        Camera mcamera;
        int focul_length;
        Camera.Parameters params;
        File mFile;

        public int PICTURE_ACTIVITY_CODE = 1;
        public String FILENAME = "sdcard/photo.jpg";
        Camera.Parameters cameraParameters;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mcamera = Camera.open();
            launchTakePhoto();

        }

        private void launchTakePhoto()
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraParameters = mcamera.getParameters();
            Camera.CameraInfo myinfo = new Camera.CameraInfo();
            float l=cameraParameters.getFocalLength(); // Here its creating Null Pointer Exception
            mFile = new File("My Focul Length:--"+l);
            Toast.makeText(getApplicationContext(), "My Focul Length:--"+l, Toast.LENGTH_SHORT);
            //System.out.println("My Focul Length:--"+l);
            Uri outputFileUri = Uri.fromFile(mFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, PICTURE_ACTIVITY_CODE);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            if (requestCode == PICTURE_ACTIVITY_CODE)
            {
                if (resultCode == RESULT_OK)
                {
                    ImageView myimageView = (ImageView) findViewById(R.id.imageView1);
                    Uri inputFileUri = Uri.fromFile(mFile);
                    myimageView.setImageURI(inputFileUri);
                }
            }
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

    }

