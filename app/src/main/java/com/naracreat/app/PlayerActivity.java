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
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

<<<<<<< HEAD
// ====== ADMOB (ADDED) ======
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
// ===========================
=======
// AdMob
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
>>>>>>> e7c5cb9 (AdMob: interstitial before play + rewarded before download)

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;

    private FrameLayout videoContainer;
    private ScrollView contentScroll;

    private TextView tvTitle, tvMeta, tvDesc, tvChannelName, tvChannelRole;
    private ImageView imgChannel, btnFullscreen;

    private View btnSawer, btnShare, btnDownload, btnLike, btnFav;

    private RecyclerView rvRelated;
    private RelatedAdapter relatedAdapter;

    private SharedPreferences sp;
    private Post current;

    private boolean descOpen = false;
    private boolean isFullscreen = false;

    private static final String APP_LINK_BASE = "https://narahentai.pages.dev/app/";
    private static final String SAWERIA_URL = "https://saweria.co/Narapoi";

    // ===== AdMob IDs =====
    private static final String ADMOB_INTERSTITIAL_ID = "ca-app-pub-2949781994076653/5848133445";
    private static final String ADMOB_REWARDED_INTERSTITIAL_ID = "ca-app-pub-2949781994076653/2967323817";

    private InterstitialAd interstitialAd;
    private RewardedInterstitialAd rewardedInterstitialAd;

    private boolean pendingPlay = false;
    private boolean pendingDownload = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // ====== ADMOB (ADDED) ======
        try {
            MobileAds.initialize(this, status -> {});
            AdView adView = findViewById(R.id.adViewPlayer);
            if (adView != null) {
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }
        } catch (Exception ignored) {}
        // ===========================

        sp = getSharedPreferences("nara_local", MODE_PRIVATE);

        bindViews();
        readIntentPost();

        // 2) riwayat: ambil dari yg terakhir ditonton dari player
        HistoryStore.addWatched(this, current);

        setupPlayer();
        setupUi();

        // Ads: init + load, lalu tampilkan interstitial sebelum play
        initAndLoadAds();
        maybeShowInterstitialThenPlay();

        loadRelated();
    }

    private void bindViews() {
        videoContainer = findViewById(R.id.videoContainer);
        contentScroll = findViewById(R.id.contentScroll);

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
        current.slug = i.getStringExtra("slug");
        current.createdAt = i.getStringExtra("created_at");
        current.publishedAt = i.getStringExtra("published_at");
        current.durationMinutes = i.hasExtra("duration_minutes") ? i.getIntExtra("duration_minutes", 0) : 0;

        int views = i.hasExtra("views") ? i.getIntExtra("views", 0) : 0;
        current.views = views;

        String desc = i.getStringExtra("description");
        if (desc == null || desc.trim().isEmpty()) {
            String when = (current.createdAt != null && !current.createdAt.isEmpty()) ? current.createdAt : current.publishedAt;
            desc = "Info:\n"
                    + "- Ditonton: " + views + "\n"
                    + "- Upload: " + (when == null ? "-" : when) + "\n"
                    + "- Link video: " + (current.videoUrl == null ? "-" : current.videoUrl);
        }
        tvDesc.setText(desc);
    }

    private void setupPlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        if (current.videoUrl != null && !current.videoUrl.trim().isEmpty()) {
            MediaItem item = MediaItem.fromUri(Uri.parse(current.videoUrl));
            player.setMediaItem(item);
            player.prepare();
            // NOTE: jangan play dulu, nanti play setelah interstitial selesai
        }
    }

    private void setupUi() {
        tvTitle.setText(current.title != null ? current.title : "—");

        String when = (current.createdAt != null && !current.createdAt.isEmpty()) ? current.createdAt : current.publishedAt;
        int v = current.views != null ? current.views : 0;
        tvMeta.setText(v + " ditonton • " + TimeUtil.timeAgo(when));

        View.OnClickListener toggleDesc = vv -> {
            descOpen = !descOpen;
            tvDesc.setVisibility(descOpen ? View.VISIBLE : View.GONE);
            tvTitle.setMaxLines(descOpen ? 20 : 2);
        };
        tvTitle.setOnClickListener(toggleDesc);
        tvMeta.setOnClickListener(toggleDesc);

        tvChannelName.setText("ItsNara");
        tvChannelRole.setText("ADMIN");
        Glide.with(this).load(R.mipmap.ic_launcher).into(imgChannel);

        refreshActionCounts();

        btnSawer.setOnClickListener(vv -> {
            Intent w = new Intent(this, WebViewActivity.class);
            w.putExtra("url", SAWERIA_URL);
            startActivity(w);
        });

        btnLike.setOnClickListener(vv -> incCount("like_"));
        btnFav.setOnClickListener(vv -> incCount("fav_"));

        // Rewarded interstitial pas klik Unduh
        btnDownload.setOnClickListener(vv -> showRewardedThenDownload());

        btnShare.setOnClickListener(vv -> shareAppLink());

        btnFullscreen.setOnClickListener(vv -> toggleFullscreen());
    }

    // ====== AdMob setup ======
    private void initAndLoadAds() {
        try {
            MobileAds.initialize(this, initializationStatus -> { });
        } catch (Exception ignored) {}

        loadInterstitial();
        loadRewardedInterstitial();
    }

    private void loadInterstitial() {
        AdRequest req = new AdRequest.Builder().build();
        InterstitialAd.load(this, ADMOB_INTERSTITIAL_ID, req, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(InterstitialAd ad) {
                interstitialAd = ad;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        interstitialAd = null;
                        // preload lagi buat next
                        loadInterstitial();
                        // lanjut play kalau pending
                        if (pendingPlay) {
                            pendingPlay = false;
                            safePlay();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        interstitialAd = null;
                        loadInterstitial();
                        if (pendingPlay) {
                            pendingPlay = false;
                            safePlay();
                        }
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(LoadAdError err) {
                interstitialAd = null;
                // kalau gagal load, tetap boleh play
                if (pendingPlay) {
                    pendingPlay = false;
                    safePlay();
                }
            }
        });
    }

    private void loadRewardedInterstitial() {
        AdRequest req = new AdRequest.Builder().build();
        RewardedInterstitialAd.load(this, ADMOB_REWARDED_INTERSTITIAL_ID, req, new RewardedInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedInterstitialAd ad) {
                rewardedInterstitialAd = ad;
                rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        rewardedInterstitialAd = null;
                        // preload lagi buat next
                        loadRewardedInterstitial();
                        // kalau user nutup iklan, kita jalanin download kalau pending (tapi reward belum tentu dapet)
                        if (pendingDownload) {
                            pendingDownload = false;
                            // download hanya kalau reward udah diterima (lihat showRewardedThenDownload)
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        rewardedInterstitialAd = null;
                        loadRewardedInterstitial();
                        if (pendingDownload) {
                            pendingDownload = false;
                            // fallback: langsung download kalau gagal show
                            downloadVideo();
                        }
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(LoadAdError err) {
                rewardedInterstitialAd = null;
                // no-op, fallback akan langsung download
            }
        });
    }

    // 1) interstitial muncul sebelum video di play
    private void maybeShowInterstitialThenPlay() {
        // kalau video kosong, skip
        if (player == null) return;

        // tandai kita mau play
        pendingPlay = true;

        // kalau iklan udah siap, tampilkan dulu
        if (interstitialAd != null) {
            try {
                interstitialAd.show(this);
                return; // play nanti setelah dismiss
            } catch (Exception ignored) {}
        }

        // kalau belum ada iklan / error, langsung play
        pendingPlay = false;
        safePlay();
    }

    private void safePlay() {
        try {
            if (player != null) player.play();
        } catch (Exception ignored) {}
    }

    // 2) rewarded pas klik unduh
    private void showRewardedThenDownload() {
        if (rewardedInterstitialAd == null) {
            // kalau belum ke-load, langsung download biar user gak stuck
            downloadVideo();
            // coba load lagi
            loadRewardedInterstitial();
            return;
        }

        pendingDownload = true;

        try {
            rewardedInterstitialAd.show(this, rewardItem -> {
                // reward diterima -> baru download
                pendingDownload = false;
                downloadVideo();
            });
        } catch (Exception e) {
            pendingDownload = false;
            downloadVideo();
            loadRewardedInterstitial();
        }
    }

    // Like/Fav per user (guest vs login)
    private void incCount(String prefix) {
        String baseKey = (current.slug != null && !current.slug.trim().isEmpty())
                ? current.slug
                : (current.videoUrl == null ? "no_key" : current.videoUrl);

        String user = Session.userKey(this);
        String key = user + "_" + prefix + baseKey;

        int val = sp.getInt(key, 0);
        sp.edit().putInt(key, val + 1).apply();
        refreshActionCounts();
    }

    private void refreshActionCounts() {
        String baseKey = (current.slug != null && !current.slug.trim().isEmpty())
                ? current.slug
                : (current.videoUrl == null ? "no_key" : current.videoUrl);

        String user = Session.userKey(this);

        int like = sp.getInt(user + "_like_" + baseKey, 0);
        int fav = sp.getInt(user + "_fav_" + baseKey, 0);

        setBtnText(btnLike, "Like " + like);
        setBtnText(btnFav, "Fav " + fav);
    }

    private void setBtnText(View v, String text) {
        try { ((TextView) v).setText(text); } catch (Exception ignored) {}
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
            contentScroll.setVisibility(View.GONE);

            ViewGroup.LayoutParams lp = videoContainer.getLayoutParams();
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            videoContainer.setLayoutParams(lp);

            hideSystemUi();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            contentScroll.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams lp = videoContainer.getLayoutParams();
            lp.height = dpToPx(240);
            videoContainer.setLayoutParams(lp);

            showSystemUi();
        }
    }

    private int dpToPx(int dp) {
        float d = getResources().getDisplayMetrics().density;
        return (int) (dp * d + 0.5f);
    }

    private void hideSystemUi() {
        try {
            View decor = getWindow().getDecorView();
            WindowInsetsController c = decor.getWindowInsetsController();
            if (c != null) {
                c.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } catch (Exception ignored) {}
    }

    private void showSystemUi() {
        try {
            View decor = getWindow().getDecorView();
            WindowInsetsController c = decor.getWindowInsetsController();
            if (c != null) c.show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } catch (Exception ignored) {}
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
        ApiClient.api().getPosts(1).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().items != null) {
                    List<Post> out = new ArrayList<>();
                    for (Post p : resp.body().items) {
                        if (current.slug != null && p.slug != null && current.slug.equals(p.slug)) continue;
                        out.add(p);
                    }
                    relatedAdapter.setItems(out);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) { }
        });
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            toggleFullscreen();
            return;
        }
        super.onBackPressed();
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
