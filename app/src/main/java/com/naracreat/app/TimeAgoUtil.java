package com.naracreat.app;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;

public class TimeAgoUtil {
    public static String timeAgo(String iso) {
        if (iso == null || iso.trim().isEmpty()) return "-";
        try {
            Instant t;
            try {
                t = OffsetDateTime.parse(iso).toInstant();
            } catch (DateTimeParseException e) {
                t = Instant.parse(iso);
            }

            long diffMs = Instant.now().toEpochMilli() - t.toEpochMilli();
            if (diffMs < 0) diffMs = 0;

            long mins = TimeUnit.MILLISECONDS.toMinutes(diffMs);
            if (mins < 1) return "Baru saja";
            if (mins < 60) return mins + " menit lalu";

            long hrs = TimeUnit.MILLISECONDS.toHours(diffMs);
            if (hrs < 24) return hrs + " jam lalu";

            long days = TimeUnit.MILLISECONDS.toDays(diffMs);
            if (days < 30) return days + " hari lalu";

            long months = days / 30;
            if (months < 12) return months + " bulan lalu";

            long years = months / 12;
            return years + " tahun lalu";
        } catch (Exception e) {
            return "-";
        }
    }
}
