package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextInputEditText etSearch;
    private RecyclerView rvGenres, rvFeed;

    private GenreAdapter genreAdapter;
    private FeedAdapter feedAdapter;

    private String selectedGenre = "Untuk Anda";
    private String query = "";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        etSearch = v.findViewById(R.id.etSearch);
        rvGenres = v.findViewById(R.id.rvGenres);
        rvFeed = v.findViewById(R.id.rvFeed);

        rvGenres.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> baseGenres = new ArrayList<>();
        baseGenres.add("Untuk Anda"); // default tab
        genreAdapter = new GenreAdapter(baseGenres, g -> {
            selectedGenre = g;
            genreAdapter.setSelected(g);
            loadHome();
        });
        rvGenres.setAdapter(genreAdapter);

        feedAdapter = new FeedAdapter(p -> {
            Intent i = new Intent(getContext(), PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);
            String created = (p.createdAt != null && !p.createdAt.isEmpty()) ? p.createdAt : (p.publishedAt != null ? p.publishedAt : "");
            i.putExtra("created_at", created);
            i.putExtra("description", "—");
            startActivity(i);
        });
        rvFeed.setAdapter(feedAdapter);

        etSearch.setOnKeyListener((vv, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                query = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
                loadHome();
                return true;
            }
            return false;
        });

        loadHome();
    }

    private void loadHome() {
        // genre “Untuk Anda” -> kirim null biar API gak kepancing filter
        String genreParam = "Untuk Anda".equals(selectedGenre) ? null : selectedGenre;
        String qParam = (query == null || query.isEmpty()) ? null : query;

        ApiClient.api().posts(1, qParam, genreParam).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful() && resp.body() != null && resp.body().items != null) {
                    List<Post> items = resp.body().items;

                    // build genre list dari posts (web)
                    LinkedHashSet<String> genres = new LinkedHashSet<>();
                    genres.add("Untuk Anda");
                    for (Post p : items) {
                        if (p.genres != null) {
                            for (String g : p.genres) if (g != null && !g.trim().isEmpty()) genres.add(g.trim());
                        }
                        if (p.genre != null && !p.genre.trim().isEmpty()) genres.add(p.genre.trim());
                        if (p.category != null && !p.category.trim().isEmpty()) genres.add(p.category.trim());
                    }

                    // refresh genre adapter data (simple recreate)
                    genreAdapter = new GenreAdapter(new ArrayList<>(genres), g -> {
                        selectedGenre = g;
                        genreAdapter.setSelected(g);
                        loadHome();
                    });
                    rvGenres.setAdapter(genreAdapter);
                    genreAdapter.setSelected(selectedGenre);

                    feedAdapter.setData(items);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) { }
        });
    }
}
