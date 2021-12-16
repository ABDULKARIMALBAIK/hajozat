package com.abdulkarimalbaik.dev.hajozat.Remote;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitScalarsClient {

    public static Retrofit instance = null;

    public static Retrofit getInstance(String baseUrl){

        if (instance == null){

            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }

        return instance;
    }
}
