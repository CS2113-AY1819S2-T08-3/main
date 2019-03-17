package planmysem.data.semester;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

/**
 * Represents a Semester in the planner.
 * Guarantees: details are present and not null, field values are validated.
 */
public interface ReadOnlySemester {
    String getName();
    String getAcademicYear();
    HashMap<LocalDate, Day> getDays();
    LocalDate getStartDate();
    LocalDate getEndDate();
    int getNoOfWeeks();

    // These variables aid in making searches more effective
    Set<LocalDate> getRecessDays();
    Set<LocalDate> getReadingDays();
    Set<LocalDate> getNormalDays();
    Set<LocalDate> getExamDays();
}
