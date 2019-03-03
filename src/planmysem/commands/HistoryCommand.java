package planmysem.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;

import planmysem.parser.CommandHistory;

/**
 * Lists all the commands entered by user from the start of app launch.
 */
public class HistoryCommand extends CommandP {

    public static final String COMMAND_WORD = "history";
    public static final String MESSAGE_SUCCESS = "Entered commands (from most recent to earliest):\n%1$s";
    public static final String MESSAGE_NO_HISTORY = "You have not yet entered any commands.";

    private final CommandHistory commandHistory;

    /**
     * TODO: java doc
     */
    public HistoryCommand(CommandHistory commandHistory) {
        this.commandHistory = commandHistory;
    }

    /**
     * TODO: description
     * @return CommandResultP with a list of previousCommands
     */
    public CommandResultP execute() {
        requireNonNull(commandHistory);
        ArrayList<String> previousCommands = new ArrayList<>(commandHistory.getHistory());

        if (previousCommands.isEmpty()) {
            return new CommandResultP(MESSAGE_NO_HISTORY);
        }

        Collections.reverse(previousCommands);
        return new CommandResultP(String.format(MESSAGE_SUCCESS, String.join("\n", previousCommands)));
    }

}

