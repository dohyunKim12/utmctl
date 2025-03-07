package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    public static String generateCurrentDateString() {
        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);
        TimeZone defaultTimeZone = TimeZone.getDefault();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(defaultTimeZone);
        return dateFormat.format(now);
    }
}
