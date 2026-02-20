package com.naracreat.app;

public class UrlUtil {

    public static final String BASE = "https://narahentai.pages.dev/";

    public static String abs(String url) {
        if (url == null) return null;
        url = url.trim();
        if (url.isEmpty()) return null;

        if (url.startsWith("http://") || url.startsWith("https://"))
            return url;

        if (url.startsWith("/"))
            url = url.substring(1);

        return BASE + url;
    }
}
