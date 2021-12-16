package com.abdulkarimalbaik.dev.hajozat.Remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitMapClient {

    public static Retrofit retrofit = null;

    //We used Scalars converter , because the result is scalars (Maps)

    public static Retrofit getClient(String baseUrl){

        if (retrofit == null){

            OkHttpClient okHttpClient = new OkHttpClient.Builder()  ///////////////
                    .connectTimeout(4, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        }


        return retrofit;
    }
}
