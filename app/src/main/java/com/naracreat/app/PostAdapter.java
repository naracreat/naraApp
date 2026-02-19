package com.naracreat.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    private final List<Post> data;

    public PostAdapter(List<Post> data) {
        this.data = data;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Post p = data.get(position);

        holder.title.setText(p.title);
        holder.sub.setText(p.sub);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                Intent i = new Intent(c, PlayerActivity.class);
                i.putExtra("url", p.videoUrl);
                c.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView title;
        TextView sub;

        public VH(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.postTitle);
            sub = itemView.findViewById(R.id.postSub);
        }
    }
}
