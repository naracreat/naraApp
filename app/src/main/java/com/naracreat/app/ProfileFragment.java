package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvSub;
    private TextView tvStatHistory, tvStatLike, tvStatFav, tvEmpty;
    private Button btnLogin, tabHistory, tabLike, tabFav;
    private RecyclerView rvGrid;
    private ProfileGridAdapter adapter;

    private enum Tab { HISTORY, LIKE, FAV }
    private Tab currentTab = Tab.HISTORY;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {

        tvName = v.findViewById(R.id.tvName);
        tvSub = v.findViewById(R.id.tvSub);
        btnLogin = v.findViewById(R.id.btnLogin);

        tvStatHistory = v.findViewById(R.id.tvStatHistory);
        tvStatLike = v.findViewById(R.id.tvStatLike);
        tvStatFav = v.findViewById(R.id.tvStatFav);

        tabHistory = v.findViewById(R.id.tabHistory);
        tabLike = v.findViewById(R.id.tabLike);
        tabFav = v.findViewById(R.id.tabFav);

        tvEmpty = v.findViewById(R.id.tvEmpty);
        rvGrid = v.findViewById(R.id.rvGrid);

        rvGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // ✅ constructor adapter cuma nerima OnClick
        adapter = new ProfileGridAdapter(p -> {
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
        rvGrid.setAdapter(adapter);

        btnLogin.setOnClickListener(vv -> {
            if (Session.isLoggedIn(requireContext())) {
                Session.logout(requireContext());
            } else {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
            refresh();
        });

        tabHistory.setOnClickListener(vv -> { currentTab = Tab.HISTORY; refresh(); });
        tabLike.setOnClickListener(vv -> { currentTab = Tab.LIKE; refresh(); });
        tabFav.setOnClickListener(vv -> { currentTab = Tab.FAV; refresh(); });

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

        List<Post> history = HistoryStore.getHistory(requireContext());
        tvStatHistory.setText(String.valueOf(history.size()));

        int likeCount = 0;
        int favCount = 0;

        String user = Session.userKey(requireContext());

        for (Post p : history) {
            String key = (p.slug != null && !p.slug.isEmpty())
                    ? p.slug
                    : (p.videoUrl == null ? "x" : p.videoUrl);

            int like = requireContext().getSharedPreferences("nara_local", 0)
                    .getInt(user + "_like_" + key, 0);

            int fav = requireContext().getSharedPreferences("nara_local", 0)
                    .getInt(user + "_fav_" + key, 0);

            if (like > 0) likeCount++;
            if (fav > 0) favCount++;
        }

        tvStatLike.setText(likeCount == 0 ? "—" : String.valueOf(likeCount));
        tvStatFav.setText(favCount == 0 ? "—" : String.valueOf(favCount));

        List<Post> show = new ArrayList<>();

        if (currentTab == Tab.HISTORY) {
            show = history;
        } else if (currentTab == Tab.LIKE) {
            for (Post p : history) {
                String key = (p.slug != null && !p.slug.isEmpty())
                        ? p.slug
                        : (p.videoUrl == null ? "x" : p.videoUrl);
                int like = requireContext().getSharedPreferences("nara_local", 0)
                        .getInt(user + "_like_" + key, 0);
                if (like > 0) show.add(p);
            }
        } else {
            for (Post p : history) {
                String key = (p.slug != null && !p.slug.isEmpty())
                        ? p.slug
                        : (p.videoUrl == null ? "x" : p.videoUrl);
                int fav = requireContext().getSharedPreferences("nara_local", 0)
                        .getInt(user + "_fav_" + key, 0);
                if (fav > 0) show.add(p);
            }
        }

        // ✅ isi list lewat setItems (harusnya ada di adapter lo)
        adapter.setItems(show);

        tvEmpty.setVisibility(show.isEmpty() ? View.VISIBLE : View.GONE);
        rvGrid.setVisibility(show.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
