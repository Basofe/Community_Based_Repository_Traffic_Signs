package com.example.trafficsignsdetection.Communication;

import android.content.Context;

import com.example.trafficsignsdetection.Database.Controller;
import com.example.trafficsignsdetection.Database.Sign;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by helde on 18/04/2017.
 */

public class RetrofitMethods {

    Controller controller;

    public RetrofitMethods(Context context){
        controller = new Controller(context);
    }

    public Controller getController(){
        return this.controller;
    }

    public void uploadSignInfo(String name, String orientation, String latitude, String longitude) {
        API getResponse = AppConfig.getRetrofit().create(API.class);
        Call<ResponseBody> call = getResponse.uploadSign(name, orientation, latitude, longitude);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void uploadSignArray(SignsData coords) {
        API getResponse = AppConfig.getRetrofit().create(API.class);
        Call<ResponseBody> call = getResponse.uploadSigns(coords);
        call.enqueue(new Callback<ResponseBody>() {
                         @Override
                         public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                             if (response.isSuccessful()) {
                                 System.out.println("Response: " + response.code());
                             } else {
                                 try {
                                     System.out.println("NÃO DEU!!! ");
                                     System.out.println(response.raw());
                                 } catch (Exception e) {
                                     System.out.println("ESCAXOU!!!");
                                 }
                             }
                         }

                         @Override

                         public void onFailure(Call<ResponseBody> call, Throwable t) {

                         }
                     }
        );
    }

    public void getSigns(String latitude, String longitude) {
        API getResponse = AppConfig.getRetrofit().create(API.class);
        Call<ArrayList<Sign>> call = getResponse.getSigns(latitude,longitude);
        call.enqueue(new Callback<ArrayList<Sign>>() {
            @Override
            public void onResponse(Call<ArrayList<Sign>> call, Response<ArrayList<Sign>> response) {
                if (response.isSuccessful()) {
                    System.out.println("Response: " + response.code());
                    List<Sign> rs = response.body();
                    controller.insertSigns(rs);
                    System.out.println("Terminou!");

                } else {
                    try {
                        System.out.println("NÃO DEU!!! ");
                        System.out.println(response.raw());
                    } catch (Exception e) {
                        System.out.println("ESCAXOU!!!");
                    }
                }
            }


            @Override
            public void onFailure(Call<ArrayList<Sign>> call, Throwable t) {

            }
        });
    }
}
