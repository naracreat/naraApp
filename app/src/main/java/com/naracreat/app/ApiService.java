package com.naracreat.app;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/posts")
    Call<PostResponse> getPosts();
}
