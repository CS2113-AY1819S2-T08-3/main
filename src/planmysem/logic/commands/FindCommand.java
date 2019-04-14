package planmysem.logic.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import javafx.util.Pair;

import planmysem.common.Messages;
import planmysem.common.Utils;
import planmysem.logic.CommandHistory;
import planmysem.model.Model;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.semester.WeightedName;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;

/**
 * Finds all slots in planner whose name contains the argument keyword.
 * Keyword matching is case sensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_WORD_SHORT = "f";
    private static final String MESSAGE_SUCCESS = "%1$s Slots listed.\n%2$s";
    private static final String MESSAGE_SUCCESS_NONE = "0 Slots listed.\n";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all slots whose name "
            + "contains the specified keywords (case-sensitive)."
            + "\n\tMandatory Parameters: n/NAME or t/TAG..."
            + "\n\tExample: " + COMMAND_WORD + " n/CS1010";

    private final String keyword;
    private final boolean isFindByName;

    private Queue<WeightedName> weightedNames = new PriorityQueue<>(new Comparator<>() {
        @Override
        public int compare(WeightedName p1, WeightedName p2) {
            String n1 = p1.getName();
            String n2 = p2.getName();
            int d1 = p1.getDist();
            int d2 = p2.getDist();
            LocalDate date1 = p1.getDate();
            LocalDate date2 = p2.getDate();

            if (d1 != d2) {
                return d1 - d2; //order by distance
            } else if (!n1.equalsIgnoreCase(n2)) {
                return n1.compareTo(n2); //order by name
            } else {
                return date1.compareTo(date2); //order by date
            }
        }
    });

    private List<WeightedName> selectedSlots = new ArrayList<>();
    private List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> lastShownList = new ArrayList<>();

    public FindCommand(String name, String tag) {
        this.keyword = (name == null) ? tag.trim() : name.trim();
        this.isFindByName = (name != null);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) {
        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (isFindByName) {
                    generateDiscoveredNames(keyword, slot.getName(), entry, slot);
                } else {
                    Set<String> tagSet = slot.getTags();
                    for (String tag : tagSet) {
                        generateDiscoveredNames(keyword, tag, entry, slot);
                    }
                }
            }
        }

        if (weightedNames.isEmpty()) {
            return new CommandResult(MESSAGE_SUCCESS_NONE);
        }

        while (!weightedNames.isEmpty()) {
            selectedSlots.add(weightedNames.poll());
        }

        for (WeightedName entry : selectedSlots) {
            ReadOnlyDay day = entry.getMap().getValue();
            ReadOnlySlot slot = entry.getSlot();
            Pair<ReadOnlyDay, ReadOnlySlot> pair = new Pair<>(day, slot);
            lastShownList.add(new Pair<>(entry.getMap().getKey(), pair));
        }
        model.setLastShownList(lastShownList);

        return new CommandResult(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessageWeighted(selectedSlots)));
    }

    /**
    * If a slot entry is found, calculates the Levenshtein Distance between the name and the keyword.
    * Updates the weightedNames PQ with the new WeightedName pair containing the name and its weight.
    */
    private void generateDiscoveredNames(String keyword, String compareString,
                                         Map.Entry<LocalDate, Day> entry, Slot slot) {

        // Pattern Matching: Check for presence of keyword in name/tag
        if (!Pattern.matches(".*" + keyword + ".*", compareString)) {
            return;
        }

        //calculate Levenshtein Distance if a match is found
        int dist = Utils.getLevenshteinDistance(keyword, compareString);
        WeightedName distNameTrie = new WeightedName(entry, slot, entry.getKey(), dist);

        weightedNames.add(distNameTrie);
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean getIsFindByName() {
        return isFindByName;
    }
}
