package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private final List<Post> all = new ArrayList<>();
    private final List<Post> filtered = new ArrayList<>();

    private PostAdapter postAdapter;
    private GenreAdapter genreAdapter;

    private String selectedGenre = "Semua";
    private String query = "";

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView tvTitle = view.findViewById(R.id.tvHomeTitle);
        tvTitle.setText("NaraApp");

        EditText etSearch = view.findViewById(R.id.etSearch);

        RecyclerView rvGenres = view.findViewById(R.id.rvGenres);
        rvGenres.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView rvPosts = view.findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));

        postAdapter = new PostAdapter(p -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("slug", p.slug);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);
            i.putExtra("created_at", p.createdAt);
            i.putExtra("published_at", p.publishedAt);
            i.putExtra("description", ""); // nanti kalau API post detail udah ada, isi di sini
            startActivity(i);
        });
        rvPosts.setAdapter(postAdapter);

        // genres default
        List<String> genreList = new ArrayList<>();
        genreList.add("Semua");
        genreAdapter = new GenreAdapter(genreList, g -> {
            selectedGenre = g;
            genreAdapter.setSelected(g);
            applyFilter();
        });
        rvGenres.setAdapter(genreAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString();
                applyFilter();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadPosts();
    }

    private void loadPosts() {
        ApiClient.api().posts(1).enqueue(new Callback<PostResponse>() {
            @Override public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> resp) {
                if (!isAdded()) return;
                if (!resp.isSuccessful() || resp.body() == null || resp.body().items == null) return;

                all.clear();
                all.addAll(resp.body().items);

                buildGenresFromPosts(resp.body().items);
                applyFilter();
            }

            @Override public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {}
        });
    }

    private void buildGenresFromPosts(List<Post> items) {
        Set<String> set = new LinkedHashSet<>();
        set.add("Semua");

        for (Post p : items) {
            // ambil dari API kalau ada
            if (p.genres != null) {
                for (String g : p.genres) {
                    if (g != null && !g.trim().isEmpty()) set.add(g.trim());
                }
            }
            if (p.genre != null && !p.genre.trim().isEmpty()) set.add(p.genre.trim());
            if (p.category != null && !p.category.trim().isEmpty()) set.add(p.category.trim());

            // fallback: ambil tag dari title (tetap sumbernya dari api/posts)
            if (p.title != null) {
                String tl = p.title.toUpperCase(Locale.ROOT);
                if (tl.contains("3D")) set.add("3D");
                if (tl.contains("L2D")) set.add("L2D");
                if (tl.contains("1080")) set.add("1080P");
                if (tl.contains("720")) set.add("720P");
                if (tl.contains("GENSHIN")) set.add("Genshin");
                if (tl.contains("BORUTO") || tl.contains("NARUTO")) set.add("Naruto");
                if (tl.contains("ATTACK ON TITAN")) set.add("AOT");
            }
        }

        List<String> list = new ArrayList<>(set);
        genreAdapter = new GenreAdapter(list, g -> {
            selectedGenre = g;
            genreAdapter.setSelected(g);
            applyFilter();
        });

        RecyclerView rvGenres = requireView().findViewById(R.id.rvGenres);
        rvGenres.setAdapter(genreAdapter);
        genreAdapter.setSelected(selectedGenre);
    }

    private void applyFilter() {
        filtered.clear();

        String q = (query == null) ? "" : query.trim().toLowerCase(Locale.ROOT);

        for (Post p : all) {
            boolean matchQ = q.isEmpty()
                    || (p.title != null && p.title.toLowerCase(Locale.ROOT).contains(q))
                    || (p.slug != null && p.slug.toLowerCase(Locale.ROOT).contains(q));

            boolean matchGenre = selectedGenre.equals("Semua") || postHasGenre(p, selectedGenre);

            if (matchQ && matchGenre) filtered.add(p);
        }

        postAdapter.setItems(filtered);
    }

    private boolean postHasGenre(Post p, String g) {
        if (g == null || g.isEmpty()) return true;

        if (p.genres != null) {
            for (String x : p.genres) if (g.equalsIgnoreCase(String.valueOf(x))) return true;
        }
        if (p.genre != null && g.equalsIgnoreCase(p.genre)) return true;
        if (p.category != null && g.equalsIgnoreCase(p.category)) return true;

        // fallback via title (tetap dari api/posts)
        if (p.title != null) {
            String tl = p.title.toUpperCase(Locale.ROOT);
            String gg = g.toUpperCase(Locale.ROOT);
            return tl.contains(gg);
        }
        return false;
    }
}
