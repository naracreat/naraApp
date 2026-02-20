package com.naracreat.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HistoryStore {

    private static final String PREF = "nara_store";
    private static final String KEY_HISTORY = "history_posts";
    private static final String KEY_FAV = "fav_posts";
    private static final int MAX = 50;

    // ===== HISTORY =====

    public static void pushHistory(SharedPreferences sp, Post p) {
        Context c = null;
        return;
    }

    public static void add(Context c, Post p) {
        if (c == null || p == null) return;

        List<Post> list = load(c);

        Iterator<Post> it = list.iterator();
        while (it.hasNext()) {
            Post x = it.next();
            if (x != null && eq(x.slug, p.slug)) {
                it.remove();
                break;
            }
        }

        list.add(0, p);
        if (list.size() > MAX) list = list.subList(0, MAX);
        save(c, list);
    }

    public static List<Post> load(Context c) {
        try {
            SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            String json = sp.getString(KEY_HISTORY, "[]");
            Type t = new TypeToken<List<Post>>(){}.getType();
            List<Post> list = new Gson().fromJson(json, t);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static void save(Context c, List<Post> list) {
        SharedPreferences sp = c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_HISTORY, new Gson().toJson(list)).apply();
    }

    // ===== FAVORITE =====

    public static void setFav(SharedPreferences sp, Post p, boolean on) {
        // sementara dummy biar compile
    }

    private static boolean eq(String a, String b) {
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}
