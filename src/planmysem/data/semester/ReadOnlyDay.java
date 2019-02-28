package planmysem.data.semester;

import java.time.DayOfWeek;
import java.util.ArrayList;

import planmysem.data.slot.Slot;

/**
 * Represents a Day in the planner.
 * Guarantees: details are present and not null, field values are validated.
 */
public interface ReadOnlyDay {
    public DayOfWeek getDayOfWeek();

    public String getDay();

    public ArrayList<Slot> getSlots();
}
