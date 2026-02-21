package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvSub, tvWatched, tvLiked, tvFavCount;
    private Button btnLogin, btnTabHistory, btnTabLike, btnTabFav;

    private RecyclerView recycler;
    private PostAdapter postAdapter;

    private enum Tab { HISTORY, LIKE, FAV }
    private Tab tab = Tab.HISTORY;

    public ProfileFragment() { super(R.layout.fragment_profile); }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        tvName = v.findViewById(R.id.tvName);
        tvSub = v.findViewById(R.id.tvSub);
        tvWatched = v.findViewById(R.id.tvWatched);
        tvLiked = v.findViewById(R.id.tvLiked);
        tvFavCount = v.findViewById(R.id.tvFavCount);

        btnLogin = v.findViewById(R.id.btnLogin);
        btnTabHistory = v.findViewById(R.id.btnTabHistory);
        btnTabLike = v.findViewById(R.id.btnTabLike);
        btnTabFav = v.findViewById(R.id.btnTabFav);

        recycler = v.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        postAdapter = new PostAdapter(p -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);
            i.putExtra("created_at", p.createdAt);
            i.putExtra("published_at", p.publishedAt);
            i.putExtra("slug", p.slug);
            i.putExtra("duration_minutes", p.durationMinutes != null ? p.durationMinutes : 0);
            startActivity(i);
        });
        recycler.setAdapter(postAdapter);

        btnLogin.setOnClickListener(vv -> {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });

        btnTabHistory.setOnClickListener(vv -> { tab = Tab.HISTORY; refresh(); });
        btnTabLike.setOnClickListener(vv -> { tab = Tab.LIKE; refresh(); });
        btnTabFav.setOnClickListener(vv -> { tab = Tab.FAV; refresh(); });

        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        boolean logged = Session.isLoggedIn(requireContext());
        if (!logged) {
            tvName.setText("Tamu");
            tvSub.setText("Masuk untuk sinkron suka & favorit");
            btnLogin.setText("Masuk");
        } else {
            String email = Session.getEmail(requireContext());
            tvName.setText(email);
            tvSub.setText("Login lokal aktif");
            btnLogin.setText("Keluar");
            btnLogin.setOnClickListener(vv -> {
                Session.logout(requireContext());
                refresh();
            });
        }

        // Counts
        List<Post> hist = HistoryStore.getHistory(requireContext());
        tvWatched.setText(String.valueOf(hist.size()));

        // Like/Fav count total: kita hitung dari prefs by scanning history list (simple, cukup buat UI)
        int likeTotal = 0;
        int favTotal = 0;
        for (Post p : hist) {
            String baseKey = (p.slug != null && !p.slug.trim().isEmpty())
                    ? p.slug
                    : (p.videoUrl == null ? "no_key" : p.videoUrl);
            String user = Session.userKey(requireContext());
            int like = requireContext().getSharedPreferences("nara_local", 0).getInt(user + "_like_" + baseKey, 0);
            int fav = requireContext().getSharedPreferences("nara_local", 0).getInt(user + "_fav_" + baseKey, 0);
            if (like > 0) likeTotal += 1;
            if (fav > 0) favTotal += 1;
        }
        tvLiked.setText(likeTotal == 0 ? "—" : String.valueOf(likeTotal));
        tvFavCount.setText(favTotal == 0 ? "—" : String.valueOf(favTotal));

        // List sesuai tab
        if (tab == Tab.HISTORY) {
            postAdapter.setItems(new ArrayList<>(hist));
        } else if (tab == Tab.LIKE) {
            postAdapter.setItems(new ArrayList<>(filterLiked(hist)));
        } else {
            postAdapter.setItems(new ArrayList<>(filterFav(hist)));
        }
    }

    private List<Post> filterLiked(List<Post> hist) {
        List<Post> out = new ArrayList<>();
        String user = Session.userKey(requireContext());
        for (Post p : hist) {
            String baseKey = (p.slug != null && !p.slug.trim().isEmpty())
                    ? p.slug
                    : (p.videoUrl == null ? "no_key" : p.videoUrl);
            int like = requireContext().getSharedPreferences("nara_local", 0).getInt(user + "_like_" + baseKey, 0);
            if (like > 0) out.add(p);
        }
        return out;
    }

    private List<Post> filterFav(List<Post> hist) {
        List<Post> out = new ArrayList<>();
        String user = Session.userKey(requireContext());
        for (Post p : hist) {
            String baseKey = (p.slug != null && !p.slug.trim().isEmpty())
                    ? p.slug
                    : (p.videoUrl == null ? "no_key" : p.videoUrl);
            int fav = requireContext().getSharedPreferences("nara_local", 0).getInt(user + "_fav_" + baseKey, 0);
            if (fav > 0) out.add(p);
        }
        return out;
    }
}
