package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;

    private TextView tvTitle, tvViews, tvTimeAgo, tvDesc;
    private Button btnSawer;

    private RecyclerView rvRelated;
    private RelatedAdapter relatedAdapter;
    private final List<Post> related = new ArrayList<>();

    private boolean descOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.playerView);
        tvTitle = findViewById(R.id.tvTitle);
        tvViews = findViewById(R.id.tvViews);
        tvTimeAgo = findViewById(R.id.tvTimeAgo);
        tvDesc = findViewById(R.id.tvDescription);
        btnSawer = findViewById(R.id.btnSawer);

        rvRelated = findViewById(R.id.rvRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(this));
        relatedAdapter = new RelatedAdapter(related, this::openPlayer);
        rvRelated.setAdapter(relatedAdapter);

        // ===== Ambil data dari Intent =====
        String title = getIntent().getStringExtra("title");
        String videoUrl = getIntent().getStringExtra("video_url");
        String description = getIntent().getStringExtra("description");
        String views = getIntent().getStringExtra("views");
        String timeAgo = getIntent().getStringExtra("timeAgo");

        if (title == null) title = "Video";
        if (description == null) description = "";
        if (views == null) views = "—";
        if (timeAgo == null) timeAgo = "—";

        tvTitle.setText(title);
        tvViews.setText(views);
        tvTimeAgo.setText(timeAgo);
        tvDesc.setText(description);

        // Toggle deskripsi saat klik judul
        tvTitle.setOnClickListener(v -> toggleDesc());

        // Tombol sawer buka di dalam app
        btnSawer.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra("url", "https://saweria.co/Narapoi");
            startActivity(i);
        });

        // ===== Init Player =====
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

        // ===== Dummy Related (anti error) =====
        loadDummyRelated(videoUrl);
    }

    private void toggleDesc() {
        descOpen = !descOpen;
        tvDesc.setVisibility(descOpen ? View.VISIBLE : View.GONE);
    }

    private void openPlayer(Post p) {
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("title", p.title);
        i.putExtra("video_url", p.videoUrl);
        i.putExtra("description", p.slug); // sementara pakai slug
        i.putExtra("views", p.views != null ? String.valueOf(p.views) : "—");
        i.putExtra("timeAgo", p.publishedAt != null ? p.publishedAt : "—");
        startActivity(i);
    }

    private void loadDummyRelated(String currentVideoUrl) {
        related.clear();

        for (int x = 1; x <= 6; x++) {
            Post p = new Post();
            p.title = "Video Random " + x;
            p.videoUrl = currentVideoUrl;
            p.views = 1000 * x;
            p.publishedAt = "2026-02-20T00:00:00Z";
            related.add(p);
        }

        Collections.shuffle(related);
        relatedAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
