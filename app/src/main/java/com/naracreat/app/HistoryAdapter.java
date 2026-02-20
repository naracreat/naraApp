package com.naracreat.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.VH> {

    public interface OnClick { void onClick(HistoryStore.HistoryItem it); }

    private final List<HistoryStore.HistoryItem> items;
    private final OnClick onClick;

    public HistoryAdapter(List<HistoryStore.HistoryItem> items, OnClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        HistoryStore.HistoryItem it = items.get(pos);
        h.title.setText(it.title != null ? it.title : "—");
        h.time.setText(TimeUtil.timeAgo(it.watchedAt != null ? it.watchedAt : ""));
        Glide.with(h.img.getContext()).load(it.thumbnailUrl).centerCrop().into(h.img);
        h.itemView.setOnClickListener(v -> onClick.onClick(it));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, time;
        VH(View v) {
            super(v);
            img = v.findViewById(R.id.img);
            title = v.findViewById(R.id.tvTitle);
            time = v.findViewById(R.id.tvTime);
        }
    }
}
