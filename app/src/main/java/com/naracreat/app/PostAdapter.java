package com.naracreat.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    private final Context c;
    private final List<String> items;

    public PostAdapter(Context c, List<String> items) {
        this.c = c;
        this.items = items;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView title;

        public VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final String url = items.get(position);

        holder.title.setText("Video " + (position + 1));

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(c, PlayerActivity.class);
            i.putExtra("url", url);
            c.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}
