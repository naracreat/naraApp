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
    private String active = "Semua";

    public GenreAdapter(List<String> items, OnClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    public void setActive(String g) {
        active = (g == null || g.isEmpty()) ? "Semua" : g;
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
        h.tv.setText(g);

        boolean isActive = g.equalsIgnoreCase(active);
        h.tv.setBackgroundResource(isActive ? R.drawable.bg_chip_active : R.drawable.bg_chip);

        h.itemView.setOnClickListener(v -> onClick.onClick(g));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;
        VH(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvChip);
        }
    }
}
