package com.naracreat.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.VH> {

    public interface OnClick { void onClick(String genre); }

    private final List<String> items;
    private final OnClick onClick;
    private String selected = "Semua";

    public GenreAdapter(List<String> items, OnClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    public void setSelected(String g) {
        selected = g;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre_chip, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        String g = items.get(pos);
        h.txt.setText(g);
        boolean active = g.equals(selected);
        h.txt.setSelected(active);
        h.itemView.setOnClickListener(v -> onClick.onClick(g));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txt;
        VH(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.tvChip);
        }
    }
}
