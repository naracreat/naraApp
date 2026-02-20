package com.naracreat.app;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    private static final String SP = "nara_session";

    public static boolean isLoggedIn(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SP, Context.MODE_PRIVATE);
        return sp.getBoolean("logged_in", false);
    }

    public static String username(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SP, Context.MODE_PRIVATE);
        return sp.getString("username", "Guest");
    }

    public static void login(Context c, String username) {
        SharedPreferences sp = c.getSharedPreferences(SP, Context.MODE_PRIVATE);
        sp.edit().putBoolean("logged_in", true).putString("username", username).apply();
    }

    public static void logout(Context c) {
        SharedPreferences sp = c.getSharedPreferences(SP, Context.MODE_PRIVATE);
        sp.edit().putBoolean("logged_in", false).putString("username", "Guest").apply();
    }
}
