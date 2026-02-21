package com.naracreat.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;

public class Session {
    private static final String PREF = "nara_session";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS = "pass";

    public static void login(Context ctx, String email, String password) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit()
                .putString(KEY_EMAIL, email == null ? "" : email.trim())
                .putString(KEY_PASS, password == null ? "" : password)
                .apply();
    }

    public static void logout(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    public static boolean isLoggedIn(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String email = sp.getString(KEY_EMAIL, "");
        String pass = sp.getString(KEY_PASS, "");
        return email != null && !email.trim().isEmpty() && pass != null && !pass.isEmpty();
    }

    public static String getEmail(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String email = sp.getString(KEY_EMAIL, "");
        return email == null ? "" : email;
    }

    // Prefix key untuk nyimpen like/fav/history per-user
    public static String userKey(Context ctx) {
        if (!isLoggedIn(ctx)) return "guest";
        return "u_" + sha1(getEmail(ctx).toLowerCase());
    }

    private static String sha1(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] b = md.digest(s.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(s.hashCode());
        }
    }
}
