package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rv;
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        rv = findRecycler(v);
        if (rv == null) {
            Toast.makeText(getContext(), "Layout error: RecyclerView tidak ketemu", Toast.LENGTH_SHORT).show();
            return v;
        }

        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostAdapter(new ArrayList<>(), this::openPlayer);
        rv.setAdapter(adapter);

        loadPage(1);

        return v;
    }

    // Cari recycler dengan beberapa id yang mungkin dipakai
    private RecyclerView findRecycler(View root) {
        int[] ids = new int[] {
                R.id.rvPosts,
                R.id.rv_post,
                R.id.rv_posts,
                R.id.recyclerPosts,
                R.id.recycler_posts,
                R.id.recycler,
                R.id.rv
        };

        for (int id : ids) {
            try {
                View v = root.findViewById(id);
                if (v instanceof RecyclerView) return (RecyclerView) v;
            } catch (Exception ignored) {}
        }

        // fallback: cari pertama RecyclerView yang ada di layout
        if (root instanceof ViewGroup) {
            RecyclerView found = deepFindRecycler((ViewGroup) root);
            if (found != null) return found;
        }
        return null;
    }

    private RecyclerView deepFindRecycler(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View c = vg.getChildAt(i);
            if (c instanceof RecyclerView) return (RecyclerView) c;
            if (c instanceof ViewGroup) {
                RecyclerView r = deepFindRecycler((ViewGroup) c);
                if (r != null) return r;
            }
        }
        return null;
    }

    private void loadPage(int page) {
        ApiClient.api().getPosts(page).enqueue(new retrofit2.Callback<PostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call,
                                   @NonNull Response<PostResponse> response) {

                if (response.body() != null && response.body().items != null) {
                    adapter.setItems(response.body().items);
                } else {
                    Toast.makeText(getContext(), "Error load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
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
