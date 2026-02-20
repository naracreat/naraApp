package com.naracreat.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // Endpoint utama: /posts?page=1
    @GET("posts")
    Call<PostResponse> getPosts(
            @Query("page") int page
    );

    // Kalau kamu nanti mau filter/search, siapin yang ini (aman walau belum dipakai)
    @GET("posts")
    Call<PostResponse> posts(
            @Query("page") int page,
            @Query("q") String q,
            @Query("genre") String genre
    );
}
