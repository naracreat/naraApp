package com.naracreat.app;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://narahentai.pages.dev/";

    private static ApiService API;

    public static ApiService api() {
        if (API == null) {

            OkHttpClient client = new OkHttpClient.Builder().build();

            Retrofit r = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            API = r.create(ApiService.class);
        }
        return API;
    }
}
