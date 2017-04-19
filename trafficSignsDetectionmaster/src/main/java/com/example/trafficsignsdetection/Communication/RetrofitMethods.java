package com.example.trafficsignsdetection.Communication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by helde on 18/04/2017.
 */

public class RetrofitMethods {

    public RetrofitMethods(){}

    public void uploadSignInfo(SignInfo signInfo) {
        API getResponse = AppConfig.getRetrofit().create(API.class);
        Call<ResponseBody> call = getResponse.uploadSign(signInfo);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
