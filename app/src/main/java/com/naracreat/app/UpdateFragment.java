package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateFragment extends Fragment {
    private SwipeRefreshLayout swipe;
    private PostAdapter adapter;

    public UpdateFragment() { super(R.layout.fragment_update); }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        swipe = v.findViewById(R.id.swipe);
        RecyclerView rv = v.findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PostAdapter(p -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);

            // FIX views: harus primitive int
            i.putExtra("views", p.views != null ? p.views : 0);

            i.putExtra("created_at", (p.createdAt != null && !p.createdAt.isEmpty()) ? p.createdAt : p.publishedAt);
            i.putExtra("published_at", p.publishedAt);
            i.putExtra("slug", p.slug);
            i.putExtra("duration_minutes", p.durationMinutes != null ? p.durationMinutes : 0);

            startActivity(i);
        });

        rv.setAdapter(adapter);
        swipe.setOnRefreshListener(this::load);
        load();
    }

    private void load() {
        swipe.setRefreshing(true);
        ApiClient.api().getPosts(1).enqueue(new Callback() {
            @Override public void onResponse(Call call, Response resp) {
                swipe.setRefreshing(false);
                if (!resp.isSuccessful() || resp.body() == null || resp.body().items == null) return;
                adapter.setItems(new ArrayList<>(resp.body().items));
            }
            @Override public void onFailure(Call call, Throwable t) {
                swipe.setRefreshing(false);
            }
        });
    }
}
