package com.example.nirma.moes5.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mvryan on 13/06/18.
 * this class is to get api
 */

public class Network {

    //connect to sails js api penduduk
    public static Retrofit request() {
        return new Retrofit.Builder()
                .baseUrl("https://kependudukan-api.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    //connect to wordpres json api
    public static Retrofit requestWp() {
        return new Retrofit.Builder()
                .baseUrl("http://192.168.43.228/moes5Article/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
