package planmysem.common;

import java.time.LocalDate;
import java.util.List;
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
    public static final String MESSAGE_PROGRAM_LAUNCH_ARGS_USAGE = "Launch command format: "
            + "java Main [STORAGE_FILE_PATH]";
    public static final String MESSAGE_WELCOME = "Welcome to PlanMySem!";
    public static final String MESSAGE_USING_STORAGE_FILE = "Using storage file : %1$s";
    public static final String MESSAGE_NOTHING_TO_EDIT = "There are no details to edit.";
    public static final String MESSAGE_INVALID_DATE_OR_DAY = "Date have to be in either these two formats:"
            + "\n\tIn the form of \"dd-mm\". e.g. \"01-01\""
            + "\n\tIn the form of \"dd-mm-yyyy\". e.g. \"01-01-2019\""
            + "\n\tOr perhaps target the next day of week."
            + "\n\t\tIn the form of the day itself, \"Monday\", in 3-letter short forms, \"mon\" "
            + "or numbered day of week, \"1\"";
    public static final String MESSAGE_INVALID_TIME = "Time have to be in either these two formats:"
            + "\n\t24-Hour in the form of “hh:mm”. e.g. \"23:00\""
            + "\n\t12-Hour in the form of `hh:mm AM|PM`. e.g. \"12:30 am\""
            + "\n\tOr perhaps type a duration in minutes. e.g. \"60\" to represent 60 minutes";
    public static final String MESSAGE_INVALID_ENDTIME = "A slot is not able to have an end time that is "
            + "before it's start time.";

    public static final String MESSAGE_INVALID_TAG = "Tags cannot be empty !";
    public static final String MESSAGE_ILLEGAL_VALUE = "Illegal value detected!";
    public static final String MESSAGE_ILLEGAL_WEEK_VALUE = "No such week is found in the current semester!";
    public static final String MESSAGE_DATE_OUT_OF_BOUNDS = "No such date is found in the current semester!";

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
        sb.append("\nEnter 'list n/{name} OR t/{tag}' to list all slots related to the name/tag\n");

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
                                              List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots) {
        StringBuilder sb = new StringBuilder();
        sb.append(header);
        sb.append("\n");

        return sb.toString() + getSelectedMessage(selectedSlots);
    }

    /**
     * Craft selected message via weighted Set of Pairs.
     */
    public static String craftListMessageWeighted(List<WeightedName> tries) {
        StringBuilder sb = new StringBuilder();

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
        sb.append("\n To view more information about a particular slot, try the 'view day' command!");
        return sb.toString();
    }

    /**
     * Craft list message.
     */
    public static String craftListMessage(List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots) {
        StringBuilder sb = new StringBuilder();

        int count = 1;
        for (Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> entry : selectedSlots) {
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
        sb.append("\n To view more information about a particular slot, try the 'view day' command!");
        return sb.toString();
    }

    /**
     * Craft selected message.
     */
    private static String getSelectedMessage(
            List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots) {
        selectedSlots.sort((p1, p2) -> p1.getKey().compareTo(p2.getKey()));

        StringBuilder sb = new StringBuilder();

        int count = 1;
        for (Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> entry : selectedSlots) {
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
