package com.example.nirma.moes5.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mvryan on 13/06/18.
 */

public class Network {

    public static Retrofit request() {
        return new Retrofit.Builder()
                .baseUrl("http://192.168.43.228:1337/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit requestWp() {
        return new Retrofit.Builder()
                .baseUrl("http://192.168.43.228/moes5Article/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
