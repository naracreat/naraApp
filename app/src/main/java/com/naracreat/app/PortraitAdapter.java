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

public class PortraitAdapter extends RecyclerView.Adapter<PortraitAdapter.VH> {

    public interface OnClick { void onClick(Post p); }

    private final List<Post> items;
    private final OnClick onClick;

    public PortraitAdapter(List<Post> items, OnClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_portrait_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Post p = items.get(pos);
        h.title.setText(p.title != null ? p.title : "—");
        Glide.with(h.img.getContext())
                .load(p.thumbnailUrl)
                .centerCrop()
                .into(h.img);
        h.itemView.setOnClickListener(v -> onClick.onClick(p));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        VH(View v) {
            super(v);
            img = v.findViewById(R.id.imgPoster);
            title = v.findViewById(R.id.tvPosterTitle);
        }
    }
}
