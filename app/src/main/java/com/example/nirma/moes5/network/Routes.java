package com.example.nirma.moes5.network;

import com.example.nirma.moes5.model.Penduduk;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mvryan on 13/06/18.
 */

public interface Routes {

    @GET("kependudukan")
    Call<List<Penduduk>> getPenduduk(@Query("nik") String nik);

    @GET("kependudukan")
    Call<String> getPenduduk();

}
