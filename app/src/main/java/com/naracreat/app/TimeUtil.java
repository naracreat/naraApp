package com.naracreat.app;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    public static String timeAgo(String input) {
        try {
            if (input == null || input.trim().isEmpty()) return "—";

            Instant time;

            // CASE 1: Unix timestamp (angka semua)
            if (input.matches("\\d+")) {
                long ts = Long.parseLong(input);
                // kalau detik (10 digit)
                if (input.length() == 10) {
                    time = Instant.ofEpochSecond(ts);
                } else {
                    // kalau millisecond (13 digit)
                    time = Instant.ofEpochMilli(ts);
                }
            }
            // CASE 2: ISO format
            else if (input.contains("T")) {
                time = Instant.parse(input);
            }
            // CASE 3: yyyy-MM-dd HH:mm:ss
            else {
                DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.of("UTC"));
                time = ZonedDateTime.parse(input, f).toInstant();
            }

            long sec = Instant.now().getEpochSecond() - time.getEpochSecond();

            if (sec < 60) return "baru saja";

            long min = sec / 60;
            if (min < 60) return min + " menit lalu";

            long hr = min / 60;
            if (hr < 24) return hr + " jam lalu";

            long day = hr / 24;
            if (day < 30) return day + " hari lalu";

            long month = day / 30;
            if (month < 12) return month + " bulan lalu";

            long year = month / 12;
            return year + " tahun lalu";

        } catch (Exception e) {
            return "—";
        }
    }
}
