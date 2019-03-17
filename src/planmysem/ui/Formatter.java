package planmysem.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;
import planmysem.data.slot.ReadOnlySlot;

/**
 * Used for formatting text for display. e.g. for adding text decorations.
 */
public class Formatter {

    /**
     * A decorative prefix added to the beginning of lines printed by Planner
     */
    private static final String LINE_PREFIX = " ";

    /**
     * A platform independent line separator.
     */
    private static final String LS = System.lineSeparator();


    /**
     * Format of indexed list item
     */
    private static final String MESSAGE_INDEXED_LIST_ITEM = "\t%1$d. %2$s";


    /**
     * Offset required to convert between 1-indexing and 0-indexing.
     */
    private static final int DISPLAYED_INDEX_OFFSET = 1;

    /**
     * Formats a list of strings as an indexed list.
     */
    private static String asIndexedList(List<String> listItems) {
        final StringBuilder formatted = new StringBuilder();
        int displayIndex = 0 + DISPLAYED_INDEX_OFFSET;
        for (String listItem : listItems) {
            formatted.append(getIndexedListItem(displayIndex, listItem)).append("\n");
            displayIndex++;
        }
        return formatted.toString();
    }

    /**
     * Formats a string as an indexed list item.
     *
     * @param visibleIndex index for this listing
     */
    private static String getIndexedListItem(int visibleIndex, String listItem) {
        return String.format(MESSAGE_INDEXED_LIST_ITEM, visibleIndex, listItem);
    }

    /**
     * Formats the given strings for displaying to the user.
     */
    public String format(String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String m : messages) {
            sb.append(LINE_PREFIX + m.replace("\n", LS + LINE_PREFIX) + LS);
        }
        return sb.toString();
    }

    /**
     * Formats the given list of slots for displaying to the user.
     */
    public String formatSlots(List<Pair<LocalDate, ? extends ReadOnlySlot>> slots) {
        final List<String> formattedSlots = new ArrayList<>();

        for (Pair<LocalDate, ? extends ReadOnlySlot> pair : slots) {
            formattedSlots.add(pair.getKey().toString() + ": " + pair.getValue().toString());
        }

        return format(asIndexedList(formattedSlots));
    }

}
