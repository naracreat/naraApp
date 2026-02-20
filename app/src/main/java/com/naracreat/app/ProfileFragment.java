package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfileFragment extends Fragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        Button btnLogin = v.findViewById(R.id.btnLogin);
        Button btnRegister = v.findViewById(R.id.btnRegister);

        // Login opsional (sementara cuma UI)
        btnLogin.setOnClickListener(x -> {
            // nanti bisa bikin LoginActivity beneran
        });
        btnRegister.setOnClickListener(x -> {
            // nanti bisa bikin RegisterActivity
        });

        RecyclerView rv = v.findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<HistoryStore.HistoryItem> items = HistoryStore.get(requireContext());
        rv.setAdapter(new HistoryAdapter(items, it -> {
            Intent i = new Intent(getContext(), PlayerActivity.class);
            i.putExtra("title", it.title);
            i.putExtra("video_url", it.videoUrl);
            i.putExtra("thumbnail_url", it.thumbnailUrl);
            i.putExtra("created_at", it.watchedAt);
            i.putExtra("views", 0);
            i.putExtra("description", "—");
            startActivity(i);
        }));
    }
}
