package com.example.trafficsignsdetection;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trafficsignsdetection.Communication.RetrofitMethods;
import com.example.trafficsignsdetection.Communication.SignData;
import com.example.trafficsignsdetection.Communication.SignsData;
import com.example.trafficsignsdetection.Database.Controller;
import com.example.trafficsignsdetection.Database.Coordinate;
import com.example.trafficsignsdetection.Database.Sign;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Created by helde on 19/05/2017.
 */

public class DatabaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database);

        Button botaoInsert = (Button)findViewById(R.id.buttonInsert);
        Button botaoGet = (Button)findViewById(R.id.buttonGet);

        botaoInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitMethods rm = new RetrofitMethods(getApplicationContext());

                rm.getSigns("41.56", "-8.39");
            }
        });

        botaoGet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(), list.get(0).toString() + " | SIZE: " + list.size(), Toast.LENGTH_SHORT);
            }
        });

        /*botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<SignData> arraylist = new ArrayList<>();
                HashMap<Coordinate, SignData> nrmStop = new HashMap<>();
                HashMap<Coordinate, SignData> nrmProh = new HashMap<>();
                RetrofitMethods rm = new RetrofitMethods(getApplicationContext());
                SignsData sd = new SignsData();

                for (int i = 0; i < 2; i++) {
                    String[] arr = {"Stop", "Prohibition"};
                    Random random = new Random();

                    // randomly selects an index from the arr
                    int select = random.nextInt(arr.length);

                    Controller crud = new Controller(getApplicationContext());
                    Coordinate c;
                    SignData sign;

                    c = crud.generateCoordinates();
                    sign = new SignData(arr[select], String.valueOf(c.getLatitude()), String.valueOf(c.getLongitude()), "0.5", arr[select]);

                    if (arr[select].equals("Stop")) {
                        if (!nrmStop.containsKey(c)) {
                            nrmStop.put(c, sign);
                        }
                    } else if (!nrmProh.containsKey(c)) {
                        nrmProh.put(c, sign);
                    }
                }

                for (SignData s : nrmStop.values()) {
                    //sd.add(s);
                    arraylist.add(s);
                }

                for (SignData s : nrmProh.values()) {
                    //sd.add(s);
                    arraylist.add(s);
                }

                sd.addArray(arraylist);

                rm.uploadSignArray(sd);
            }
        });*/

        /*botaoInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random generator = new Random();

                DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.UK);
                otherSymbols.setDecimalSeparator('.');
                otherSymbols.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat("0.####", otherSymbols);

                ArrayList<Coordinate> coords = new ArrayList<>();
                HashMap<Character, ArrayList<Coordinate>> coordsByOrientation;
                HashMap<Integer, ArrayList<SignData>> coordsByOrientationOptimized = null;

                Controller crud = new Controller(getApplicationContext());
                EditText name = (EditText)findViewById(R.id.editText);
                EditText lat = (EditText)findViewById((R.id.editText2));
                EditText lon = (EditText)findViewById(R.id.editText3);
                String signName = name.getText().toString();
                String latitude = lat.getText().toString();
                String longitude = lon.getText().toString();
                String result;

//                for (int i = 0; i < 100000; i++) {
//                    Coordinate coordinate = crud.generateCoordinates();
//                    double num = generator.nextDouble() * (6.28 - 0) + 0;
//                    crud.insertSign("Stop", df.format(num), coordinate.getLatitude(), coordinate.getLongitude());
//                }

                  //coords = crud.getFromToSigns(5000, 20);

                long start = System.currentTimeMillis();

//                coordsByOrientation = crud.getSignsHashArray(5000,20);

                coordsByOrientationOptimized = crud.getSignsHashArrayOptimized(5000, 20);

                long stop = System.currentTimeMillis();
                long timeElapsed = stop-start;

                System.out.println("Load Time: " + timeElapsed + " millisecs!");

//                coords = crud.getAllSigns();


//                int N = coordsByOrientationOptimized.size();

                int counter = 0;

                long start2 = System.currentTimeMillis();

                for(int j=0; j<7; j++){
//                    System.out.println("ORIENTATION " + j + "\n--------------------------------");
//                    char index = Character.forDigit(j,10);
                    ArrayList<SignData> cs = coordsByOrientationOptimized.get(j);
                    for(SignData c : cs){
//                        System.out.println(String.valueOf(c.getSignName()) + " -> " + c.getLatitude() + ", " + c.getLongitude());
//                        counter++;
                    }
//                    System.out.println("Contagem: " + counter + "\n--------------------------------");
                }


                long stop2 = System.currentTimeMillis();

                long timeElapsed2 = stop2-start2;

                System.out.println("Accessing all elements: " + timeElapsed2 + " milisegundos!");

                Toast.makeText(getApplicationContext(), String.valueOf(timeElapsed), Toast.LENGTH_LONG).show();
            }
        });*/
    }
}
