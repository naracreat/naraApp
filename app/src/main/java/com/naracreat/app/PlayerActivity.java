
package com.naracreat.app;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
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

    private ExoPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // PAKSA PORTRAIT
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_player);

        PlayerView playerView = findViewById(R.id.playerView);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvViews = findViewById(R.id.tvViews);
        TextView tvTimeAgo = findViewById(R.id.tvTimeAgo);
        TextView tvDesc = findViewById(R.id.tvDescription);
        ImageView imgChannel = findViewById(R.id.imgChannel);
        TextView tvChannelName = findViewById(R.id.tvChannelName);
        TextView tvChannelMeta = findViewById(R.id.tvChannelMeta);
        Button btnSawer = findViewById(R.id.btnSawer);

        TextView btnLike = findViewById(R.id.btnLike);
        TextView btnFav = findViewById(R.id.btnFav);
        TextView btnDownload = findViewById(R.id.btnDownload);
        TextView btnShare = findViewById(R.id.btnShare);

        RecyclerView rvRelated = findViewById(R.id.rvRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(this));

        // DATA DARI INTENT
        String title = getIntent().getStringExtra("title");
        String videoUrl = getIntent().getStringExtra("video_url");
        String createdAt = getIntent().getStringExtra("created_at");
        int views = getIntent().getIntExtra("views", 0);
        String desc = getIntent().getStringExtra("description");

        tvTitle.setText(title != null ? title : "—");
        tvViews.setText(String.valueOf(views));
        tvTimeAgo.setText(TimeUtil.timeAgo(createdAt != null ? createdAt : ""));
        tvDesc.setText(desc != null ? desc : "—");

        tvChannelName.setText("ItsNara");
        tvChannelMeta.setText("ADMIN"); // FIX: jadi ADMIN

        // Title toggle desc
        tvTitle.setOnClickListener(v -> {
            tvDesc.setVisibility(tvDesc.getVisibility() == android.view.View.VISIBLE
                    ? android.view.View.GONE : android.view.View.VISIBLE);
        });

        // Sawer in-app
        btnSawer.setOnClickListener(v -> startActivity(new Intent(this, SawerActivity.class)));

        // PLAYER
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        if (videoUrl != null && !videoUrl.isEmpty()) {
            player.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)));
            player.prepare();
            player.play();
        }

        // LIKE / FAV (local)
        SharedPreferences sp = getSharedPreferences("nara", MODE_PRIVATE);
        String keyLike = "like_" + (title != null ? title : "");
        String keyFav = "fav_" + (title != null ? title : "");

        updateToggleText(btnLike, "Suka", sp.getBoolean(keyLike, false));
        updateToggleText(btnFav, "Favorit", sp.getBoolean(keyFav, false));

        btnLike.setOnClickListener(v -> {
            boolean newVal = !sp.getBoolean(keyLike, false);
            sp.edit().putBoolean(keyLike, newVal).apply();
            updateToggleText(btnLike, "Suka", newVal);
        });

        btnFav.setOnClickListener(v -> {
            boolean newVal = !sp.getBoolean(keyFav, false);
            sp.edit().putBoolean(keyFav, newVal).apply();
            updateToggleText(btnFav, "Favorit", newVal);
        });

        // DOWNLOAD
        btnDownload.setOnClickListener(v -> {
            if (videoUrl == null || videoUrl.isEmpty()) return;

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request r = new DownloadManager.Request(Uri.parse(videoUrl));
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    "NaraApp_" + System.currentTimeMillis() + ".mp4");
            dm.enqueue(r);
        });

        // SHARE
        btnShare.setOnClickListener(v -> {
            if (videoUrl == null) return;
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, (title != null ? title : "Video") + "\n" + videoUrl);
            startActivity(Intent.createChooser(share, "Bagikan"));
        });

        // RELATED LIST ambil dari API (random: page 1)
        RelatedAdapter relatedAdapter = new RelatedAdapter(new ArrayList<>(), p -> {
            Intent i = new Intent(this, PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);
            i.putExtra("created_at", p.createdAt != null ? p.createdAt : (p.publishedAt != null ? p.publishedAt : ""));
            i.putExtra("description", "—");
            startActivity(i);
            finish();
        });
        rvRelated.setAdapter(relatedAdapter);

        ApiClient.api().posts(1).enqueue(new Callback<PostsResponse>() {
            @Override public void onResponse(Call<PostsResponse> call, Response<PostsResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().items != null) {
                    relatedAdapter.notifyDataSetChanged();
                    // hack simpel: replace list via reflection ga enak, jadi bikin adapter ulang:
                    rvRelated.setAdapter(new RelatedAdapter(resp.body().items, relatedAdapter::onClick));
                }
            }
            @Override public void onFailure(Call<PostsResponse> call, Throwable t) {}
        });
    }

    private void updateToggleText(TextView tv, String label, boolean active) {
        tv.setText(active ? (label + " (ON)") : label);
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
