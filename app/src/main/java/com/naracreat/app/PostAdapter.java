package com.naracreat.app;

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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    public interface OnClick { void onClick(Post p); }

    private List<Post> items;
    private final OnClick onClick;

    // ✅ Support style baru: new PostAdapter(onClick)
    public PostAdapter(OnClick onClick) {
        this(new ArrayList<>(), onClick);
    }

    // ✅ Support style lama: new PostAdapter(list, onClick)
    public PostAdapter(List<Post> items, OnClick onClick) {
        this.items = (items != null) ? items : new ArrayList<>();
        this.onClick = onClick;
    }

    public void setItems(List<Post> items) {
        this.items = (items != null) ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Post p = items.get(pos);

        h.title.setText(p.title != null ? p.title : "—");

        String when = (p.createdAt != null && !p.createdAt.isEmpty())
                ? p.createdAt
                : (p.publishedAt != null ? p.publishedAt : "");

        String meta = (p.views != null ? p.views : 0) + " • " + TimeUtil.timeAgo(when);
        h.meta.setText(meta);

        String thumbUrl = p.thumbnailUrl;
        Glide.with(h.thumb.getContext())
                .load(thumbUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(h.thumb);

        h.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onClick(p);
        });
    }

    @Override
    public int getItemCount() { return items != null ? items.size() : 0; }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title, meta;

        VH(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.imgThumb);
            title = itemView.findViewById(R.id.tvTitle);
            meta  = itemView.findViewById(R.id.tvMeta);
        }
    }
}
