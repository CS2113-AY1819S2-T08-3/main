package planmysem.data.recurrence;

import java.time.LocalDate;
import java.util.Set;

/**
 * Represents a Recurrence Value of a slot in the Planner.
 */
public class Recurrence {
    public final boolean recess; // Represents recess week
    public final boolean reading; // Represents reading week
    public final boolean normal; // Represents normal academic weeks
    public final int day;
    public final LocalDate date;

    /**
     * Generate Recurrence values from a set and recurse over a given day.
     */
    public Recurrence(Set<String> recurrences, int day) {
        if (recurrences.contains("recess")) {
            recess = true;
        } else {
            recess = false;
        }
        if (recurrences.contains("reading")) {
            reading = true;
        } else {
            reading = false;
        }
        if (recurrences.contains("normal")) {
            normal = true;
        } else {
            normal = false;
        }
        this.day = day;
        date = null;
    }

    /**
     * Generate Recurrence values from a set and recurse over a given date.
     */
    public Recurrence(Set<String> recurrences, LocalDate date) {
        if (recurrences.contains("recess")) {
            recess = true;
        } else {
            recess = false;
        }
        if (recurrences.contains("reading")) {
            reading = true;
        } else {
            reading = false;
        }
        if (recurrences.contains("normal")) {
            normal = true;
        } else {
            normal = false;
        }
        this.day = 0;
        this.date = date;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Recurrence // instanceof handles nulls
                && this.recess == ((Recurrence) other).recess
                && this.reading == ((Recurrence) other).reading
                && this.normal == ((Recurrence) other).normal); // state check
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        if (recess) {
            hashCode += 1; // 0001
        }
        if (reading) {
            hashCode += 2; // 0010
        }
        if (normal) {
            hashCode += 4; // 0100
        }
        return hashCode;
    }
}
