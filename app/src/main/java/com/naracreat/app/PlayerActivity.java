package com.naracreat.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity {

    ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        PlayerView playerView = findViewById(R.id.playerView);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvMeta = findViewById(R.id.tvTimeAgo);

        String title = getIntent().getStringExtra("title");
        String videoRaw = getIntent().getStringExtra("video_url");
        String created = getIntent().getStringExtra("created_at");
        int views = getIntent().getIntExtra("views", 0);

        String videoUrl = UrlUtil.abs(videoRaw);

        tvTitle.setText(title);
        tvMeta.setText(views + " • " + TimeUtil.timeAgo(created));

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (videoUrl != null) {
            player.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)));
            player.prepare();
            player.play();
        }

        RecyclerView rvRelated = findViewById(R.id.rvRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(this));

        RelatedAdapter relatedAdapter = new RelatedAdapter(new ArrayList<>(), p -> {
            Intent i = new Intent(this, PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);
            i.putExtra("created_at", p.createdAt);
            startActivity(i);
            finish();
        });

        rvRelated.setAdapter(relatedAdapter);

        ApiClient.api().posts(1, null, null).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    relatedAdapter.setItems(resp.body().items);
                }
            }
            @Override public void onFailure(Call<PostResponse> call, Throwable t) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) player.release();
    }
}
