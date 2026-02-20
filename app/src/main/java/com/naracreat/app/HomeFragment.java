package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private RecyclerView rvPosts, rvGenres;
    private EditText etSearch;

    private final List<Post> all = new ArrayList<>();
    private final List<Post> filtered = new ArrayList<>();

    private final List<String> genres = new ArrayList<>();
    private GenreAdapter genreAdapter;
    private PostAdapter postAdapter;

    private String activeGenre = "Semua";
    private String q = "";

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        swipe = v.findViewById(R.id.swipe);
        rvPosts = v.findViewById(R.id.recycler);
        rvGenres = v.findViewById(R.id.rvGenres);
        etSearch = v.findViewById(R.id.etSearch);

        // Genres horizontal
        rvGenres.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        genres.clear();
        genres.add("Semua");
        genreAdapter = new GenreAdapter(genres, g -> {
            activeGenre = g;
            genreAdapter.setActive(g);
            applyFilters();
        });
        rvGenres.setAdapter(genreAdapter);

        // Posts grid 2 cols
        rvPosts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        postAdapter = new PostAdapter(p -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views);
            i.putExtra("created_at", (p.createdAt != null && !p.createdAt.isEmpty()) ? p.createdAt : p.publishedAt);
            startActivity(i);
        });
        rvPosts.setAdapter(postAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                q = (s == null) ? "" : s.toString();
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        swipe.setOnRefreshListener(this::load);

        load();
    }

    private void load() {
        swipe.setRefreshing(true);

        ApiClient.api().getPosts(1).enqueue(new Callback<PostResponse>() {
            @Override public void onResponse(Call<PostResponse> call, Response<PostResponse> resp) {
                swipe.setRefreshing(false);

                if (!resp.isSuccessful() || resp.body() == null || resp.body().items == null) return;

                all.clear();
                all.addAll(resp.body().items);

                rebuildGenres(all);
                applyFilters();
            }

            @Override public void onFailure(Call<PostResponse> call, Throwable t) {
                swipe.setRefreshing(false);
            }
        });
    }

    private void rebuildGenres(List<Post> posts) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add("Semua");

        for (Post p : posts) {
            String g = pickGenreFromTitle(p.title);
            if (g != null && !g.trim().isEmpty()) set.add(g.trim());
        }

        genres.clear();
        genres.addAll(set);

        // reset active genre kalau gak ada
        if (!genres.contains(activeGenre)) activeGenre = "Semua";
        genreAdapter.setActive(activeGenre);
        genreAdapter.notifyDataSetChanged();
    }

    private void applyFilters() {
        filtered.clear();

        String qq = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);

        for (Post p : all) {
            String title = p.title == null ? "" : p.title;
            String g = pickGenreFromTitle(title);

            boolean okGenre = "Semua".equalsIgnoreCase(activeGenre) || (g != null && g.equalsIgnoreCase(activeGenre));
            boolean okSearch = qq.isEmpty() || title.toLowerCase(Locale.ROOT).contains(qq);

            if (okGenre && okSearch) filtered.add(p);
        }

        postAdapter.setItems(filtered);
    }

    // Genre dari judul: [3D], [L2D], dll. Kalau gak ada, "Lainnya"
    private String pickGenreFromTitle(String title) {
        if (title == null) return "Lainnya";
        String t = title.trim();

        // Cari pola [XXXX]
        int a = t.indexOf('[');
        int b = t.indexOf(']');
        if (a >= 0 && b > a && (b - a) <= 8) {
            String inside = t.substring(a + 1, b).trim();
            if (!inside.isEmpty()) return inside.toUpperCase(Locale.ROOT);
        }

        // fallback sederhana
        if (t.toLowerCase(Locale.ROOT).contains("3d")) return "3D";
        if (t.toLowerCase(Locale.ROOT).contains("l2d")) return "L2D";
        return "Lainnya";
    }
}
