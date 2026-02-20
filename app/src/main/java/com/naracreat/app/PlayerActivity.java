package com.naracreat.app;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private boolean isFullscreen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // portrait default
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_player);

        SharedPreferences sp = getSharedPreferences("nara", MODE_PRIVATE);

        PlayerView playerView = findViewById(R.id.playerView);
        View playerFrame = findViewById(R.id.playerFrame);
        ImageButton btnFullscreen = findViewById(R.id.btnFullscreen);

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvViews = findViewById(R.id.tvViews);
        TextView tvTimeAgo = findViewById(R.id.tvTimeAgo);
        TextView tvDesc = findViewById(R.id.tvDescription);

        TextView tvChannelMeta = findViewById(R.id.tvChannelMeta);
        tvChannelMeta.setText("ADMIN");

        TextView btnLike = findViewById(R.id.btnLike);
        TextView btnFav = findViewById(R.id.btnFav);
        TextView btnDownload = findViewById(R.id.btnDownload);
        TextView btnShare = findViewById(R.id.btnShare);

        findViewById(R.id.btnSawer).setOnClickListener(v ->
                startActivity(new Intent(this, SawerActivity.class))
        );

        RecyclerView rvRelated = findViewById(R.id.rvRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(this));

        // extras
        String title = getIntent().getStringExtra("title");
        String slug = getIntent().getStringExtra("slug");
        String videoUrl = getIntent().getStringExtra("video_url");
        String createdAt = getIntent().getStringExtra("created_at");
        String publishedAt = getIntent().getStringExtra("published_at");
        int views = getIntent().getIntExtra("views", 0);
        String desc = getIntent().getStringExtra("description");
        String thumb = getIntent().getStringExtra("thumbnail_url");

        tvTitle.setText(title != null ? title : "—");
        tvViews.setText(String.valueOf(views));
        String when = (publishedAt != null && !publishedAt.isEmpty()) ? publishedAt : createdAt;
        tvTimeAgo.setText(TimeUtil.timeAgo(when != null ? when : ""));
        tvDesc.setText(desc != null ? desc : "");

        // toggle desc on title click
        tvTitle.setOnClickListener(v -> {
            tvDesc.setVisibility(tvDesc.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        // build Post for history/fav
        Post current = new Post();
        current.title = title;
        current.slug = slug;
        current.videoUrl = videoUrl;
        current.thumbnailUrl = thumb;
        current.views = views;
        current.createdAt = createdAt;
        current.publishedAt = publishedAt;
        current.description = desc;

        // push history always
        HistoryStore.pushHistory(sp, current);

        // fullscreen toggle
        btnFullscreen.setOnClickListener(v -> toggleFullscreen(playerFrame));

        // init player
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (videoUrl != null && !videoUrl.isEmpty()) {
            player.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)));
            player.prepare();
            player.play();
        } else {
            Toast.makeText(this, "Video URL kosong", Toast.LENGTH_SHORT).show();
        }

        // like/fav require login
        boolean logged = sp.getBoolean("logged_in", false);
        refreshButtons(sp, logged, current, btnLike, btnFav);

        btnLike.setOnClickListener(v -> {
            boolean isLogged = sp.getBoolean("logged_in", false);
            if (!isLogged) {
                Toast.makeText(this, "Login dulu untuk Suka", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean on = !sp.getBoolean("like_" + safe(slug), false);
            sp.edit().putBoolean("like_" + safe(slug), on).apply();
            Toast.makeText(this, on ? "Disukai" : "Batal suka", Toast.LENGTH_SHORT).show();
            refreshButtons(sp, true, current, btnLike, btnFav);
        });

        btnFav.setOnClickListener(v -> {
            boolean isLogged = sp.getBoolean("logged_in", false);
            if (!isLogged) {
                Toast.makeText(this, "Login dulu untuk Favorit", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean on = !sp.getBoolean("fav_" + safe(slug), false);
            sp.edit().putBoolean("fav_" + safe(slug), on).apply();
            HistoryStore.setFav(sp, current, on);
            Toast.makeText(this, on ? "Masuk favorit" : "Hapus favorit", Toast.LENGTH_SHORT).show();
            refreshButtons(sp, true, current, btnLike, btnFav);
        });

        // download
        btnDownload.setOnClickListener(v -> {
            if (videoUrl == null || videoUrl.isEmpty()) return;

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request r = new DownloadManager.Request(Uri.parse(videoUrl));
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    "NaraApp_" + System.currentTimeMillis() + ".mp4");
            dm.enqueue(r);

            Toast.makeText(this, "Mulai unduh…", Toast.LENGTH_SHORT).show();
        });

        // share => link app (web watch slug)
        btnShare.setOnClickListener(v -> {
            String shareUrl = "https://narahentai.pages.dev/watch?slug=" + Uri.encode(safe(slug));
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, (title != null ? title : "Video") + "\n" + shareUrl);
            startActivity(Intent.createChooser(share, "Bagikan"));
        });

        // related list (page 1)
        RelatedAdapter related = new RelatedAdapter(new java.util.ArrayList<>(), p -> {
            Intent i = new Intent(this, PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("slug", p.slug);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views);
            i.putExtra("created_at", p.createdAt);
            i.putExtra("published_at", p.publishedAt);
            i.putExtra("description", p.description);
            startActivity(i);
            finish();
        });
        rvRelated.setAdapter(related);

        ApiClient.api().getPosts(1).enqueue(new retrofit2.Callback<PostResponse>() {
            @Override public void onResponse(retrofit2.Call<PostResponse> call, retrofit2.Response<PostResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().items != null) {
                    java.util.ArrayList<Post> list = new java.util.ArrayList<>();
                    for (Post p : resp.body().items) {
                        if (p != null && p.slug != null && !p.slug.equals(slug)) list.add(p);
                        if (list.size() >= 12) break;
                    }
                    related.setItems(list);
                }
            }
            @Override public void onFailure(retrofit2.Call<PostResponse> call, Throwable t) {}
        });
    }

    private void refreshButtons(SharedPreferences sp, boolean logged, Post current, TextView btnLike, TextView btnFav) {
        String slug = current != null ? current.slug : "";
        boolean like = logged && sp.getBoolean("like_" + safe(slug), false);
        boolean fav = logged && sp.getBoolean("fav_" + safe(slug), false);

        btnLike.setText(like ? "Suka (On)" : "Suka");
        btnFav.setText(fav ? "Favorit (On)" : "Favorit");
    }

    private void toggleFullscreen(View playerFrame) {
        isFullscreen = !isFullscreen;

        if (isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
            ViewGroup.LayoutParams lp = playerFrame.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            playerFrame.setLayoutParams(lp);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            ViewGroup.LayoutParams lp = playerFrame.getLayoutParams();
            lp.height = dp(220);
            playerFrame.setLayoutParams(lp);
        }
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (v * d);
    }

    private String safe(String s) {
        return s == null ? "" : s;
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
