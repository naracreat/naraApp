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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;

    private TextView tvTitle, tvViews, tvTimeAgo, tvDescription, tvChannelName, tvChannelMeta;
    private Button btnSawer;

    private RecyclerView rvRelated;

    private String videoUrl, title, description, createdAt;
    private int views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // paksa portrait biar "lurus"
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_player);

        bindViews();
        readIntent();
        setupUI();
        setupPlayer();
        setupButtons();
        setupRelatedDummy(); // nanti lo ganti fetch related beneran kalau mau
    }

    private void bindViews() {
        playerView = findViewById(R.id.playerView);

        tvTitle = findViewById(R.id.tvTitle);
        tvViews = findViewById(R.id.tvViews);
        tvTimeAgo = findViewById(R.id.tvTimeAgo);
        tvDescription = findViewById(R.id.tvDescription);

        tvChannelName = findViewById(R.id.tvChannelName);
        tvChannelMeta = findViewById(R.id.tvChannelMeta);

        btnSawer = findViewById(R.id.btnSawer);

        rvRelated = findViewById(R.id.rvRelated);
    }

    private void readIntent() {
        Intent i = getIntent();
        videoUrl = i.getStringExtra("video_url"); // pastikan sender pakai key ini
        title = i.getStringExtra("title");
        description = i.getStringExtra("description");
        createdAt = i.getStringExtra("created_at");
        views = i.getIntExtra("views", 0);

        if (videoUrl == null) videoUrl = "";
        if (title == null) title = "-";
        if (description == null) description = "";
        if (createdAt == null) createdAt = "";
    }

    private void setupUI() {
        tvTitle.setText(title);
        tvViews.setText(formatViews(views));
        tvTimeAgo.setText(TimeAgoUtil.timeAgo(createdAt));

        // default hide
        tvDescription.setVisibility(View.GONE);
        tvDescription.setText(description);

        // klik title toggle deskripsi
        tvTitle.setOnClickListener(v -> {
            if (tvDescription.getVisibility() == View.VISIBLE) {
                tvDescription.setVisibility(View.GONE);
            } else {
                tvDescription.setVisibility(View.VISIBLE);
            }
        });

        tvChannelName.setText("ItsNara");
        tvChannelMeta.setText("ADMIN");

        btnSawer.setOnClickListener(v -> {
            Intent it = new Intent(PlayerActivity.this, SawerActivity.class);
            it.putExtra("url", "https://saweria.co/Narapoi");
            startActivity(it);
        });
    }

    private void setupPlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (!videoUrl.isEmpty()) {
            MediaItem item = MediaItem.fromUri(Uri.parse(videoUrl));
            player.setMediaItem(item);
            player.prepare();
            player.play();
        }
    }

    private void setupButtons() {
        // tombol di layout lo masih TextView (Suka/Favorit/Unduh/Bagikan)
        // Jadi kita ambil via findViewById dengan id baru.
        // Kalau id belum ada, lo tambahin id-nya di XML: tvLike, tvFav, tvDownload, tvShare

        TextView tvLike = findViewById(R.id.tvLike);
        TextView tvFav = findViewById(R.id.tvFav);
        TextView tvDownload = findViewById(R.id.tvDownload);
        TextView tvShare = findViewById(R.id.tvShare);

        SharedPreferences sp = getSharedPreferences("nara", MODE_PRIVATE);
        String keyLike = "like_" + videoUrl;
        String keyFav = "fav_" + videoUrl;

        boolean liked = sp.getBoolean(keyLike, false);
        boolean faved = sp.getBoolean(keyFav, false);

        tvLike.setText(liked ? "Suka" : "Suka");
        tvFav.setText(faved ? "Favorit" : "Favorit");

        tvLike.setOnClickListener(v -> {
            boolean now = !sp.getBoolean(keyLike, false);
            sp.edit().putBoolean(keyLike, now).apply();
            Toast.makeText(this, now ? "Disukai" : "Batal suka", Toast.LENGTH_SHORT).show();
        });

        tvFav.setOnClickListener(v -> {
            boolean now = !sp.getBoolean(keyFav, false);
            sp.edit().putBoolean(keyFav, now).apply();
            Toast.makeText(this, now ? "Masuk favorit" : "Batal favorit", Toast.LENGTH_SHORT).show();
        });

        tvDownload.setOnClickListener(v -> {
            if (videoUrl.isEmpty()) {
                Toast.makeText(this, "URL video kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadVideo(videoUrl, safeFileName(title) + ".mp4");
        });

        tvShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, title + "\n" + videoUrl);
            startActivity(Intent.createChooser(share, "Bagikan"));
        });
    }

    private void downloadVideo(String url, String fileName) {
        try {
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
            req.setTitle(fileName);
            req.setDescription("Downloading...");
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            dm.enqueue(req);
            Toast.makeText(this, "Mulai download", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal download: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRelatedDummy() {
        rvRelated.setLayoutManager(new LinearLayoutManager(this));
        rvRelated.setAdapter(new RelatedAdapter(new ArrayList<>(), p -> {
            Intent it = new Intent(PlayerActivity.this, PlayerActivity.class);
            it.putExtra("video_url", p.videoUrl);
            it.putExtra("title", p.title);
            it.putExtra("description", "");
            it.putExtra("created_at", p.createdAt);
            it.putExtra("views", p.views);
            startActivity(it);
        }));
    }

    private String formatViews(int v) {
        if (v >= 1_000_000) return String.format("%.1fM", v / 1_000_000f);
        if (v >= 1_000) return String.format("%.1fK", v / 1_000f);
        return String.valueOf(v);
    }

    private String safeFileName(String s) {
        return s.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
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
