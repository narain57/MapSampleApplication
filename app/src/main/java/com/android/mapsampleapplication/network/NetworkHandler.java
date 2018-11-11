package com.android.mapsampleapplication.network;

import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkHandler {

    private static final long CONNECTION_TIMEOUT = 50000;
    private static OkHttpClient okHttpClient;
    private static final String BASE_URL = "https://fake-poi-api.mytaxi.com";


    public static MSServicesInterface getClientInstance() {

        // Reset client instance.
        okHttpClient = null;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getOkHttpClient())
                .build();

        return retrofit.create(MSServicesInterface.class);
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = getOkHttpClientBuilder();
        return builder.build();
    }

    private static OkHttpClient.Builder getOkHttpClientBuilder() {

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        client.readTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

        return client;
    }
}
