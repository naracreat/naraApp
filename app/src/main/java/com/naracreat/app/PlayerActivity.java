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

    private TextView tvTitle, tvViews, tvTimeAgo, tvDesc, tvChannelName, tvChannelMeta;
    private Button btnSawer;
    private boolean descOpen = false;

    private RecyclerView rvRelated;
    private RelatedAdapter relatedAdapter;
    private final List<Post> related = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.playerView);

        tvTitle = findViewById(R.id.tvTitle);
        tvViews = findViewById(R.id.tvViews);
        tvTimeAgo = findViewById(R.id.tvTimeAgo);
        tvDesc = findViewById(R.id.tvDescription);

        tvChannelName = findViewById(R.id.tvChannelName);
        tvChannelMeta = findViewById(R.id.tvChannelMeta);
        btnSawer = findViewById(R.id.btnSawer);

        rvRelated = findViewById(R.id.rvRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(this));
        relatedAdapter = new RelatedAdapter(related, p -> openPlayer(p));
        rvRelated.setAdapter(relatedAdapter);

        // ====== data dari Intent (set dari HomeAdapter) ======
        String title = getIntent().getStringExtra("title");
        String videoUrl = getIntent().getStringExtra("video_url");
        String desc = getIntent().getStringExtra("description");
        String views = getIntent().getStringExtra("views");
        String timeAgo = getIntent().getStringExtra("timeAgo");

        if (title == null) title = "Video";
        if (desc == null) desc = "";
        if (views == null) views = "—";
        if (timeAgo == null) timeAgo = "—";

        tvTitle.setText(title);
        tvViews.setText(views);
        tvTimeAgo.setText(timeAgo);
        tvDesc.setText(desc);

        // profile fixed sesuai request
        tvChannelName.setText("ItsNara");
        tvChannelMeta.setText("Support creator • Saweria");

        // toggle deskripsi pas klik judul
        tvTitle.setOnClickListener(v -> toggleDesc());

        // sawer buka di dalam app (WebViewActivity)
        btnSawer.setOnClickListener(v -> {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra("url", "https://saweria.co/Narapoi");
            startActivity(i);
        });

        // ====== init player ======
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

        // ====== TODO: fetch related random ======
        // sementara dummy dulu. Nanti gue kasih function fetch dari /api/posts dan shuffle.
        loadDummyRelated();
    }

    private void toggleDesc() {
        descOpen = !descOpen;
        tvDesc.setVisibility(descOpen ? View.VISIBLE : View.GONE);
    }

    private void openPlayer(Post p) {
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("title", p.title);
        i.putExtra("video_url", p.video_url);
        i.putExtra("description", p.description);
        i.putExtra("views", p.views);
        i.putExtra("timeAgo", p.timeAgo);
        startActivity(i);
    }

    private void loadDummyRelated() {
        related.clear();
        for (int x = 1; x <= 8; x++) {
            Post p = new Post();
            p.title = "Video Random " + x;
            p.views = (1000 * x) + " views";
            p.timeAgo = x + " jam lalu";
            p.video_url = getIntent().getStringExtra("video_url"); // biar bisa play juga (dummy)
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
