package planmysem.common;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility methods
 */
public class Utils {
    public static final Pattern INTEGER_FORMAT =
            Pattern.compile("\\d+");

    private static final int MAXIMUM_DISTANCE = 20;

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
    public static int parseDay(String unknown) {
        if (unknown.trim().isEmpty()) {
            return -1;
        }
        String day = unknown.trim().toLowerCase();
        int result;
        switch (day) {
        case "monday":
        case "mon":
        case "1":
            result = 1;
            break;

        case "tuesday":
        case "tue":
        case "2":
            result = 2;
            break;

        case "wednesday":
        case "wed":
        case "3":
            result = 3;
            break;

        case "thursday":
        case "thu":
        case "4":
            result = 4;
            break;

        case "friday":
        case "fri":
        case "5":
            result = 5;
            break;

        case "saturday":
        case "sat":
        case "6":
            result = 6;
            break;

        case "sunday":
        case "sun":
        case "7":
            result = 7;
            break;

        default:
            result = -1;
            break;
        }

        return result;
    }

    /**
     * Parse String to LocalDate.
     */
    public static LocalDate parseDate(String date) {
        if (date == null) {
            return null;
        }

        LocalDate result;

        // with year
        try {
            result = LocalDate.parse(date, DateTimeFormatter.ofPattern("d-MM-yyyy"));
        } catch (DateTimeParseException dtpe) {
            result = null;
        }

        // without year
        if (result == null) {
            try {
                result = LocalDate.parse(date + "-" + Year.now().getValue(), DateTimeFormatter.ofPattern("d-MM-yyyy"));
            } catch (DateTimeParseException dtpe) {
                result = null;
            }
        }

        return result;
    }

    /**
     * Parse String to 12 hour or 24 hour time format.
     */
    public static LocalTime parseTime(String time) {
        if (time == null) {
            return null;
        }

        LocalTime result;

        // Twelve hour format
        try {
            result = LocalTime.parse(time.toUpperCase(), DateTimeFormatter.ofPattern("h[h]:mma"));
        } catch (DateTimeParseException dtpe) {
            result = null;
        }

        if (result == null) {
            try {
                result = LocalTime.parse(time.toUpperCase(), DateTimeFormatter.ofPattern("h[h]:mm a"));
            } catch (DateTimeParseException dtpe) {
                result = null;
            }
        }

        if (result == null) {
            // Twenty-four hour format
            try {
                result = LocalTime.parse(time, DateTimeFormatter.ofPattern("H[H]:mm"));
            } catch (DateTimeParseException dtpe) {
                result = null;
            }
        }

        return result;
    }

    /**
     * Parse string into integer.
     */
    public static int parseInteger(String value) {
        if (value != null && INTEGER_FORMAT.matcher(value).matches()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Get the time difference between two LocalTimes in minutes.
     */
    public static int getDuration(LocalTime startTime, LocalTime endTime) {
        return (int) MINUTES.between(startTime, endTime);
    }

    /**
     * Get the end time of a time after a duration.
     */
    public static LocalTime getEndTime(LocalTime time, int duration) {
        return time.plusMinutes(duration);
    }

    /**
     * Computes Levenshtein Distance from strings
     */
    public static int getLevenshteinDistance (String lhsIn, String rhsIn) {
        String lhs = lhsIn.toLowerCase();
        String rhs = rhsIn.toLowerCase();

        // the array of distances
        int[] cost = new int[lhs.length() + 1];
        int[] newCost = new int[lhs.length() + 1];

        // initial cost in String lhs
        for (int i = 0; i < lhs.length(); i++) {
            cost[i] = i;
        }

        // cost for transforming each letter in String rhs
        for (int j = 1; j < rhs.length() + 1 && j < MAXIMUM_DISTANCE; j++) {
            // initial cost in String rhs
            newCost[0] = j;

            // transformation cost for each letter in String lhs
            for (int i = 1; i < lhs.length() + 1; i++) {
                // match current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // cost for each type of transformation
                int costReplace = cost[i - 1] + match;
                int costInsert = cost[i] + 1;
                int costDelete = newCost[i - 1] + 1;

                // keep minimum cost
                newCost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
            }

            // switch cost array with newCost array
            int[] temp = cost;
            cost = newCost;
            newCost = temp;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[lhs.length()];
    }

    /**
     * Get the nearest date to a type of day from today.
     */
    public static LocalDate getNearestDayOfWeek(LocalDate date, int day) {
        return date.with(TemporalAdjusters.next(DayOfWeek.of(day)));
    }
}
