package com.example.trafficsignsdetection.Communication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by helde on 18/04/2017.
 */

public interface API {
        @POST("coordinates")
        Call<ResponseBody> uploadSign(@Body SignInfo signInfo);
}
