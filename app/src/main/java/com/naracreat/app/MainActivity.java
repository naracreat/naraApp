package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
        rv.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new PostAdapter(new ArrayList<>(), p -> {
            Intent i = new Intent(this, PlayerActivity.class);

            i.putExtra("title", p.title);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);

            String when = (p.createdAt != null && !p.createdAt.isEmpty())
                    ? p.createdAt
                    : (p.publishedAt != null ? p.publishedAt : "");

            i.putExtra("created_at", when);
            i.putExtra("description", "—");

            startActivity(i);
        });

        rv.setAdapter(adapter);

        load();
    }

    private void load() {
        ApiClient.api().posts(1, null, null).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().items != null) {
                    adapter.setItems(resp.body().items);
                }
            }
            @Override public void onFailure(Call<PostResponse> call, Throwable t) {}
        });
    }
}
