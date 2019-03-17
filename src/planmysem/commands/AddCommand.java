package planmysem.commands;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.recurrence.Recurrence;
import planmysem.data.semester.Semester;
import planmysem.data.slot.Description;
import planmysem.data.slot.Location;
import planmysem.data.slot.Name;
import planmysem.data.slot.Slot;

/**
 * Adds a person to the planner.
 */
public class AddCommand extends Command {
    public static final String COMMAND_WORD = "add";
    public static final String COMMAND_WORD_SHORT = "a";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Add single or multiple slots to the Planner."
            + "\n\tParameters: "
            + "\n\t\tMandatory: n/NAME d/DATE_OR_DAY_OF_WEEK st/START_TIME et/END_TIME_OR_DURATION"
            + "\n\t\tOptional: [l/LOCATION] [des/DESCRIPTION] [r/normal] [r/recess] [r/reading] [r/exam]"
            + "[r/past] [t/TAG]..."
            + "\n\tExample: " + COMMAND_WORD
            + " n/CS2113T Tutorial d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram t/CS2113T "
            + "t/Tutorial r/normal";

    public static final String MESSAGE_SUCCESS_NO_CHANGE = "No slots were added.";
    public static final String MESSAGE_SUCCESS = "%1$s Slots added.\n\n%2$s";
    public static final String MESSAGE_FAIL_OUT_OF_BOUNDS = "Date specified is out of bounds.";

    private final Slot slot;
    private final Recurrence recurrence;

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(LocalDate date, String name, String location, String description, LocalTime startTime,
                      int duration, Set<String> tags, Set<String> recurrences) throws IllegalValueException {
        slot = new Slot(new Name(name), new Location(location), new Description(description),
                startTime, duration, Utils.parseTags(tags));
        recurrence = new Recurrence(recurrences, date);
    }

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommand(int day, String name, String location, String description, LocalTime startTime,
                      int duration, Set<String> tags, Set<String> recurrences) throws IllegalValueException {
        slot = new Slot(new Name(name), new Location(location), new Description(description),
                startTime, duration, Utils.parseTags(tags));
        recurrence = new Recurrence(recurrences, day);
    }

    @Override
    public CommandResult execute() {
        Set<LocalDate> dates = recurrence.generateDates(planner.getSemester());

        for (LocalDate date : dates) {
            try {
                planner.addSlot(date, slot);
            } catch (Semester.DateNotFoundException dnfe) {
                return new CommandResult(MESSAGE_FAIL_OUT_OF_BOUNDS);
            }
        }

        if (dates.size() == 0) {
            return new CommandResult(MESSAGE_SUCCESS_NO_CHANGE);
        } else {
            return new CommandResult(String.format(MESSAGE_SUCCESS, dates.size(),
                    craftSuccessMessage(dates, slot)));
        }
    }

    /**
     * Craft success message.
     */
    private String craftSuccessMessage(Set<LocalDate> dates, Slot slot) {
        StringBuilder sb = new StringBuilder();
        sb.append("On dates:");

        for (LocalDate date : dates) {
            sb.append("\n\t");
            sb.append(planner.getSemester().getDays().get(date).getType());
            sb.append(", ");
            sb.append(date.toString());
            sb.append(", ");
            sb.append(date.getDayOfWeek().toString());
        }
        sb.append("\n\n");

        sb.append(slot.toString());

        return sb.toString();
    }
}
