package com.example.trafficsignsdetection.Communication;

import com.example.trafficsignsdetection.Database.Sign;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
//import retrofit2.http.QueryMap;
import retrofit2.http.Query;

/**
 * Created by helde on 18/04/2017.
 */

public interface API {
        @POST("signs/add")
        Call<ResponseBody> uploadSigns(@Body SignsData coords);

        @GET("signs/list")
        Call<ArrayList<Sign>> getSigns(@Query("latitude") String latitude, @Query("longitude") String longitude);
}
