package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateFragment extends Fragment {

    public UpdateFragment() { super(R.layout.fragment_update); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView title = view.findViewById(R.id.tvUpdateTitle);
        title.setText("Update Terbaru");

        RecyclerView rv = view.findViewById(R.id.rvUpdate);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        PostAdapter adapter = new PostAdapter(p -> {
            Intent i = new Intent(requireContext(), PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("slug", p.slug);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);
            i.putExtra("created_at", p.createdAt);
            i.putExtra("published_at", p.publishedAt);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        ApiClient.api().posts(1).enqueue(new Callback<PostResponse>() {
            @Override public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful() && resp.body() != null) {
                    adapter.setItems(resp.body().items);
                }
            }
            @Override public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {}
        });
    }
}
