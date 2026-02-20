package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private RecyclerView recycler;
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        swipe = v.findViewById(R.id.swipe);
        recycler = v.findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(new ArrayList<>(), this::openPlayer);
        recycler.setAdapter(adapter);

        swipe.setOnRefreshListener(() -> loadPage(1, true));

        // first load
        swipe.setRefreshing(true);
        loadPage(1, false);

        return v;
    }

    private void loadPage(int page, boolean fromPull) {
        ApiClient.api().getPosts(page).enqueue(new retrofit2.Callback<PostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call,
                                   @NonNull Response<PostResponse> response) {

                swipe.setRefreshing(false);

                PostResponse body = response.body();
                if (body != null && body.items != null) {
                    adapter.setItems(body.items);
                    if (fromPull) Toast.makeText(getContext(), "Berhasil refresh", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Gagal load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getContext(), "Offline / Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPlayer(Post p) {
        Intent i = new Intent(getContext(), PlayerActivity.class);
        i.putExtra("title", p.title);
        i.putExtra("video_url", p.videoUrl);
        i.putExtra("thumbnail_url", p.thumbnailUrl);
        i.putExtra("views", p.views != null ? p.views : 0);
        i.putExtra("created_at", p.timeSrc());
        startActivity(i);
    }
}
