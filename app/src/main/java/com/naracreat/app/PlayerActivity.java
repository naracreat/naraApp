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
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;

    private TextView tvTitle, tvMeta, tvDesc, tvChannelName, tvChannelRole;
    private ImageView imgChannel, btnFullscreen;

    private View btnSawer, btnShare, btnDownload, btnLike, btnDislike, btnFav;

    private RecyclerView rvRelated;
    private RelatedAdapter relatedAdapter;

    private SharedPreferences sp;
    private Post current;

    private boolean descOpen = false;
    private boolean isFullscreen = false;

    // Link aplikasi kamu (buat share). Ubah sesuai domain/link kamu.
    private static final String APP_LINK_BASE = "https://narahentai.pages.dev/app/";
    private static final String SAWERIA_URL = "https://saweria.co/Narapoi";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        sp = getSharedPreferences("nara_local", MODE_PRIVATE);

        bindViews();
        readIntentPost();
        setupPlayer();
        setupUi();
        loadRelated();
    }

    private void bindViews() {
        playerView = findViewById(R.id.playerView);
        btnFullscreen = findViewById(R.id.btnFullscreen);

        tvTitle = findViewById(R.id.tvTitle);
        tvMeta = findViewById(R.id.tvMeta);
        tvDesc = findViewById(R.id.tvDesc);

        imgChannel = findViewById(R.id.imgChannel);
        tvChannelName = findViewById(R.id.tvChannelName);
        tvChannelRole = findViewById(R.id.tvChannelRole);

        btnSawer = findViewById(R.id.btnSawer);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);
        btnLike = findViewById(R.id.btnLike);
        btnDislike = findViewById(R.id.btnDislike);
        btnFav = findViewById(R.id.btnFav);

        rvRelated = findViewById(R.id.rvRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(this));
        relatedAdapter = new RelatedAdapter(new ArrayList<>(), this::openAnother);
        rvRelated.setAdapter(relatedAdapter);
    }

    private void readIntentPost() {
        Intent i = getIntent();

        current = new Post();
        current.title = i.getStringExtra("title");
        current.videoUrl = i.getStringExtra("video_url");
        current.thumbnailUrl = i.getStringExtra("thumbnail_url");
        current.views = i.hasExtra("views") ? i.getIntExtra("views", 0) : 0;
        current.createdAt = i.getStringExtra("created_at");
        current.publishedAt = i.getStringExtra("published_at");
        current.slug = i.getStringExtra("slug");
        current.durationMinutes = i.hasExtra("duration_minutes") ? i.getIntExtra("duration_minutes", 0) : 0;

        String desc = i.getStringExtra("description");
        if (desc == null || desc.trim().isEmpty()) desc = "Tidak ada deskripsi.";
        tvDesc.setText(desc);
    }

    private void setupPlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (current.videoUrl != null && !current.videoUrl.trim().isEmpty()) {
            MediaItem item = MediaItem.fromUri(Uri.parse(current.videoUrl));
            player.setMediaItem(item);
            player.prepare();
            player.play();
        }
    }

    private void setupUi() {
        // Title + meta
        tvTitle.setText(current.title != null ? current.title : "—");

        String when = (current.createdAt != null && !current.createdAt.isEmpty()) ? current.createdAt : current.publishedAt;
        String meta = (current.views != null ? current.views : 0) + " ditonton • " + TimeUtil.timeAgo(when);
        tvMeta.setText(meta);

        // Toggle description
        View.OnClickListener toggleDesc = v -> {
            descOpen = !descOpen;
            tvDesc.setVisibility(descOpen ? View.VISIBLE : View.GONE);
            tvTitle.setMaxLines(descOpen ? 20 : 2);
        };
        tvTitle.setOnClickListener(toggleDesc);
        tvMeta.setOnClickListener(toggleDesc);

        // Channel info (sementara)
        tvChannelName.setText("ItsNara");
        tvChannelRole.setText("ADMIN");
        Glide.with(this).load(R.mipmap.ic_launcher).into(imgChannel);

        // Refresh counts
        refreshActionCounts();

        // Sawer: buka Saweria tetap di dalam aplikasi (WebViewActivity)
        btnSawer.setOnClickListener(v -> {
            Intent w = new Intent(this, WebViewActivity.class);
            w.putExtra("url", SAWERIA_URL);
            startActivity(w);
        });

        // Bagikan
        btnShare.setOnClickListener(v -> shareAppLink());

        // Unduh
        btnDownload.setOnClickListener(v -> downloadVideo());

        // Like / Dislike / Favorit (sementara local)
        btnLike.setOnClickListener(v -> incCount("like_"));
        btnDislike.setOnClickListener(v -> incCount("dislike_"));
        btnFav.setOnClickListener(v -> incCount("fav_"));

        // Fullscreen
        btnFullscreen.setOnClickListener(v -> toggleFullscreen());
    }

    private void incCount(String prefix) {
        if (current.slug == null || current.slug.trim().isEmpty()) return;
        String key = prefix + current.slug;
        int val = sp.getInt(key, 0);
        sp.edit().putInt(key, val + 1).apply();
        refreshActionCounts();
    }

    private void refreshActionCounts() {
        if (current.slug == null || current.slug.trim().isEmpty()) {
            setBtnText(btnLike, "Like 0");
            setBtnText(btnDislike, "Dislike 0");
            setBtnText(btnFav, "Favorit 0");
            return;
        }

        int like = sp.getInt("like_" + current.slug, 0);
        int dislike = sp.getInt("dislike_" + current.slug, 0);
        int fav = sp.getInt("fav_" + current.slug, 0);

        setBtnText(btnLike, "Like " + like);
        setBtnText(btnDislike, "Dislike " + dislike);
        setBtnText(btnFav, "Favorit " + fav);
    }

    private void setBtnText(View v, String text) {
        try {
            ((TextView) v).setText(text);
        } catch (Exception ignored) {
        }
    }

    private void shareAppLink() {
        String slug = (current.slug != null && !current.slug.trim().isEmpty()) ? current.slug : "home";
        String link = APP_LINK_BASE + slug;

        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(send, "Bagikan"));
    }

    private void downloadVideo() {
        if (current.videoUrl == null || current.videoUrl.trim().isEmpty()) return;

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(current.videoUrl);

        DownloadManager.Request req = new DownloadManager.Request(uri);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        req.setAllowedOverRoaming(true);
        req.setAllowedOverMetered(true);

        String name = (current.slug != null && !current.slug.trim().isEmpty()) ? current.slug : "video";
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "nara_" + name + ".mp4");

        if (dm != null) dm.enqueue(req);
    }

    private void toggleFullscreen() {
        isFullscreen = !isFullscreen;

        if (isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            hideSystemUi();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            showSystemUi();
        }
    }

    private void hideSystemUi() {
        try {
            View decor = getWindow().getDecorView();
            WindowInsetsController c = decor.getWindowInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } catch (Exception ignored) {
        }
    }

    private void showSystemUi() {
        try {
            View decor = getWindow().getDecorView();
            WindowInsetsController c = decor.getWindowInsetsController();
            if (c != null) c.show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } catch (Exception ignored) {
        }
    }

    private void openAnother(Post p) {
        Intent i = new Intent(this, PlayerActivity.class);
        i.putExtra("title", p.title);
        i.putExtra("video_url", p.videoUrl);
        i.putExtra("thumbnail_url", p.thumbnailUrl);
        i.putExtra("views", p.views != null ? p.views : 0);
        i.putExtra("created_at", p.createdAt);
        i.putExtra("published_at", p.publishedAt);
        i.putExtra("slug", p.slug);
        i.putExtra("duration_minutes", p.durationMinutes != null ? p.durationMinutes : 0);
        startActivity(i);
    }

    private void loadRelated() {
        // rekomendasi simple: ambil page 1, tampilkan list (kecuali current slug)
        ApiClient.api().getPosts(1).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    try {
                        PostResponse pr = (PostResponse) resp.body();
                        if (pr.items == null) return;

                        List<Post> out = new ArrayList<>();
                        for (Object o : pr.items) {
                            Post p = (Post) o;
                            if (current.slug != null && p.slug != null && current.slug.equals(p.slug)) continue;
                            out.add(p);
                        }
                        relatedAdapter.setItems(out);
                    } catch (Exception ignored) {
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
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
