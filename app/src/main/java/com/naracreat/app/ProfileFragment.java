package com.naracreat.app;

import android.content.Intent;
import android.content.SharedPreferences;
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

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    private SharedPreferences sp;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sp = requireContext().getSharedPreferences("nara", 0);

        TextView tvStatus = view.findViewById(R.id.tvStatus);
        Button btnToggle = view.findViewById(R.id.btnToggleLogin);

        RecyclerView rvHistory = view.findViewById(R.id.rvHistory);
        RecyclerView rvFav = view.findViewById(R.id.rvFav);

        rvHistory.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvFav.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        PostAdapter historyAdapter = new PostAdapter(new ArrayList<>(), p -> openPlayer(p));
        PostAdapter favAdapter = new PostAdapter(new ArrayList<>(), p -> openPlayer(p));

        rvHistory.setAdapter(historyAdapter);
        rvFav.setAdapter(favAdapter);

        Runnable refresh = () -> {
            boolean logged = sp.getBoolean("logged_in", false);
            tvStatus.setText("Status: " + (logged ? "Login" : "Guest"));

            List<Post> history = HistoryStore.loadHistory(sp);
            historyAdapter.setItems(history);

            List<Post> favs = logged ? HistoryStore.loadFavs(sp) : new ArrayList<>();
            favAdapter.setItems(favs);
        };

        btnToggle.setOnClickListener(v -> {
            boolean logged = sp.getBoolean("logged_in", false);
            sp.edit().putBoolean("logged_in", !logged).apply();
            refresh.run();
        });

        refresh.run();
    }

    private void openPlayer(Post p) {
        Intent i = new Intent(requireContext(), PlayerActivity.class);
        i.putExtra("title", p.title);
        i.putExtra("slug", p.slug);
        i.putExtra("video_url", p.videoUrl);
        i.putExtra("thumbnail_url", p.thumbnailUrl);
        i.putExtra("views", p.views);
        i.putExtra("created_at", p.createdAt);
        i.putExtra("published_at", p.publishedAt);
        i.putExtra("description", p.description);
        startActivity(i);
    }
}
