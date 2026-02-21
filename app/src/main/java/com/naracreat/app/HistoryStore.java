package com.naracreat.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryStore {

    private static final String PREF = "nara_history";
    private static final int MAX = 50;

    public static void addWatched(Context ctx, Post p) {
        if (p == null) return;

        String key = Session.userKey(ctx) + "_history";
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        List<Post> cur = getHistory(ctx);
        // remove duplicate by slug/videoUrl
        String id = idOf(p);
        List<Post> out = new ArrayList<>();
        out.add(p);
        for (Post x : cur) {
            if (idOf(x).equals(id)) continue;
            out.add(x);
            if (out.size() >= MAX) break;
        }
        sp.edit().putString(key, toJson(out)).apply();
    }

    public static List<Post> getHistory(Context ctx) {
        String key = Session.userKey(ctx) + "_history";
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String raw = sp.getString(key, "[]");
        return fromJson(raw);
    }

    public static void clear(Context ctx) {
        String key = Session.userKey(ctx) + "_history";
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }

    private static String idOf(Post p) {
        if (p == null) return "x";
        if (p.slug != null && !p.slug.trim().isEmpty()) return "s:" + p.slug;
        if (p.videoUrl != null && !p.videoUrl.trim().isEmpty()) return "v:" + p.videoUrl;
        return "t:" + (p.title == null ? "" : p.title);
    }

    private static String toJson(List<Post> list) {
        try {
            JSONArray arr = new JSONArray();
            for (Post p : list) {
                JSONObject o = new JSONObject();
                o.put("title", p.title == null ? "" : p.title);
                o.put("videoUrl", p.videoUrl == null ? "" : p.videoUrl);
                o.put("thumbnailUrl", p.thumbnailUrl == null ? "" : p.thumbnailUrl);
                o.put("slug", p.slug == null ? "" : p.slug);
                o.put("createdAt", p.createdAt == null ? "" : p.createdAt);
                o.put("publishedAt", p.publishedAt == null ? "" : p.publishedAt);
                o.put("views", p.views == null ? 0 : p.views);
                o.put("durationMinutes", p.durationMinutes == null ? 0 : p.durationMinutes);
                arr.put(o);
            }
            return arr.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    private static List<Post> fromJson(String raw) {
        List<Post> out = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(raw == null ? "[]" : raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Post p = new Post();
                p.title = o.optString("title", "");
                p.videoUrl = o.optString("videoUrl", "");
                p.thumbnailUrl = o.optString("thumbnailUrl", "");
                p.slug = o.optString("slug", "");
                p.createdAt = o.optString("createdAt", "");
                p.publishedAt = o.optString("publishedAt", "");
                p.views = o.optInt("views", 0);
                p.durationMinutes = o.optInt("durationMinutes", 0);
                out.add(p);
            }
        } catch (Exception ignored) {}
        return out;
    }
}
