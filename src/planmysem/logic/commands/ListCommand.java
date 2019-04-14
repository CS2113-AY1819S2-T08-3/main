//@@author marcus-pzj
package planmysem.logic.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.logic.CommandHistory;
import planmysem.model.Model;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;

/**
 * Displays a list of all slots in the planner whose name matches the argument keyword.
 * Keyword matching is case sensitive.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String COMMAND_WORD_SHORT = "l";
    public static final String MESSAGE_SUCCESS = "%1$s Slots listed.\n%2$s";
    public static final String MESSAGE_SUCCESS_NONE = "0 Slots listed.\n";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all slots/ slots whose name "
            + "directly matches the specified keyword (not case-sensitive)."
            + "\n\tMandatory Parameters: n/NAME or t/TAG... or o/OPTION"
            + "\n\tExample: " + COMMAND_WORD + " n/CS1010"
            + "\n\tExample: " + COMMAND_WORD + " all";

    private final String keyword;
    private final boolean isListByName;
    private final boolean isListAll;

    public ListCommand(String name, String tag) {
        this.keyword = (name == null) ? tag.trim() : name.trim();
        this.isListByName = (name != null);
        this.isListAll = false;
    }

    public ListCommand() {
        this.keyword = null;
        this.isListByName = false;
        this.isListAll = true;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) {
        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (isListAll) {
                    selectedSlots.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(), slot)));
                } else if (isListByName) {
                    if (slot.getName().equalsIgnoreCase(keyword)) {
                        selectedSlots.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(), slot)));
                    }
                } else {
                    Set<String> tagSet = slot.getTags();
                    for (String tag : tagSet) {
                        if (tag.equalsIgnoreCase(keyword)) {
                            selectedSlots.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(), slot)));
                        }
                    }
                }
            }
        }

        if (selectedSlots.isEmpty()) {
            return new CommandResult(MESSAGE_SUCCESS_NONE);
        }

        model.setLastShownList(selectedSlots);

        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessage(selectedSlots)));
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean getIsListByName() {
        return isListByName;
    }
}


