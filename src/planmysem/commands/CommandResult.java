package planmysem.commands;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javafx.util.Pair;
import planmysem.data.slot.ReadOnlySlot;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    /**
     * The feedback message to be shown to the user. Contains a description of the execution result
     */
    public final String feedbackToUser;

    /**
     * The list of Slots that was produced by the command
     */
    private final List<Pair<LocalDate, ? extends ReadOnlySlot>> slots;

    public CommandResult(String feedbackToUser) {
        this.feedbackToUser = feedbackToUser;
        slots = null;
    }

    public CommandResult(String feedbackToUser, List<Pair<LocalDate, ? extends ReadOnlySlot>> slots) {
        this.feedbackToUser = feedbackToUser;
        this.slots = slots;
    }

    /**
     * Returns list of Slots relevant to the command command result, if any.
     */
    public Optional<List<Pair<LocalDate, ? extends ReadOnlySlot>>> getRelevantSlots() {
        return Optional.ofNullable(slots);
    }

}
