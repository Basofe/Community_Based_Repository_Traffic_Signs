package com.example.trafficsignsdetection.Communication;

import com.example.trafficsignsdetection.Database.Sign;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryName;

/**
 * Created by helde on 18/04/2017.
 */

public interface API {
        @FormUrlEncoded
        @POST("/signs/add")
        Call<ResponseBody> uploadSign(@Field("name") String name,
                                      @Field("orientation") String orientation,
                                      @Field("latitude") String latitude,
                                      @Field("longitude") String longitude);

        @POST("signs/add")
        Call<ResponseBody> uploadSigns(@Body SignsData coords);

        @GET("signs/list")
        //Call<ArrayList<Sign>> getSigns();
        Call<ArrayList<Sign>> getSigns(@QueryName String latitude, @QueryName String longitude);
}
