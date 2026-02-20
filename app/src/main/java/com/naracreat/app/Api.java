package com.naracreat.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("api/posts")
    Call<PostResponse> posts(
            @Query("page") int page,
            @Query("q") String q,
            @Query("genre") String genre
    );
}
