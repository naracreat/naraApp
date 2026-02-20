package com.naracreat.app;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtil {
    public static String timeAgo(String iso) {
        try {
            Instant t = Instant.parse(iso);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
            long sec = now.toInstant().getEpochSecond() - t.getEpochSecond();

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
