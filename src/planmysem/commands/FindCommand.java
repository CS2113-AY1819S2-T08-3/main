package planmysem.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javafx.util.Pair;

import planmysem.data.semester.Day;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;


/**
 * Finds all slots in planner whose name contains the argument keyword.
 * Keyword matching is case sensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_WORD_SHORT = "f";
    public static final String MESSAGE_SUCCESS = "%1$s Slots listed.\n%2$s";
    public static final String MESSAGE_SUCCESS_NONE = "0 Slots listed.\n";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Finds all slots whose name "
            + "contains the specified keywords (case-sensitive).\n\t"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n\t"
            + "Example: " + COMMAND_WORD + "CS2113T";

    private final Set<String> keywords;

    public FindCommand(Set<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public CommandResult execute() {
        final List<Pair<LocalDate, ? extends ReadOnlySlot>> relevantSlots = new ArrayList<>();
        List<Slot> matchedSlots = new ArrayList<>();
        LocalDate date;

        for (Map.Entry<LocalDate, Day> entry : planner.getSemester().getDays().entrySet()) {
            for (Slot slots : entry.getValue().getSlots()) {
                for (String keyword : keywords) {
                    if (Pattern.matches(".*" + keyword + ".*", slots.getName().value)) {
                        if (!matchedSlots.contains(slots)) {
                            matchedSlots.add(slots);
                            date = entry.getKey();
                            relevantSlots.add(new Pair<>(date, slots));
                        }
                    }
                }
            }
        }

        if (matchedSlots.isEmpty()) {
            return new CommandResult(MESSAGE_SUCCESS_NONE);
        }
        setData(this.planner, relevantSlots);

        return new CommandResult(String.format(MESSAGE_SUCCESS, matchedSlots.size(),
                craftSuccessMessage(relevantSlots)));
    }

    /**
     * Craft success message.
     */
    private String craftSuccessMessage(List<Pair<LocalDate, ? extends ReadOnlySlot>> result) {
        int count = 1;
        StringBuilder sb = new StringBuilder();

        for (Pair<LocalDate, ? extends ReadOnlySlot> pair : result) {
            sb.append("\n");
            sb.append(count + ".\t");
            sb.append("Name: ");
            sb.append(pair.getValue().getName().toString());
            sb.append(",\n\t");
            sb.append("Date: ");
            sb.append(pair.getKey().toString());
            sb.append(",\n\t");
            sb.append("Start Time: ");
            sb.append(pair.getValue().getStartTime());
            sb.append("\n");
            count++;
        }
        return sb.toString();
    }
}
