package com.naracreat.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    private final List<Post> data;

    public PostAdapter(List<Post> data) {
        this.data = data;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int pos) {
        Post p = data.get(pos);
        h.title.setText(p.title);
        h.sub.setText(p.sub);

        h.itemView.setOnClickListener(v -> {
            Context c = v.getContext();
            Toast.makeText(c, "Video: " + p.videoUrl, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, sub;
        VH(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.postTitle);
            sub = itemView.findViewById(R.id.postSub);
        }
    }
}
