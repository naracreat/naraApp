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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProfileFragment extends Fragment {

    public ProfileFragment() { super(R.layout.fragment_profile); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView tvUser = view.findViewById(R.id.tvUser);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        boolean logged = Session.isLoggedIn(requireContext());
        tvUser.setText(logged ? ("Hi, " + Session.username(requireContext())) : "Guest");

        btnLogin.setVisibility(logged ? View.GONE : View.VISIBLE);
        btnLogout.setVisibility(logged ? View.VISIBLE : View.GONE);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(requireContext(), LoginActivity.class)));
        btnLogout.setOnClickListener(v -> {
            Session.logout(requireContext());
            requireActivity().recreate();
        });

        // History always available
        RecyclerView rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        SimpleTextAdapter historyAdapter = new SimpleTextAdapter();
        rvHistory.setAdapter(historyAdapter);
        historyAdapter.setItems(loadHistory());

        // Likes & Favorites only if logged
        RecyclerView rvLikes = view.findViewById(R.id.rvLikes);
        RecyclerView rvFavs  = view.findViewById(R.id.rvFavs);

        rvLikes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFavs.setLayoutManager(new LinearLayoutManager(requireContext()));

        SimpleTextAdapter likesAdapter = new SimpleTextAdapter();
        SimpleTextAdapter favsAdapter  = new SimpleTextAdapter();

        rvLikes.setAdapter(likesAdapter);
        rvFavs.setAdapter(favsAdapter);

        if (logged) {
            likesAdapter.setItems(loadSet("likes"));
            favsAdapter.setItems(loadSet("favs"));
        } else {
            likesAdapter.setItems(new ArrayList<>());
            favsAdapter.setItems(new ArrayList<>());
        }

        view.findViewById(R.id.boxLikes).setVisibility(logged ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.boxFavs).setVisibility(logged ? View.VISIBLE : View.GONE);
    }

    private List<String> loadHistory() {
        SharedPreferences sp = requireContext().getSharedPreferences("nara_data", 0);
        String csv = sp.getString("history_csv", "");
        List<String> list = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return list;
        for (String x : csv.split("\\|")) {
            if (x != null && !x.trim().isEmpty()) list.add(x.trim());
        }
        return list;
    }

    private List<String> loadSet(String key) {
        SharedPreferences sp = requireContext().getSharedPreferences("nara_data", 0);
        Set<String> set = sp.getStringSet(key, null);
        List<String> list = new ArrayList<>();
        if (set != null) list.addAll(set);
        return list;
    }
}
