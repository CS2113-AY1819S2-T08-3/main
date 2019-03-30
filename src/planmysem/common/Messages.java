package planmysem.common;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.semester.WeightedName;
import planmysem.model.slot.ReadOnlySlot;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL = "Invalid command format! \n%1$s\n\n%2$s";
    public static final String MESSAGE_INVALID_MULTIPLE_PARAMS = "Either search by NAME or by TAG only, not both.";
    public static final String MESSAGE_INVALID_SLOT_DISPLAYED_INDEX = "The slot index provided is invalid";
    public static final String MESSAGE_SLOT_NOT_IN_PLANNER = "Slot could not be found in Planner";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_SLOTS_LISTED_OVERVIEW = "%1$d slots listed!";
    public static final String MESSAGE_PROGRAM_LAUNCH_ARGS_USAGE = "Launch command format: "
            + "java Main [STORAGE_FILE_PATH]";
    public static final String MESSAGE_WELCOME = "Welcome to PlanMySem!";
    public static final String MESSAGE_USING_STORAGE_FILE = "Using storage file : %1$s";

    public static final String MESSAGE_INVALID_DATE = "Date have to be in either these two formats:"
            + "\n\tIn the form of \"dd-mm\". e.g. \"01-01\""
            + "\n\tIn the form of \"dd-mm-yyyy\". e.g. \"01-01-2019\""
            + "\n\tOr perhaps target the next day of week. e.g. \"Monday\", \"mon\", \"1\"";

    public static final String MESSAGE_INVALID_TIME = "Time have to be in either these two formats:"
            + "\n\t24-Hour in the form of “hh:mm”. e.g. \"23:00\""
            + "\n\t12-Hour in the form of `hh:mm+AM|PM`. e.g. \"12:30am\""
            + "\n\tOr perhaps type a duration in minutes. e.g. \"60\" to represent 60 minutes";

    public static final String MESSAGE_INVALID_TAG = "Tags cannot be empty !";

    public static final String MESSAGE_ILLEGAL_VALUE = "Illegal value detected!";

    /**
     * Craft selected message via tags.
     */
    public static String craftSelectedMessage(Set<String> tags) {
        StringBuilder sb = new StringBuilder();
        sb.append("Selected Slots containing: \n");

        int count = 1;
        for (String tag : tags) {
            sb.append(count);
            sb.append(".\t");
            sb.append(tag);
            sb.append("\n");
            count++;
        }

        return sb.toString();
    }

    /**
     * Craft selected message via index.
     */
    public static String craftSelectedMessage(int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("Selected index: ");
        sb.append(index);

        return sb.toString();
    }

    /**
     * Craft selected message with header.
     */
    public static String craftSelectedMessage(String header,
                                              Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots) {
        StringBuilder sb = new StringBuilder();
        sb.append(header);
        sb.append("\n");

        return sb.toString() + getSelectedMessage(selectedSlots);
    }

    /**
     * Craft selected message via weighted Set of Pairs.
     */
    public static String craftListMessage(List<WeightedName> tries) {
        StringBuilder sb = new StringBuilder();

        sb.append("Here are the closest matching names/tags: \n");

        int count = 1;
        for (WeightedName wn : tries) {
            sb.append("\n");
            sb.append(count + ".\t");
            sb.append("Name: ");
            sb.append(wn.getName());
            sb.append(",\n\t");
            sb.append("Date: ");
            sb.append(wn.getMap().getKey().toString());
            sb.append(",\n\t");
            sb.append("Start Time: ");
            sb.append(wn.getSlot().getStartTime());
            sb.append("\n\t");
            sb.append("Tags: ");
            sb.append(wn.getSlot().getTags());
            sb.append("\n");
            count++;
        }
        return sb.toString();
    }

    /**
     * Craft list message.
     */
    public static String craftListMessage(Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots) {
        StringBuilder sb = new StringBuilder();

        int count = 1;
        for (Map.Entry<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> entry : selectedSlots.entrySet()) {
            sb.append("\n");
            sb.append(count + ".\t");
            sb.append("Name: ");
            sb.append(entry.getValue().getValue().getName());
            sb.append(",\n\t");
            sb.append("Date: ");
            sb.append(entry.getKey().toString());
            sb.append(",\n\t");
            sb.append("Start Time: ");
            sb.append(entry.getValue().getValue().getStartTime());
            sb.append("\n\t");
            sb.append("Tags: ");
            sb.append(entry.getValue().getValue().getTags());
            sb.append("\n");
            count++;
        }
        return sb.toString();
    }

    /**
     * Craft selected message.
     */
    private static String getSelectedMessage(Map<LocalDate,
            Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots) {
        StringBuilder sb = new StringBuilder();

        int count = 1;
        for (Map.Entry<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> entry : selectedSlots.entrySet()) {
            sb.append(count);
            sb.append(".\t");
            sb.append(entry.getValue().getValue().getName());
            sb.append(", ");
            sb.append(entry.getKey());
            sb.append(" ");
            sb.append(entry.getValue().getValue().getStartTime());
            sb.append(", ");
            sb.append(entry.getValue().getKey().getType());
            sb.append(", ");
            sb.append(entry.getKey().getDayOfWeek().toString());
            count++;
            sb.append("\n");
        }

        return sb.toString();
    }
}
