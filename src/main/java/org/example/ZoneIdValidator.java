package org.example;

import java.time.ZoneId;
import java.util.Objects;
import java.util.regex.Pattern;

public class ZoneIdValidator {
    private static Pattern pattern = Pattern.compile("^UTC(?:\\s*([+-]?\\d{1,2})(?::(\\d{2}))?)?$");
    private static ZoneId zoneId;

    public static boolean validate(String timezone) {
        if (timezone == null || timezone.isEmpty() || !pattern.matcher(timezone).matches()) {
            return false;
        }

        try {
            zoneId = ZoneId.of(timezone.replace(" ", "+"));
        } catch (Exception ex) {
            zoneId = null;
            return false;
        }

        return true;
    }

    public static ZoneId getZoneId() {
        return Objects.requireNonNull(zoneId);
    }
}
