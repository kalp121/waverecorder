package com.danielkim.soundrecorder.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        OkHttpClient client = new OkHttpClient.Builder().build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://stage29.wavebiz.in/web/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();


        return retrofit;
    }

}