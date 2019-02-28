package planmysem.data.semester;

import java.time.LocalDate;
import java.util.HashMap;

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
}
