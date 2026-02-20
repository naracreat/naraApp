package com.naracreat.app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://narahentai.pages.dev/";
    private static Retrofit retrofit;

    public static Retrofit get() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // INI YANG BIKIN ApiClient.api().posts(page) BISA
    public static Api api() {
        return get().create(Api.class);
    }

    // Optional: kalau lo mau pakai ApiService juga
    public static ApiService service() {
        return get().create(ApiService.class);
    }
}
