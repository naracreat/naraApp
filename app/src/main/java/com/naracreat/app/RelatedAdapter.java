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

    public interface OnClick {
        void onClick(Post p);
    }

    private List<Post> items;
    private final OnClick onClick;

    public RelatedAdapter(List<Post> items, OnClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    public void setItems(List<Post> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
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

        String timeSrc = (p.createdAt != null && !p.createdAt.isEmpty())
                ? p.createdAt
                : (p.publishedAt != null ? p.publishedAt : "");
        String meta = p.views + " • " + TimeUtil.timeAgo(timeSrc);
        h.meta.setText(meta);

        // Thumbnail dari API
        if (p.thumbnailUrl != null && !p.thumbnailUrl.isEmpty()) {
            Glide.with(h.thumb.getContext())
                    .load(p.thumbnailUrl)
                    .into(h.thumb);
        } else {
            h.thumb.setImageResource(R.mipmap.ic_launcher);
        }

        h.itemView.setOnClickListener(v -> onClick.onClick(p));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
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
}
