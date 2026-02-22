package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    // ini TextView di XML, tapi kita pegang sebagai View biar aman
    private View btnLogin, tabHistory, tabLike, tabFav;

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

        // guard biar ga crash kalo ada view null
        if (tvName == null || tvSub == null || btnLogin == null ||
                tvStatHistory == null || tvStatLike == null || tvStatFav == null ||
                tabHistory == null || tabLike == null || tabFav == null ||
                tvEmpty == null || rvGrid == null) {
            return;
        }

        // paksa bisa diklik (buat TextView "tombol")
        forceClickable(btnLogin);
        forceClickable(tabHistory);
        forceClickable(tabLike);
        forceClickable(tabFav);

        // kalau ada overlay, angkat ke depan
        btnLogin.bringToFront();
        tabHistory.bringToFront();
        tabLike.bringToFront();
        tabFav.bringToFront();

        rvGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new ProfileGridAdapter(p -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);
            i.putExtra("slug", p.slug);
            startActivity(i);
        });
        rvGrid.setAdapter(adapter);

        btnLogin.setOnClickListener(vv -> {
            if (Session.isLoggedIn(requireContext())) {
                Session.logout(requireContext());
                refresh();
            } else {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
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

    private void forceClickable(View v) {
        v.setEnabled(true);
        v.setClickable(true);
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
    }

    private void setTextIfTextView(View v, String text) {
        if (v instanceof TextView) ((TextView) v).setText(text);
    }

    private void refresh() {
        if (!isAdded() || getContext() == null) return;
        if (tvName == null) return;

        boolean logged = Session.isLoggedIn(requireContext());
        if (!logged) {
            tvName.setText("Tamu");
            tvSub.setText("Masuk untuk sinkron suka & favorit");
            setTextIfTextView(btnLogin, "Masuk");
        } else {
            tvName.setText(Session.getEmail(requireContext()));
            tvSub.setText("Login lokal aktif");
            setTextIfTextView(btnLogin, "Keluar");
        }

        List<Post> history = HistoryStore.getHistory(requireContext());
        if (history == null) history = new ArrayList<>();

        tvStatHistory.setText(String.valueOf(history.size()));

        int likeCount = 0;
        int favCount = 0;

        String user = Session.userKey(requireContext());
        if (user == null) user = "guest";

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

        if (adapter != null) adapter.setItems(show);

        tvEmpty.setVisibility(show.isEmpty() ? View.VISIBLE : View.GONE);
        rvGrid.setVisibility(show.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
