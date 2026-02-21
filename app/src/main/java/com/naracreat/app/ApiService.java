package com.naracreat.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    // https://narahentai.pages.dev/api/posts?page=1
    @GET("api/posts")
    Call<PostResponse> getPosts(@Query("page") int page);
}
