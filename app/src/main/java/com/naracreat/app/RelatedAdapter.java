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

    private final List<Post> items;
    private final OnClick listener;

    public RelatedAdapter(List<Post> items, OnClick listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_related, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Post p = items.get(position);

        h.title.setText(p.title != null ? p.title : "—");
        h.meta.setText(p.views + " views");

        if (p.thumbnailUrl != null && !p.thumbnailUrl.isEmpty()) {
            Glide.with(h.thumb.getContext())
                    .load(p.thumbnailUrl)
                    .centerCrop()
                    .into(h.thumb);
        } else {
            h.thumb.setImageResource(R.mipmap.ic_launcher);
        }

        h.itemView.setOnClickListener(v -> listener.onClick(p));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView thumb;
        TextView title;
        TextView meta;

        VH(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.imgThumb);
            title = itemView.findViewById(R.id.tvTitle);
            meta = itemView.findViewById(R.id.tvMeta);
        }
    }
}
