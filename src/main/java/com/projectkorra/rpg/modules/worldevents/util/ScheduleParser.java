package com.projectkorra.rpg.modules.worldevents.util;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScheduleParser {
    private static final Pattern TIME = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?(am|pm)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern DURATION = Pattern.compile("(\\d+)([dhms])", Pattern.CASE_INSENSITIVE);

    private ScheduleParser() {}

    public static LocalTime parseTimeOfDay(String raw, LocalTime fallback) {
        if (raw == null || raw.isBlank()) return fallback;
        String s = raw.trim().toLowerCase(Locale.ROOT);
        Matcher m = TIME.matcher(s);
        if (!m.matches()) return fallback;

        int hour = Integer.parseInt(m.group(1));
        int minute = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
        String period = m.group(3);

        if ("pm".equals(period) && hour < 12) hour += 12;
        if ("am".equals(period) && hour == 12) hour = 0;

        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) return fallback;

        return LocalTime.of(hour, minute);
    }

    public static Duration parseDuration(String raw, Duration fallback) {
        if (raw == null || raw.isBlank()) return fallback;

        Matcher m = DURATION.matcher(raw.trim().toLowerCase(Locale.ROOT));
        long seconds = 0L;
        boolean any = false;

        while (m.find()) {
            any = true;
            long value = Long.parseLong(m.group(1));
            String unit = m.group(2);
            if ("d".equals(unit)) seconds += value * 86400L;
            else if ("h".equals(unit)) seconds += value * 3600L;
            else if ("m".equals(unit)) seconds += value * 60L;
            else if ("s".equals(unit)) seconds += value;
        }

        return any ? Duration.ofSeconds(seconds) : fallback;
    }
}
