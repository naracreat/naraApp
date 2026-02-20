package com.naracreat.app;

public class UrlUtil {

    // Samain dengan BASE_URL retrofit kamu
    public static final String BASE = "https://narahentai.pages.dev/";

    public static String abs(String url) {
        if (url == null) return null;
        url = url.trim();
        if (url.isEmpty()) return null;

        // already absolute
        if (url.startsWith("http://") || url.startsWith("https://")) return url;

        // protocol-relative
        if (url.startsWith("//")) return "https:" + url;

        // ensure base ends with /
        String base = BASE.endsWith("/") ? BASE : (BASE + "/");

        // relative path
        if (url.startsWith("/")) return base + url.substring(1);
        return base + url;
    }
}
