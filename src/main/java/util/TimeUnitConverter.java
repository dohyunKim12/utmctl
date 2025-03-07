package util;

import picocli.CommandLine;
import java.util.concurrent.TimeUnit;

public class TimeUnitConverter implements CommandLine.ITypeConverter<TimeUnit> {
    @Override
    public TimeUnit convert(String value) throws Exception {
        switch (value.toLowerCase()) {
            case "s":
            case "sec":
            case "second":
            case "seconds":
                return TimeUnit.SECONDS;
            case "m":
            case "min":
            case "minute":
            case "minutes":
                return TimeUnit.MINUTES;
            case "h":
            case "hour":
            case "hours":
                return TimeUnit.HOURS;
            case "d":
            case "day":
            case "days":
                return TimeUnit.DAYS;
            default:
                throw new IllegalArgumentException("Invalid time unit: " + value);
        }
    }
}
