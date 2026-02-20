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

public class ProfileGridAdapter extends RecyclerView.Adapter<ProfileGridAdapter.VH> {

    public interface OnClick { void onClick(Post p); }

    private List<Post> items = new ArrayList<>();
    private final OnClick onClick;

    public ProfileGridAdapter(OnClick onClick) {
        this.onClick = onClick;
    }

    public void setItems(List<Post> list) {
        items = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_grid, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Post p = items.get(position);
        h.title.setText(p.title != null ? p.title : "—");
        Glide.with(h.thumb.getContext())
                .load(p.thumbnailUrl)
                .placeholder(R.drawable.ph_16x9)
                .error(R.drawable.ph_16x9)
                .centerCrop()
                .into(h.thumb);

        h.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onClick(p);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title;
        VH(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.imgThumb);
            title = itemView.findViewById(R.id.tvTitle);
        }
    }
}
