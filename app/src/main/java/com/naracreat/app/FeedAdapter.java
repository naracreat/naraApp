package com.naracreat.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnPostClick { void onClick(Post p); }

    private static final int TYPE_PORTRAIT_ROW = 1;
    private static final int TYPE_VIDEO = 2;

    private final OnPostClick onClick;

    private List<Post> portrait = new ArrayList<>();
    private List<Post> feed = new ArrayList<>();

    public FeedAdapter(OnPostClick onClick) {
        this.onClick = onClick;
    }

    public void setData(List<Post> all) {
        portrait.clear();
        feed.clear();

        // Simple rule: 8 pertama jadi “poster potret” row
        int take = Math.min(8, all.size());
        for (int i = 0; i < take; i++) portrait.add(all.get(i));
        for (int i = take; i < all.size(); i++) feed.add(all.get(i));

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_PORTRAIT_ROW;
        return TYPE_VIDEO;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_PORTRAIT_ROW) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_portrait_row, parent, false);
            return new PortraitRowVH(v);
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_card, parent, false);
        return new VideoVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PORTRAIT_ROW) {
            PortraitRowVH h = (PortraitRowVH) holder;
            h.rv.setLayoutManager(
                    new LinearLayoutManager(h.rv.getContext(), LinearLayoutManager.HORIZONTAL, false)
            );
            h.rv.setAdapter(new PortraitAdapter(portrait, onClick::onClick));
            return;
        }

        int idx = position - 1;
        Post p = feed.get(idx);
        VideoVH h = (VideoVH) holder;

        h.title.setText(p.title != null ? p.title : "—");
        String created = (p.createdAt != null && !p.createdAt.isEmpty()) ? p.createdAt : (p.publishedAt != null ? p.publishedAt : "");
        int views = p.views != null ? p.views : 0;
        h.meta.setText(views + " • " + TimeUtil.timeAgo(created));

        Glide.with(h.thumb.getContext())
                .load(p.thumbnailUrl)
                .centerCrop()
                .into(h.thumb);

        h.itemView.setOnClickListener(v -> onClick.onClick(p));
    }

    @Override
    public int getItemCount() {
        // +1 untuk portrait row
        return 1 + feed.size();
    }

    static class VideoVH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title, meta;
        VideoVH(View v) {
            super(v);
            thumb = v.findViewById(R.id.imgThumb);
            title = v.findViewById(R.id.tvTitle);
            meta = v.findViewById(R.id.tvMeta);
        }
    }

    static class PortraitRowVH extends RecyclerView.ViewHolder {
        RecyclerView rv;
        PortraitRowVH(View v) {
            super(v);
            rv = v.findViewById(R.id.rvPortrait);
        }
    }
}
