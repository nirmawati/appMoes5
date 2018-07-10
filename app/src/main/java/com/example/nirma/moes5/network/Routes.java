package com.example.nirma.moes5.network;

import com.example.nirma.moes5.model.Penduduk;
import com.example.nirma.moes5.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mvryan on 13/06/18.
 */

public interface Routes {

    //mengambil data dari kependudukan
    @GET("penduduk")
    Call<List<Penduduk>> getPenduduk(@Query("nik") String nik);

    //mengambil data dari wp-json
    @GET("index.php/wp-json/wp/v2/posts")
    Call<List<Post>> getPosts();

    //get media
    @GET("index.php/wp-json/wp/v2/media")
    Call<List<Post>> getMedia(@Query("parent") String idPost);

    //get category
    @GET("index.php/wp-json/wp/v2/categories")
    Call<List<Post>> getCategory(@Query("post") String idPost);

}
