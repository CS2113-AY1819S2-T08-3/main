package planmysem.commands;

import java.util.HashSet;
import java.util.Set;

import javafx.util.Pair;
import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.recurrence.Recurrence;
import planmysem.data.semester.Semester;
import planmysem.data.slot.Description;
import planmysem.data.slot.Location;
import planmysem.data.slot.Name;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;
import planmysem.data.tag.Tag;

/**
 * Adds a person to the address book.
 */
public class AddCommandP extends CommandP {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Adds a single or multiple slot to the Planner."
            + "\n\tParameters: NAME st/START_TIME et/END_TIME|DURATION [date/DATE|DAY_OF_WEEK] "
            + "[l/LOCATION] [d/DESCRIPTION] [r/recess|norecess] [r/reading|noreading] [r/normal|nonormal] [t/TAG].."
            + "\n\tExample: " + COMMAND_WORD
            + " CS2113T Tutorial st/0800 et/0900 date/tuesday l/COM2 r/norecess r/noreading "
            + " t/tutorial t/cs2113t t/module";

    public static final String MESSAGE_SUCCESS = "New slot(s) added: %1$s";
    public static final String MESSAGE_SUCCESS_NO_CHANGE = "No slots were added.";
    public static final String MESSAGE_FAIL_OUT_OF_BOUNCE = "Date specified is out of bounce.";

    private final Pair<Slot, Recurrence> toAdd;

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommandP(String date, String name, String location, String description, String startTime,
                       String endTime, Set<String> tags, Set<String> recurrences) throws IllegalValueException {

        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }

        // check variable date if it is a date or day
        int day = Utils.getDay(date);
        if (day != 0) {
            toAdd = new Pair<>(new Slot(new Name(name), new Location(location), new Description(description),
                    Utils.parseTime(startTime), Utils.parseTime(endTime), tagSet), new Recurrence(recurrences, day));
        } else {
            toAdd = new Pair<>(new Slot(new Name(name), new Location(location), new Description(description),
                    Utils.parseTime(startTime), Utils.parseTime(endTime), tagSet),
                        new Recurrence(recurrences, Utils.parseDate(date)));
        }
    }

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddCommandP(String date, String name, String location, String description, String startTime,
                       int duration, Set<String> tags, Set<String> recurrences) throws IllegalValueException {
        final Set<Tag> tagSet = new HashSet<>();

        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }

        // check variable date if it is a date or day
        int day = Utils.getDay(date);
        if (day != 0) {
            toAdd = new Pair<>(new Slot(new Name(name), new Location(location), new Description(description),
                    Utils.parseTime(startTime), duration, tagSet), new Recurrence(recurrences, day));
        } else {
            toAdd = new Pair<>(new Slot(new Name(name), new Location(location), new Description(description),
                    Utils.parseTime(startTime), duration, tagSet), new Recurrence(recurrences, Utils.parseDate(date)));
        }
    }

    public AddCommandP(Pair<Slot, Recurrence> toAdd) {
        this.toAdd = toAdd;
    }

    public Pair<? extends ReadOnlySlot, Recurrence> getSlots() {
        return toAdd;
    }

    @Override
    public CommandResultP execute() {
        try {
            int addCount = planner.addSlots(toAdd);

            if (addCount == 1) {
                return new CommandResultP(MESSAGE_SUCCESS_NO_CHANGE);
            } else {
                return new CommandResultP(String.format(MESSAGE_SUCCESS, toAdd));
            }
        } catch (Semester.DayNotFoundException dnfe) {
            return new CommandResultP(MESSAGE_FAIL_OUT_OF_BOUNCE);
        }

    }

}
