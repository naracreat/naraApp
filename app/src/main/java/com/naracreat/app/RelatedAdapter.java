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

public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.VH> {

    public interface OnClick { void onClick(Post p); }

    private final List<Post> items;
    private final OnClick onClick;

    public RelatedAdapter(List<Post> items, OnClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_related, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Post p = items.get(pos);
        h.title.setText(p.title != null ? p.title : "—");

        String when = p.createdAt != null ? p.createdAt : p.publishedAt;
        h.meta.setText((p.views != null ? p.views : 0) + " • " + TimeUtil.timeAgo(when != null ? when : ""));

        Glide.with(h.thumb.getContext())
                .load(p.thumbnailUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(h.thumb);

        h.itemView.setOnClickListener(v -> onClick.onClick(p));
    }

    @Override public int getItemCount() { return items != null ? items.size() : 0; }

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
}
