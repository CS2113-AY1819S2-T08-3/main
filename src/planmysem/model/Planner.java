package planmysem.model;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;
import planmysem.common.Clock;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.semester.Semester;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;

/**
 * Represents the entire Planner. Contains the model of the Planner.
 */
public class Planner implements ReadOnlyPlanner {
    private final Semester semester;

    /**
     * Creates an empty planner.
     */
    public Planner() {
        semester = Semester.generateSemester(LocalDate.now(Clock.get()));
    }

    /**
     * Creates a Planner using the days in the {@code toBeCopied}
     */
    public Planner(ReadOnlyPlanner toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    /**
     * Constructs a Planner with the given model.
     *
     * @param semester external changes to this will not affect this Planner
     */
    public Planner(Semester semester) {
        this.semester = new Semester(semester);
    }

    /**
     * Resets the existing data of this {@code Planner} with {@code newData}.
     */
    public void resetData(ReadOnlyPlanner newData) {
        requireNonNull(newData);

        setDays(newData.getDays());
    }

    public Day addSlot(LocalDate date, Slot slot) throws Semester.DateNotFoundException {
        return semester.addSlot(date, slot);
    }

    public void removeSlot(LocalDate date, ReadOnlySlot slot) {
        semester.removeSlot(date, slot);
    }

    public void editSlot(LocalDate targetDate, ReadOnlySlot targetSlot, LocalDate date,
                         LocalTime startTime, int duration, String name, String location,
                         String description, Set<String> tags) {
        semester.editSlot(targetDate, targetSlot, date, startTime, duration, name, location, description, tags);
    }

    public void clearSlots() {
        semester.clearSlots();
    }

    public Semester getSemester() {
        return semester;
    }

    /**
     * Replaces the days of the planner with {@code days}.
     */
    public void setDays(HashMap<LocalDate, Day> days) {
        this.semester.setDays(days);
    }

    public List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getSlots(Set<String> tags) {
        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();

        for (Map.Entry<LocalDate, Day> entry : getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (slot.getTags().containsAll(tags)) {
                    selectedSlots.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(), slot)));
                }
            }
        }

        return selectedSlots;
    }

    @Override
    public HashMap<LocalDate, Day> getDays() {
        return semester.getDays();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Planner // instanceof handles nulls
                && this.semester.equals(((Planner) other).semester));
    }

    @Override
    public int hashCode() {
        return semester.hashCode();
    }
}
