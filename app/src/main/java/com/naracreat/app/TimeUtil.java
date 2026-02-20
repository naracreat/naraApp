package com.naracreat.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {

    // coba banyak format umum
    private static final String[] FORMATS = new String[]{
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
    };

    public static String timeAgo(String raw) {
        if (raw == null) return "—";
        raw = raw.trim();
        if (raw.isEmpty() || raw.equals("null")) return "—";

        Long epochMs = tryParseEpoch(raw);
        Date dt = null;

        if (epochMs != null) {
            dt = new Date(epochMs);
        } else {
            dt = tryParseDate(raw);
        }

        if (dt == null) return "—";

        long now = System.currentTimeMillis();
        long diff = (now - dt.getTime()) / 1000; // seconds

        if (diff < 0) diff = 0;

        if (diff < 60) return "baru saja";
        long min = diff / 60;
        if (min < 60) return min + " menit lalu";
        long hr = min / 60;
        if (hr < 24) return hr + " jam lalu";
        long day = hr / 24;
        return day + " hari lalu";
    }

    private static Date tryParseDate(String raw) {
        for (String f : FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(f, Locale.US);
                // kalau format Z / ISO, anggap UTC
                if (f.contains("'Z'") || f.contains("XXX")) {
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                }
                return sdf.parse(raw);
            } catch (ParseException ignored) { }
        }
        return null;
    }

    private static Long tryParseEpoch(String raw) {
        // support: "1712345678" (sec) atau "1712345678000" (ms)
        try {
            if (!raw.matches("^\\d{10,13}$")) return null;
            long v = Long.parseLong(raw);
            if (raw.length() == 10) return v * 1000L;
            return v;
        } catch (Exception e) {
            return null;
        }
    }
}
