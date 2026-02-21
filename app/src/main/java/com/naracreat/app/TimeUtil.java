package com.naracreat.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {

    public static String timeAgo(String iso) {
        if (iso == null || iso.trim().isEmpty()) return "—";
        try {
            // contoh: 2026-02-16T05:59:18.332Z
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date t = df.parse(iso);

            if (t == null) return "—";
            long diffMs = System.currentTimeMillis() - t.getTime();
            long sec = diffMs / 1000L;
            if (sec < 60) return "baru saja";
            long min = sec / 60;
            if (min < 60) return min + " menit lalu";
            long hr = min / 60;
            if (hr < 24) return hr + " jam lalu";
            long day = hr / 24;
            return day + " hari lalu";
        } catch (ParseException e) {
            return "—";
        } catch (Exception e) {
            return "—";
        }
    }

    public static String mmssFromMinutes(int minutes) {
        if (minutes <= 0) return "0:00";
        int totalSec = minutes * 60;
        int mm = totalSec / 60;
        int ss = totalSec % 60;
        return mm + ":" + (ss < 10 ? "0" + ss : String.valueOf(ss));
    }
}
