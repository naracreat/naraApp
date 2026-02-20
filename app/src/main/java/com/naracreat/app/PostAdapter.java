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

    private List<Post> items = new ArrayList<>();
    private final OnClick onClick;

    public PostAdapter(OnClick onClick) {
        this.onClick = onClick;
    }

    public void setItems(List<Post> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_16x9, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Post p = items.get(pos);

        h.title.setText(p.title != null ? p.title : "—");

        String whenIso = p.timeSrc();
        String timeAgo = TimeUtil.timeAgo(whenIso != null ? whenIso : "");
        int views = (p.views != null) ? p.views : 0;
        h.meta.setText(views + " views • " + timeAgo);

        if (p.thumbnailUrl != null && !p.thumbnailUrl.isEmpty()) {
            Glide.with(h.thumb.getContext())
                    .load(p.thumbnailUrl)
                    .centerCrop()
                    .placeholder(R.drawable.thumb_placeholder)
                    .error(R.drawable.thumb_placeholder)
                    .into(h.thumb);
        } else {
            h.thumb.setImageResource(R.drawable.thumb_placeholder);
        }

        h.itemView.setOnClickListener(v -> onClick.onClick(p));
    }

    @Override
    public int getItemCount() { return items.size(); }

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
