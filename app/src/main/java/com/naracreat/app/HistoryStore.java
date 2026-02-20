package com.naracreat.app;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HistoryStore {

    private static final Gson gson = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<Post>>(){}.getType();

    public static void pushHistory(SharedPreferences sp, Post p) {
        if (p == null || p.slug == null) return;

        List<Post> cur = loadHistory(sp);

        // unique by slug, newest first
        Map<String, Post> map = new LinkedHashMap<>();
        map.put(p.slug, p);
        for (Post x : cur) {
            if (x != null && x.slug != null && !x.slug.equals(p.slug)) map.put(x.slug, x);
        }

        List<Post> out = new ArrayList<>(map.values());
        if (out.size() > 50) out = out.subList(0, 50);

        sp.edit().putString("history_json", gson.toJson(out)).apply();
    }

    public static List<Post> loadHistory(SharedPreferences sp) {
        String s = sp.getString("history_json", "[]");
        try {
            List<Post> list = gson.fromJson(s, LIST_TYPE);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void setFav(SharedPreferences sp, Post p, boolean on) {
        if (p == null || p.slug == null) return;
        List<Post> cur = loadFavs(sp);

        Map<String, Post> map = new LinkedHashMap<>();
        if (on) map.put(p.slug, p);
        for (Post x : cur) {
            if (x != null && x.slug != null && !x.slug.equals(p.slug)) map.put(x.slug, x);
        }

        List<Post> out = new ArrayList<>(map.values());
        if (out.size() > 200) out = out.subList(0, 200);

        sp.edit().putString("fav_json", gson.toJson(out)).apply();
    }

    public static List<Post> loadFavs(SharedPreferences sp) {
        String s = sp.getString("fav_json", "[]");
        try {
            List<Post> list = gson.fromJson(s, LIST_TYPE);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
