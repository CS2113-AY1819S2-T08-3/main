package planmysem.data.semester;

import java.time.DayOfWeek;
import java.util.ArrayList;

import planmysem.data.slot.Slot;

/**
 * Represents a Day in the planner.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Day implements ReadOnlyDay {
    private DayOfWeek dayOfWeek;
    private ArrayList<Slot> slots;

    /**
     * Assumption: Every field must be present and not null.
     */
    public Day(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Day(DayOfWeek dayOfWeek, ArrayList<Slot> slots) {
        this.dayOfWeek = dayOfWeek;
        this.slots = slots;
    }

    public void setSlot(Slot slot) {
        slots.add(slot);
    }

    public void clear() {
        slots.clear();
    }

    @Override
    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public String getDay() {
        return dayOfWeek.toString();
    }

    @Override
    public ArrayList<Slot> getSlots() {
        return slots;
    }
}
