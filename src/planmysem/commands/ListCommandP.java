package planmysem.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

import planmysem.data.semester.Day;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;

/**
 * Finds and lists all slots in planner whose name contains any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class ListCommandP extends CommandP {

    public static final String COMMAND_WORD = "list";
    public static final String COMMAND_WORD_SHORT = "l";
    public static final String MESSAGE_SUCCESS = "%1$s Slots listed.\n%2$s";
    public static final String MESSAGE_SUCCESS_NONE = "0 Slots listed.\n";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all slots."
            + "\n\tOptional Parameters: [past] [next] [all]"
            + "\n\tDefault: list all"
            + "\n\tExample: " + COMMAND_WORD + " CS1010 tutorial lab";

    private final String name;

    public ListCommandP(String name) {
        this.name = name;
    }
    @Override
    public CommandResultP execute() {
        final List<Pair<LocalDate, ? extends ReadOnlySlot>> relevantSlots = new ArrayList<>();
        List<Slot> matchedSlots = new ArrayList<>();
        LocalDate date;

        for (Map.Entry<LocalDate, Day> entry : planner.getSemester().getDays().entrySet()) {
            for (Slot slots : entry.getValue().getSlots()) {
                if (slots.getName().value.equalsIgnoreCase(name)) {
                    matchedSlots.add(slots);
                    date = entry.getKey();
                    relevantSlots.add(new Pair<>(date, slots));
                }
            }
        }

        if (matchedSlots.isEmpty()) {
            return new CommandResultP(MESSAGE_SUCCESS_NONE);
        }
        setData(this.planner, relevantSlots);

        return new CommandResultP(String.format(MESSAGE_SUCCESS, matchedSlots.size(),
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


