package com.naracreat.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryStore {

    private static final String PREF = "nara_pref";
    private static final String KEY = "watch_history";
    private static final int MAX = 30;

    public static class HistoryItem {
        public String title;
        public String videoUrl;
        public String thumbnailUrl;
        public String watchedAt;
    }

    public static void add(Context ctx, HistoryItem item) {
        List<HistoryItem> list = get(ctx);

        // remove duplicate same videoUrl
        if (item.videoUrl != null) {
            List<HistoryItem> filtered = new ArrayList<>();
            for (HistoryItem it : list) {
                if (it.videoUrl == null || !it.videoUrl.equals(item.videoUrl)) filtered.add(it);
            }
            list = filtered;
        }

        list.add(0, item);
        if (list.size() > MAX) list = list.subList(0, MAX);

        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(KEY, new Gson().toJson(list)).apply();
    }

    public static List<HistoryItem> get(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String json = sp.getString(KEY, "[]");
        Type type = new TypeToken<List<HistoryItem>>(){}.getType();
        try {
            List<HistoryItem> out = new Gson().fromJson(json, type);
            return out != null ? out : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
