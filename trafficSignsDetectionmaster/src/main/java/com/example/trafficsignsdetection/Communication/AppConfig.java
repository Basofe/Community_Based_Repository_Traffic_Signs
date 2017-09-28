package com.example.trafficsignsdetection.Communication;

import android.support.annotation.NonNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by helde on 18/04/2017.
 */

public class AppConfig {

    private static String BASE_URL = "https://traffic-signs-repository.herokuapp.com";

    @NonNull
    static Retrofit getRetrofit() {

        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
