package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

    public UpdateFragment() {
        super(R.layout.fragment_update);
    }

    private SwipeRefreshLayout swipe;
    private PostAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        swipe = view.findViewById(R.id.swipe);

        RecyclerView rv = view.findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PostAdapter(new ArrayList<>(), p -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("slug", p.slug);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views);
            i.putExtra("created_at", p.createdAt);
            i.putExtra("published_at", p.publishedAt);
            i.putExtra("description", p.description);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        swipe.setOnRefreshListener(() -> load(true));

        load(false);
    }

    private void load(boolean fromPull) {
        if (!fromPull) swipe.setRefreshing(true);

        ApiClient.get().create(ApiService.class)
                .getPosts(1)
                .enqueue(new Callback<PostResponse>() {
                    @Override
                    public void onResponse(Call<PostResponse> call, Response<PostResponse> resp) {
                        swipe.setRefreshing(false);
                        if (!resp.isSuccessful() || resp.body() == null || resp.body().items == null) {
                            Toast.makeText(requireContext(), "Gagal load update", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adapter.setItems(resp.body().items);
                    }

                    @Override
                    public void onFailure(Call<PostResponse> call, Throwable t) {
                        swipe.setRefreshing(false);
                        Toast.makeText(requireContext(), "Offline / Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
