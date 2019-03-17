package planmysem.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import planmysem.data.semester.Day;
import planmysem.data.slot.Slot;


/**
 * Finds all slots and tags in planner whose name directly matches any of the argument keywords.
 * Keyword matching is case sensitive.
 */
public class FindCommandP extends CommandP {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Finds all slots and tags which directly "
            + "matches the specified keywords (case-sensitive).\n\t"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n\t"
            + "Example: " + COMMAND_WORD + " CS2113T";

    private final Set<String> keywords;

    public FindCommandP(Set<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Returns copy of keywords in this command.
     */
    public Set<String> getKeywords() {
        return new HashSet<>(keywords);
    }

    @Override
    public CommandResultP execute() {
        String result = getSlotsWithTag(keywords).stream().map(Object::toString)
                .collect(Collectors.joining(", "));
        return new CommandResultP(result);
    }

    /**
     * Retrieve all slot in the semesters of the planner whose slots contain some of the specified keywords.
     *
     * @param keywords for searching
     * @return list of persons found
     */
    private List<Slot> getSlotsWithTag(Set<String> keywords) {
        List<Slot> test = new ArrayList<>();
        for (Day days : planner.getSemester().getDays().values()) {
            for (Slot slots : days.getSlots()) {
                for (String keyword : keywords) {
                    if (slots.getName().value.equalsIgnoreCase(keyword)) {
                        test.add(slots);
                    }
                }
            }
        }
        return test;
    }
}
