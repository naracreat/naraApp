package com.naracreat.app;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/posts")
    Call<PostsResponse> getPosts(@Query("page") int page);

}
