package com.example.trafficsignsdetection;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trafficsignsdetection.Database.Controller;

/**
 * Created by helde on 19/05/2017.
 */

public class DatabaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database);

        Button botao = (Button)findViewById(R.id.button);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller crud = new Controller(getApplicationContext());
                EditText name = (EditText)findViewById(R.id.editText);
                EditText lat = (EditText)findViewById((R.id.editText2));
                EditText lon = (EditText)findViewById(R.id.editText3);
                String signName = name.getText().toString();
                String latitude = lat.getText().toString();
                String longitude = lon.getText().toString();
                String result;

                result = crud.insertSign(signName, "3.14", latitude,longitude);

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }
}
