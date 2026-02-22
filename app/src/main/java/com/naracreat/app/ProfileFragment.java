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

// PENTING: jangan import android.R
import com.naracreat.app.R;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvSub, tvWatched, tvLiked, tvFavCount, tvEmpty;
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
        tvEmpty = v.findViewById(R.id.tvEmpty);

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
            if (Session.isLoggedIn(requireContext())) {
                Session.logout(requireContext());
                refresh();
            } else {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
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
            tvName.setText(Session.getEmail(requireContext()));
            tvSub.setText("Login lokal aktif");
            btnLogin.setText("Keluar");
        }

        List<Post> hist = HistoryStore.getHistory(requireContext());
        tvWatched.setText(String.valueOf(hist.size()));

        int likedItems = 0;
        int favItems = 0;

        String user = Session.userKey(requireContext());
        for (Post p : hist) {
            String baseKey = (p.slug != null && !p.slug.trim().isEmpty())
                    ? p.slug
                    : (p.videoUrl == null ? "no_key" : p.videoUrl);

            int like = requireContext().getSharedPreferences("nara_local", 0)
                    .getInt(user + "_like_" + baseKey, 0);
            int fav = requireContext().getSharedPreferences("nara_local", 0)
                    .getInt(user + "_fav_" + baseKey, 0);

            if (like > 0) likedItems++;
            if (fav > 0) favItems++;
        }

        tvLiked.setText(likedItems == 0 ? "—" : String.valueOf(likedItems));
        tvFavCount.setText(favItems == 0 ? "—" : String.valueOf(favItems));

        List<Post> show;
        if (tab == Tab.HISTORY) show = hist;
        else if (tab == Tab.LIKE) show = filterLiked(hist);
        else show = filterFav(hist);

        postAdapter.setItems(new ArrayList<>(show));

        tvEmpty.setVisibility(show.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(show.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private List<Post> filterLiked(List<Post> hist) {
        List<Post> out = new ArrayList<>();
        String user = Session.userKey(requireContext());
        for (Post p : hist) {
            String baseKey = (p.slug != null && !p.slug.trim().isEmpty())
                    ? p.slug
                    : (p.videoUrl == null ? "no_key" : p.videoUrl);
            int like = requireContext().getSharedPreferences("nara_local", 0)
                    .getInt(user + "_like_" + baseKey, 0);
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
            int fav = requireContext().getSharedPreferences("nara_local", 0)
                    .getInt(user + "_fav_" + baseKey, 0);
            if (fav > 0) out.add(p);
        }
        return out;
    }
}
