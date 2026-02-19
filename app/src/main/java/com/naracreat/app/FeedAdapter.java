package com.naracreat.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.VH> {

    public static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title;
        TextView meta;
        TextView badgeDuration;

        public VH(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.thumb);
            title = itemView.findViewById(R.id.title);
            meta = itemView.findViewById(R.id.meta);
            badgeDuration = itemView.findViewById(R.id.badgeDuration);
        }
    }

    private final Context context;
    private final List<Post> items = new ArrayList<>();

    public FeedAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<Post> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_big, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Post p = items.get(position);

        h.title.setText(p.title != null ? p.title : "-");

        String duration = formatDuration(p.durationMinutes);
        h.badgeDuration.setText(duration);

        String meta = (p.views) + " suka • " + (p.durationMinutes > 0 ? (p.durationMinutes + " min") : "-");
        h.meta.setText(meta);

        Glide.with(context)
                .load(p.thumbnailUrl)
                .centerCrop()
                .into(h.thumb);

        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, PlayerActivity.class);
            i.putExtra("title", p.title);
            i.putExtra("videoUrl", p.videoUrl);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatDuration(int minutes) {
        if (minutes <= 0) return "00:00";
        int h = minutes / 60;
        int m = minutes % 60;
        if (h > 0) {
            return String.format("%02d:%02d:00", h, m);
        } else {
            return String.format("%02d:00", m);
        }
    }
}
