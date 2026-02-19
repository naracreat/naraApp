package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    PostAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostAdapter(new ArrayList<>(), p -> {
            Intent i = new Intent(this, PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);
            i.putExtra("created_at", p.createdAt != null ? p.createdAt : (p.publishedAt != null ? p.publishedAt : ""));
            i.putExtra("description", "—"); // kalau nanti API ada description, tinggal isi
            startActivity(i);
        });

        rv.setAdapter(adapter);

        loadPage(1);
    }

    private void loadPage(int page) {
        ApiClient.api().posts(page).enqueue(new Callback<PostsResponse>() {
            @Override public void onResponse(Call<PostsResponse> call, Response<PostsResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().items != null) {
                    adapter.setItems(resp.body().items);
                }
            }
            @Override public void onFailure(Call<PostsResponse> call, Throwable t) {}
        });
    }
}
