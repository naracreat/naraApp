package com.naracreat.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    public interface OnClick { void onClick(Post p); }

    private List<Post> items;
    private final OnClick onClick;

    public PostAdapter(List<Post> items, OnClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    public void setItems(List<Post> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
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

        // FIX: thumbnail url sering relatif, jadi harus dibuat absolute
        String thumbUrl = UrlUtil.abs(p.thumbnailUrl);

        Glide.with(h.thumb.getContext())
                .load(thumbUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                // biar gak "ketuker" / nyangkut cache pas debugging
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(h.thumb);

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
