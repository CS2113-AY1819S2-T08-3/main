package planmysem.commands;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

import planmysem.data.semester.ReadOnlyDay;

/**
 * Represents the result of a command execution.
 */
public class CommandResultP {

    /**
     * The feedback message to be shown to the user. Contains a description of the execution result
     */
    public final String feedbackToUser;

    /**
     * The list of days that was produced by the command
     */
    private final HashMap<LocalDate, ? extends ReadOnlyDay> days;

    public CommandResultP(String feedbackToUser) {
        this.feedbackToUser = feedbackToUser;
        days = null;
    }

    public CommandResultP(String feedbackToUser, HashMap<LocalDate, ? extends ReadOnlyDay> days) {
        this.feedbackToUser = feedbackToUser;
        this.days = days;
    }

    /**
     * Returns list of Days relevant to the command command result, if any.
     */
    public Optional<HashMap<LocalDate, ? extends ReadOnlyDay>> getRelevantDays() {
        return Optional.ofNullable(days);
    }

}
