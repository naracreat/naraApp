package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvPosts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        rvPosts = v.findViewById(R.id.rvPosts);
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        loadPosts();

        return v;
    }

    private void loadPosts() {
        ApiService api = ApiClient.get().create(ApiService.class);

        api.getPosts(1).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call,
                                   Response<PostResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    PostAdapter adapter = new PostAdapter(
                            response.body().items,
                            post -> {
                                Intent i = new Intent(getContext(), PlayerActivity.class);
                                i.putExtra("title", post.title);
                                i.putExtra("video_url", post.videoUrl);
                                i.putExtra("created_at", post.createdAt);
                                i.putExtra("views", post.views != null ? post.views : 0);
                                startActivity(i);
                            });

                    rvPosts.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) { }
        });
    }
}
