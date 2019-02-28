package planmysem.common;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods
 */
public class Utils {
    public static final Pattern DATE_FORMAT =
            Pattern.compile("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)");

    public static final Pattern TWELVE_HOUR_FORMAT =
            Pattern.compile("(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)");
    public static final Pattern TWENTY_FOUR_HOUR_FORMAT =
            Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]\n");

    /**
     * Checks whether any of the given items are null.
     */
    public static boolean isAnyNull(Object... items) {
        for (Object item : items) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if every element in a collection are unique by {@link Object#equals(Object)}.
     */
    public static boolean elementsAreUnique(Collection<?> items) {
        final Set<Object> testSet = new HashSet<>();
        for (Object item : items) {
            final boolean itemAlreadyExists = !testSet.add(item); // see Set documentation
            if (itemAlreadyExists) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if String represents an actual date or day.
     * Returns 0 if string does not represent a DayOfWeek, else returns int corresponding to the day.
     */
    public static int getDay(String unknown) {
        String day = unknown.toLowerCase();

        int result = 0;
        switch (day) {
        case "monday":
        case "mon":
        case "1":
            result = 1;
            break;

        case "tuesday":
        case "tues":
        case "2":
            result = 2;
            break;

        case "wednesday":
        case "wed":
        case "3":
            result = 3;
            break;
        case "thursday":
        case "thurs":
        case "4":
            result = 4;
            break;

        case "friday":
        case "fri":
        case "5":
            result = 5;
            break;

        default:
            result = 0;
        }

        return result;
    }

    /**
     * Parse String LocalDate.
     */
    public static LocalDate parseDate(String date) {
        LocalDate localDate = null;
        Matcher dateMatcher = DATE_FORMAT.matcher(date);

        if (dateMatcher.matches()) {
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d-MM-yyyy"));
        }

        return localDate;
    }

    /**
     * Parse String to 12 hour or 24 hour time format.
     */
    public static LocalTime parseTime(String time) {
        LocalTime localTime = null;
        Matcher twelveHourMatcher = TWELVE_HOUR_FORMAT.matcher(time);
        Matcher twentyFourHourMatcher = TWENTY_FOUR_HOUR_FORMAT.matcher(time);

        // Parse start time into 12 hour format, else, as 24 Hour Format
        if (twelveHourMatcher.matches()) {
            localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm:ss a"));
        } else if (twentyFourHourMatcher.matches()) {
            localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("kk:mm:ss"));
        }

        return localTime;
    }

    /**
     * Get the time difference between two LocalTimes
     */
    public static int getDuration(LocalTime startTime, LocalTime endTime) {
        return (int) MINUTES.between(startTime, endTime);
    }

    /**
     * Get number of matches
     */
    public static int countMatches(Matcher matcher) {
        int counter = 0;
        while (matcher.find()) {
            counter++;
        }
        return counter;
    }
}
