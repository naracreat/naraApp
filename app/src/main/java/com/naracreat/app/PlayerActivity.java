package com.naracreat.app;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity {

    private ExoPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // paksa portrait
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

        // intent data
        String title = getIntent().getStringExtra("title");
        String slug = getIntent().getStringExtra("slug");
        String videoUrl = getIntent().getStringExtra("video_url");
        String thumbUrl = getIntent().getStringExtra("thumbnail_url");
        int views = getIntent().getIntExtra("views", 0);
        String createdAt = getIntent().getStringExtra("created_at");
        String publishedAt = getIntent().getStringExtra("published_at");
        String desc = getIntent().getStringExtra("description");

        String timeSrc = (publishedAt != null && !publishedAt.isEmpty()) ? publishedAt : createdAt;

        tvTitle.setText(title != null ? title : "—");
        tvViews.setText(views + " views");
        tvTimeAgo.setText(TimeUtil.timeAgo(timeSrc != null ? timeSrc : ""));
        tvDesc.setText(desc != null ? desc : "");
        tvDesc.setVisibility(View.GONE);

        // channel
        Glide.with(this).load(R.mipmap.ic_launcher).into(imgChannel);
        tvChannelName.setText("ItsNara");
        tvChannelMeta.setText("ADMIN");

        // toggle desc on title click
        tvTitle.setOnClickListener(v -> tvDesc.setVisibility(tvDesc.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));

        // Sawer (in-app)
        btnSawer.setOnClickListener(v -> startActivity(new Intent(this, SawerActivity.class)));

        // save history (always)
        saveHistory(title, slug);

        // player
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (videoUrl != null && !videoUrl.trim().isEmpty()) {
            player.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl.trim())));
            player.prepare();
            player.play();
        } else {
            Toast.makeText(this, "Video URL kosong", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sp = getSharedPreferences("nara_data", MODE_PRIVATE);

        // Like/Fav only logged
        updateToggleLabel(btnLike, "Suka", isInSet(sp, "likes", slug));
        updateToggleLabel(btnFav, "Favorit", isInSet(sp, "favs", slug));

        btnLike.setOnClickListener(v -> {
            if (!Session.isLoggedIn(this)) {
                Toast.makeText(this, "Login dulu buat Suka", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean active = toggleSet(sp, "likes", slug);
            updateToggleLabel(btnLike, "Suka", active);
        });

        btnFav.setOnClickListener(v -> {
            if (!Session.isLoggedIn(this)) {
                Toast.makeText(this, "Login dulu buat Favorit", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean active = toggleSet(sp, "favs", slug);
            updateToggleLabel(btnFav, "Favorit", active);
        });

        // Download to HP
        btnDownload.setOnClickListener(v -> {
            if (videoUrl == null || videoUrl.trim().isEmpty()) return;

            try {
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request r = new DownloadManager.Request(Uri.parse(videoUrl.trim()));
                r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                String fname = "NaraApp_" + System.currentTimeMillis() + ".mp4";
                r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fname);
                dm.enqueue(r);

                Toast.makeText(this, "Download dimulai…", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Gagal download", Toast.LENGTH_SHORT).show();
            }
        });

        // Share link APP (bukan CDN)
        String shareLink = "https://narahentai.pages.dev/watch?slug=" + (slug != null ? Uri.encode(slug) : "");
        btnShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, (title != null ? title : "Video") + "\n" + shareLink);
            startActivity(Intent.createChooser(share, "Bagikan"));
        });

        // also copy to clipboard on long press
        btnShare.setOnLongClickListener(v -> {
            ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cb.setPrimaryClip(ClipData.newPlainText("link", shareLink));
            Toast.makeText(this, "Link disalin", Toast.LENGTH_SHORT).show();
            return true;
        });

        // Related list (use thumbnails from API)
        RelatedAdapter relatedAdapter = new RelatedAdapter(new ArrayList<>(), p -> {
            Intent i = new Intent(this, PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("slug", p.slug);
            i.putExtra("video_url", p.videoUrl);
            i.putExtra("thumbnail_url", p.thumbnailUrl);
            i.putExtra("views", p.views != null ? p.views : 0);
            i.putExtra("created_at", p.createdAt);
            i.putExtra("published_at", p.publishedAt);
            i.putExtra("description", "");
            startActivity(i);
            finish();
        });
        rvRelated.setAdapter(relatedAdapter);

        ApiClient.api().posts(1).enqueue(new Callback<PostResponse>() {
            @Override public void onResponse(Call<PostResponse> call, Response<PostResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null || resp.body().items == null) return;

                List<Post> list = new ArrayList<>();
                for (Post p : resp.body().items) {
                    if (slug != null && slug.equals(p.slug)) continue;
                    list.add(p);
                }
                relatedAdapter.setItems(list);
            }
            @Override public void onFailure(Call<PostResponse> call, Throwable t) {}
        });

        // show thumb in player background (optional)
        if (thumbUrl != null && !thumbUrl.isEmpty()) {
            ImageView art = playerView.findViewById(androidx.media3.ui.R.id.exo_artwork);
            if (art != null) {
                Glide.with(this).load(thumbUrl).into(art);
            }
        }
    }

    private void saveHistory(String title, String slug) {
        SharedPreferences sp = getSharedPreferences("nara_data", MODE_PRIVATE);
        String key = (slug != null && !slug.isEmpty()) ? slug : (title != null ? title : "unknown");
        String csv = sp.getString("history_csv", "");
        if (csv == null) csv = "";

        // prepend unique
        String[] parts = csv.split("\\|");
        StringBuilder sb = new StringBuilder();
        sb.append(key);

        int added = 1;
        for (String p : parts) {
            if (p == null || p.trim().isEmpty()) continue;
            if (p.trim().equals(key)) continue;
            if (added >= 20) break;
            sb.append("|").append(p.trim());
            added++;
        }

        sp.edit().putString("history_csv", sb.toString()).apply();
    }

    private boolean isInSet(SharedPreferences sp, String setKey, String slug) {
        if (slug == null || slug.isEmpty()) return false;
        Set<String> set = sp.getStringSet(setKey, new LinkedHashSet<>());
        return set != null && set.contains(slug);
    }

    private boolean toggleSet(SharedPreferences sp, String setKey, String slug) {
        if (slug == null || slug.isEmpty()) return false;
        Set<String> old = sp.getStringSet(setKey, new LinkedHashSet<>());
        Set<String> set = new LinkedHashSet<>();
        if (old != null) set.addAll(old);

        boolean active;
        if (set.contains(slug)) {
            set.remove(slug);
            active = false;
        } else {
            set.add(slug);
            active = true;
        }
        sp.edit().putStringSet(setKey, set).apply();
        return active;
    }

    private void updateToggleLabel(TextView tv, String label, boolean active) {
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
