package planmysem.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.semester.Day;
import planmysem.data.semester.Semester;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;
import planmysem.data.tag.TagP;

/**
 * Adds a person to the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";
    public static final String COMMAND_WORD_SHORT = "e";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edit single or multiple slots in the Planner."
            + "\n\tParameters: "
            + "\n\t\tMandatory: t/TAG... or INDEX"
            + "\n\t\tOptional Parameters: [nst/NEW_START_TIME] [net/NEW_END_TIME|DURATION] "
            + "[nl/NEW_LOCATION] [nd/NEW_DESCRIPTION]"
            + "\n\tExample 1: " + COMMAND_WORD
            + " t/CS2113T t/Tutorial nl/COM2 04-01"
            + "\n\tExample 2: " + COMMAND_WORD
            + " 2 nl/COM2 04-01";


    public static final String MESSAGE_SUCCESS_NO_CHANGE = "No Slots were edited.\n\n%1$s";
    public static final String MESSAGE_SUCCESS = "%1$s Slots edited.\n\n%2$s\n%3$s";
    public static final String MESSAGE_FAIL_ILLEGAL_VALUE = MESSAGE_SUCCESS
        + " Illegal characters were detected.";

    private final LocalDate date;
    private final LocalTime startTime;
    private final int duration;
    private final String name;
    private final String location;
    private final String description;
    private final Set<TagP> tags = new HashSet<>();
    private final Set<TagP> newTags = new HashSet<>();

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public EditCommand(String name, LocalTime startTime, int duration, String location, String description,
                       Set<String> tags, Set<String> newTags) throws IllegalValueException {
        this.date = null;
        this.startTime = startTime;
        this.duration = duration;
        this.name = name;
        this.location = location;
        this.description = description;
        this.tags.addAll(Utils.parseTags(tags));
        if (newTags != null) {
            this.newTags.addAll(Utils.parseTags(newTags));
        }
    }

    /**
     * Convenience constructor using raw values.
     */
    public EditCommand(int index, String name, LocalDate date, LocalTime startTime, int duration,
                       String location, String description, Set<String> newTags)
            throws IllegalValueException {
        super(index);
        this.date = date;
        this.startTime = startTime;
        this.duration = duration;
        this.name = name;
        this.location = location;
        this.description = description;
        if (newTags != null) {
            this.newTags.addAll(Utils.parseTags(newTags));
        }
    }

    @Override
    public CommandResult execute() {
        Map<LocalDateTime, ReadOnlySlot> selectedSlots = new TreeMap<>();

        if (getTargetIndex() == -1) {
            for (Map.Entry<LocalDate, Day> day : planner.getSemester().getDays().entrySet()) {
                for (Slot slot : day.getValue().getSlots()) {
                    if (slot.getTags().containsAll(tags)) {
                        selectedSlots.put(LocalDateTime.of(day.getKey(), slot.getStartTime()), slot);
                    }
                }
            }
            if (selectedSlots.size() == 0) {
                return new CommandResult(String.format(MESSAGE_SUCCESS_NO_CHANGE, craftSelectedMessage()));
            }
        } else {
            try {
                final Pair<LocalDate, ? extends ReadOnlySlot> target = getTargetSlot();
                selectedSlots.put(LocalDateTime.of(target.getKey(),
                        target.getValue().getStartTime()), target.getValue());
                if (!planner.containsSlot(target.getKey(), target.getValue())) {
                    return new CommandResult(Messages.MESSAGE_SLOT_NOT_IN_PLANNER);
                }
            } catch (IndexOutOfBoundsException ie) {
                return new CommandResult(Messages.MESSAGE_INVALID_SLOT_DISPLAYED_INDEX);
            }
        }

        for (Map.Entry<LocalDateTime, ReadOnlySlot> entry : selectedSlots.entrySet()) {
            try {
                planner.editSlot(entry.getKey().toLocalDate(), entry.getValue(), date,
                        startTime, duration, name, location, description, newTags);
            } catch (IllegalValueException ive) {
                return new CommandResult(MESSAGE_FAIL_ILLEGAL_VALUE);
            } catch (Semester.DateNotFoundException dnfe) {
                return new CommandResult(Messages.MESSAGE_SLOT_NOT_IN_PLANNER);
            }
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                craftSelectedMessage(), craftSuccessMessage(selectedSlots)));
    }

    /**
     * Craft success message.
     */
    private String craftSuccessMessage(Map<LocalDateTime, ReadOnlySlot> selectedSlots) {
        StringBuilder sb = new StringBuilder();

        sb.append("Details Edited: ");

        if (startTime != null) {
            sb.append("\n\tStart Time: ");
            sb.append("\"");
            sb.append(startTime.toString());
            sb.append("\"");
        }
        if (duration != -1) {
            sb.append("\n\tDuration: ");
            sb.append("\"");
            sb.append(duration);
            sb.append("\"");
        }
        if (location != null) {
            sb.append("\n\tLocation: ");
            sb.append("\"");
            if ("".equals(location)) {
                sb.append("null");
            } else {
                sb.append(location);
            }
            sb.append("\"");
        }
        if (description != null) {
            sb.append("\n\tDescription: ");
            sb.append("\"");
            if ("".equals(description)) {
                sb.append("null");
            } else {
                sb.append(description);
            }
            sb.append("\"");
        }

        sb.append("\n\n");
        sb.append("Edited Slots: ");
        sb.append("\n");

        int count = 1;
        for (Map.Entry<LocalDateTime, ReadOnlySlot> editedSlot : selectedSlots.entrySet()) {
            sb.append("\tItem: ");
            sb.append(count);
            sb.append(": ");
            sb.append("\n\t\t");
            sb.append(editedSlot.getValue().getName().toString());
            sb.append("\n\t\t");
            sb.append(editedSlot.getKey().toLocalDate().toString());
            sb.append(" ");
            sb.append(editedSlot.getKey().toLocalTime().toString());
            sb.append(", ");
            sb.append(planner.getSemester().getDays().get(editedSlot.getKey().toLocalDate()).getType());
            sb.append(", ");
            sb.append(editedSlot.getKey().getDayOfWeek().toString());
            count++;
            sb.append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Craft selected message.
     */
    private String craftSelectedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Selected Slots containing tags: \n");

        for (TagP tag : tags) {
            sb.append("\t");
            sb.append(tag.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
