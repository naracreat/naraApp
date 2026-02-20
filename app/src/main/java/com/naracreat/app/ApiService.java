package com.naracreat.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // bisa dipanggil tanpa argumen: api.getPosts()
    @GET("api/posts")
    Call<PostResponse> getPosts();

    // bisa dipanggil pakai page: api.getPosts(1)
    @GET("api/posts")
    Call<PostResponse> getPosts(@Query("page") int page);
}
