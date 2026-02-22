package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView rvGrid;
    private TextView tvEmpty;
    private ProfileGridAdapter adapter;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {

        try {
            rvGrid = v.findViewById(R.id.rvGrid);
            tvEmpty = v.findViewById(R.id.tvEmpty);

            if (rvGrid == null || tvEmpty == null) {
                Toast.makeText(getContext(), "Layout ID salah", Toast.LENGTH_LONG).show();
                return;
            }

            rvGrid.setLayoutManager(new GridLayoutManager(requireContext(), 2));

            adapter = new ProfileGridAdapter(p -> {
                Intent i = new Intent(requireContext(), PlayerActivity.class);
                i.putExtra("title", p.title);
                i.putExtra("video_url", p.videoUrl);
                startActivity(i);
            });

            rvGrid.setAdapter(adapter);

            List<Post> history = HistoryStore.getHistory(requireContext());
            if (history == null) history = new ArrayList<>();

            adapter.setItems(history);

            tvEmpty.setVisibility(history.isEmpty() ? View.VISIBLE : View.GONE);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Crash: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
