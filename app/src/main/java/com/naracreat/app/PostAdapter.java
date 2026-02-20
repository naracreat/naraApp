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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    public interface OnClick { void onClick(Post p); }

    private List<Post> items;
    private final OnClick onClick;

    public PostAdapter(OnClick onClick) {
        this.onClick = onClick;
    }

    public void setItems(List<Post> items) {
        this.items = items;
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

        h.tvTitle.setText(p.title != null ? p.title : "—");

        String when = (p.createdAt != null && !p.createdAt.isEmpty())
                ? p.createdAt
                : (p.publishedAt != null ? p.publishedAt : "");

        String meta = (p.views) + " views • " + TimeUtil.timeAgo(when);
        h.tvMeta.setText(meta);

        h.tvDuration.setText((p.durationMinutes > 0 ? (p.durationMinutes + " menit") : "—"));

        Glide.with(h.imgThumb.getContext())
                .load(p.thumbnailUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
                .into(h.imgThumb);

        h.itemView.setOnClickListener(v -> onClick.onClick(p));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle, tvMeta, tvDuration;

        VH(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }
    }
}
