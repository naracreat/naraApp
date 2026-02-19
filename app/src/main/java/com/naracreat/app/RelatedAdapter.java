package com.naracreat.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.VH> {

    public interface OnClick {
        void onClick(Post p);
    }

    private final List<Post> items;
    private final OnClick onClick;

    public RelatedAdapter(List<Post> items, OnClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_related, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Post p = items.get(pos);

        h.title.setText(safe(p.title));

        String viewsText = formatViews(p.views);
        String timeText = timeAgo(firstNonNull(p.publishedAt, p.createdAt));

        h.meta.setText(viewsText + " • " + timeText);

        // Thumbnail: sementara pakai icon (nanti bisa pasang Glide buat URL)
        h.thumb.setImageResource(R.mipmap.ic_launcher);

        h.itemView.setOnClickListener(v -> onClick.onClick(p));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title, meta;

        VH(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.imgThumb);
            title = itemView.findViewById(R.id.tvTitle);
            meta = itemView.findViewById(R.id.tvMeta);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String firstNonNull(String a, String b) {
        return a != null ? a : b;
    }

    private String formatViews(Integer views) {
        if (views == null) return "—";
        if (views >= 1_000_000) return String.format("%.1fM", views / 1_000_000f);
        if (views >= 1_000) return String.format("%.1fK", views / 1_000f);
        return String.valueOf(views);
    }

    private String timeAgo(String iso) {
        if (iso == null) return "—";
        try {
            Instant t = Instant.parse(iso);
            Duration d = Duration.between(t, Instant.now());

            long minutes = d.toMinutes();
            long hours = d.toHours();
            long days = d.toDays();

            if (minutes < 1) return "baru saja";
            if (minutes < 60) return minutes + " menit lalu";
            if (hours < 24) return hours + " jam lalu";
            return days + " hari lalu";
        } catch (Exception e) {
            return "—";
        }
    }
}
