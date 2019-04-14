package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_MULTIPLE_PARAMS;

import java.util.HashMap;
import java.util.Set;

import planmysem.logic.commands.ListCommand;
import planmysem.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns an ListCommand object for execution.
     *
     * @param args full command args string
     * @return the prepared command
     */
    public ListCommand parse(String args) throws ParseException {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);
        String name = getFirstInSet(arguments.get(PREFIX_NAME));
        String tag = getFirstInSet(arguments.get(PREFIX_TAG));
        String option = getStartingArgument(args);

        if (option != null && option.equalsIgnoreCase("all")) {
            return new ListCommand(option);
        }
        if (name == null && tag == null) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        } else if (name != null && tag != null) {
            throw new ParseException(String.format(MESSAGE_INVALID_MULTIPLE_PARAMS, ListCommand.MESSAGE_USAGE));
        }
        return new ListCommand(name, tag);
    }
}
