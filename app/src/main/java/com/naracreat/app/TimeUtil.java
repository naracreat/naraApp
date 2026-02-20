package com.naracreat.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {

    // Parse ISO8601: 2026-02-16T05:59:18.332Z
    private static Date parseIso(String iso) throws ParseException {
        if (iso == null || iso.trim().isEmpty()) return null;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.parse(iso);
    }

    public static String timeAgo(String iso) {
        try {
            Date t = parseIso(iso);
            if (t == null) return "—";

            long now = System.currentTimeMillis();
            long diffMs = now - t.getTime();
            long sec = diffMs / 1000L;
            if (sec < 60) return "baru saja";
            long min = sec / 60;
            if (min < 60) return min + " menit lalu";
            long hr = min / 60;
            if (hr < 24) return hr + " jam lalu";
            long day = hr / 24;
            return day + " hari lalu";
        } catch (Exception e) {
            return "—";
        }
    }
}
