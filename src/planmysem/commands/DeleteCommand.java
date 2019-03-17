package planmysem.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.semester.Day;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;
import planmysem.data.tag.TagP;

/**
 * Adds a person to the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String COMMAND_WORD_ALT = "del";
    public static final String COMMAND_WORD_SHORT = "d";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Delete single or multiple slots in the Planner."
            + "\n\tParameters: "
            + "\n\t\tMandatory: t/TAG... or INDEX"
            + "\n\tExample 1: " + COMMAND_WORD
            + " t/CS2113T t/Tutorial"
            + "\n\tExample 2: " + COMMAND_WORD
            + " 2";


    public static final String MESSAGE_SUCCESS_NO_CHANGE = "No Slots were deleted.\n\n%1$s";
    public static final String MESSAGE_SUCCESS = "%1$s Slots deleted.\n\n%2$s\n%3$s";
    public static final String MESSAGE_FAIL_ILLEGAL_VALUE = MESSAGE_SUCCESS_NO_CHANGE
            + " Illegal characters were detected.";

    private final Set<TagP> tags = new HashSet<>();

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public DeleteCommand(Set<String> tags) throws IllegalValueException {
        this.tags.addAll(Utils.parseTags(tags));
    }

    /**
     * Convenience constructor using raw values.
     */
    public DeleteCommand(int index) {
        super(index);
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

        // perform deletion of slots from the planner
        for (Map.Entry<LocalDateTime, ? extends ReadOnlySlot> slot: selectedSlots.entrySet()) {
            planner.getSemester().getDays().get(slot.getKey().toLocalDate()).removeSlot(slot.getValue());
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                craftSelectedMessage(), craftSuccessMessage(selectedSlots)));
    }

    /**
     * Craft success message.
     */
    private String craftSuccessMessage(Map<LocalDateTime, ReadOnlySlot> selectedSlots) {
        StringBuilder sb = new StringBuilder();

        sb.append("Deleted Slots: ");
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
