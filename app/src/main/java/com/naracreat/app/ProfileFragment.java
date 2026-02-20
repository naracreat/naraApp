package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvSub, btnLogin;
    private TextView tvStatHistory, tvStatLike, tvStatFav;
    private TextView tabHistory, tabLike, tabFav;
    private TextView tvEmpty;
    private RecyclerView rvGrid;

    private ProfileGridAdapter adapter;

    // sementara: login belum dibuat, jadi selalu guest
    private boolean isLoggedIn = false;

    private enum Tab { HISTORY, LIKE, FAV }
    private Tab currentTab = Tab.HISTORY;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

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

        rvGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new ProfileGridAdapter(this::openPlayer);
        rvGrid.setAdapter(adapter);

        btnLogin.setOnClickListener(x -> {
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Login belum dibuat. Nanti kita aktifin.", Toast.LENGTH_SHORT).show();
            } else {
                isLoggedIn = false;
                refreshHeader();
                loadTab(Tab.HISTORY);
            }
        });

        tabHistory.setOnClickListener(x -> loadTab(Tab.HISTORY));
        tabLike.setOnClickListener(x -> loadTab(Tab.LIKE));
        tabFav.setOnClickListener(x -> loadTab(Tab.FAV));

        refreshHeader();
        loadTab(Tab.HISTORY);

        return v;
    }

    private void refreshHeader() {
        if (!isLoggedIn) {
            tvName.setText("Tamu");
            tvSub.setText("Masuk untuk sinkron suka & favorit");
            btnLogin.setText("Masuk");
        } else {
            tvName.setText("User");
            tvSub.setText("Akun aktif");
            btnLogin.setText("Keluar");
        }

        // stat (riwayat lokal)
        List<Post> history = HistoryStore.load(requireContext());
        tvStatHistory.setText(String.valueOf(history.size()));
        tvStatLike.setText(isLoggedIn ? "0" : "—");
        tvStatFav.setText(isLoggedIn ? "0" : "—");
    }

    private void setTabUI(Tab t) {
        // active/inactive
        tabHistory.setTextColor(getResources().getColor(t == Tab.HISTORY ? android.R.color.white : R.color.text));
        tabLike.setTextColor(getResources().getColor(t == Tab.LIKE ? android.R.color.white : R.color.text));
        tabFav.setTextColor(getResources().getColor(t == Tab.FAV ? android.R.color.white : R.color.text));

        tabHistory.setBackgroundResource(t == Tab.HISTORY ? R.drawable.tab_active : R.drawable.tab_inactive);
        tabLike.setBackgroundResource(t == Tab.LIKE ? R.drawable.tab_active : R.drawable.tab_inactive);
        tabFav.setBackgroundResource(t == Tab.FAV ? R.drawable.tab_active : R.drawable.tab_inactive);
    }

    private void loadTab(Tab t) {
        currentTab = t;
        setTabUI(t);

        if (t == Tab.HISTORY) {
            List<Post> list = HistoryStore.load(requireContext());
            showList(list, list.isEmpty() ? "Belum ada riwayat ditonton" : null);
            refreshHeader();
            return;
        }

        // Like/Fav: harus login
        if (!isLoggedIn) {
            adapter.setItems(new ArrayList<>());
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Login dulu untuk melihat " + (t == Tab.LIKE ? "Suka" : "Favorit"));
            return;
        }

        // nanti kalau login + backend siap: load dari server/local
        adapter.setItems(new ArrayList<>());
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText("Belum ada data");
    }

    private void showList(List<Post> list, String emptyMsg) {
        adapter.setItems(list);
        if (list == null || list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(emptyMsg != null ? emptyMsg : "Belum ada data");
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void openPlayer(Post p) {
        if (getContext() == null || p == null) return;
        Intent i = new Intent(getContext(), PlayerActivity.class);
        i.putExtra("title", p.title);
        i.putExtra("video_url", p.videoUrl);
        i.putExtra("thumbnail_url", p.thumbnailUrl);
        i.putExtra("views", p.views != null ? p.views : 0);
        i.putExtra("created_at", p.createdAt != null ? p.createdAt : (p.publishedAt != null ? p.publishedAt : ""));
        i.putExtra("description", p.description != null ? p.description : "");
        startActivity(i);
    }
}
