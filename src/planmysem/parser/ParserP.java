package planmysem.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import planmysem.commands.AddCommandP;
import planmysem.commands.ClearCommandP;
import planmysem.commands.Command;
import planmysem.commands.CommandP;
import planmysem.commands.DeleteCommandP;
import planmysem.commands.EditCommandP;
import planmysem.commands.ExitCommandP;
import planmysem.commands.ExportCommandP;
import planmysem.commands.FindCommand;
import planmysem.commands.HelpCommand;
import planmysem.commands.HelpCommandP;
import planmysem.commands.IncorrectCommand;
import planmysem.commands.IncorrectCommandP;
import planmysem.commands.ListCommandP;
import planmysem.commands.ViewCommand;
import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;

/**
 * Parses user input.
 */
public class ParserP {

    public static final Pattern PERSON_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    public static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace

    private static final String PARAMETER_NAME = "n";
    private static final String PARAMETER_DATE_OR_DAY = "d";
    private static final String PARAMETER_START_TIME = "st";
    private static final String PARAMETER_END_TIME = "et";
    private static final String PARAMETER_RECURRENCE = "r";
    private static final String PARAMETER_LOCATION = "l";
    private static final String PARAMETER_DESCRIPTION = "des";
    private static final String PARAMETER_TAG = "t";
    private static final String PARAMETER_NEW_NAME = "nn";
    private static final String PARAMETER_NEW_DATE = "nd";
    private static final String PARAMETER_NEW_START_TIME = "nst";
    private static final String PARAMETER_NEW_END_TIME = "net";
    private static final String PARAMETER_NEW_LOCATION = "nl";
    private static final String PARAMETER_NEW_DESCRIPTION = "ndes";
    private static final String PARAMETER_NEW_TAG = "nt";


    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public CommandP parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        switch (commandWord) {
        case AddCommandP.COMMAND_WORD:
        case AddCommandP.COMMAND_WORD_SHORT:
            return prepareAdd(arguments);

        case EditCommandP.COMMAND_WORD:
        case EditCommandP.COMMAND_WORD_SHORT:
            return prepareEdit(arguments);

        case ExportCommandP.COMMAND_WORD:
            return new ExportCommandP();

        case DeleteCommandP.COMMAND_WORD:
        case DeleteCommandP.COMMAND_WORD_ALT:
        case DeleteCommandP.COMMAND_WORD_SHORT:
            return prepareDelete(arguments);

        case ListCommandP.COMMAND_WORD:
        case ListCommandP.COMMAND_WORD_SHORT:
            return prepareList(arguments);

        case ClearCommandP.COMMAND_WORD:
            return new ClearCommandP();

        case ExitCommandP.COMMAND_WORD:
            return new ExitCommandP();

        case HelpCommandP.COMMAND_WORD: // Fallthrough

        default:
            return new HelpCommandP();
        }
    }

    /**
     * Parses arguments in the context of the add command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private CommandP prepareAdd(String args) {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);

        // Name is mandatory
        String name = getFirstInSet(arguments.get(PARAMETER_NAME));

        if (arguments.isEmpty() || name.isEmpty() || arguments.get(PARAMETER_DATE_OR_DAY) == null
                || arguments.get(PARAMETER_START_TIME) == null || arguments.get(PARAMETER_END_TIME) == null) {
            return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommandP.MESSAGE_USAGE));
        }

        // Either date or day must be present
        String dateOrDay = getFirstInSet(arguments.get(PARAMETER_DATE_OR_DAY));
        int day = Utils.parseDay(dateOrDay);
        LocalDate date = null;
        if (day == 0) {
            date = Utils.parseDate(dateOrDay);
        }
        if (day == 0 && date == null) {
            return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommandP.MESSAGE_USAGE));
        }

        // Start time is mandatory
        String stringStartTime = getFirstInSet(arguments.get(PARAMETER_START_TIME));
        LocalTime startTime = Utils.parseTime(stringStartTime);
        if (startTime == null) {
            return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommandP.MESSAGE_USAGE));
        }

        // determine if "end time" is a duration or time
        String stringEndTime = getFirstInSet(arguments.get(PARAMETER_END_TIME));
        int duration = Utils.parseInteger(stringEndTime);

        if (duration == -1) {
            LocalTime endTime = Utils.parseTime(stringEndTime);
            if (endTime == null) {
                return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommandP.MESSAGE_USAGE));
            }
            duration = Utils.getDuration(startTime, endTime);
        }

        // Description is not mandatory and can be null
        String description = getFirstInSet(arguments.get(PARAMETER_DESCRIPTION));

        // Location is not mandatory and can be null
        String location = getFirstInSet(arguments.get(PARAMETER_LOCATION));

        // Tags is not mandatory
        Set<String> tags = arguments.get(PARAMETER_TAG);
        for (String tag : tags) {
            if (tag.length() == 0) {
                return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommandP.MESSAGE_USAGE));
            }
        }

        // Recurrences is not mandatory
        Set<String> recurrences = arguments.get(PARAMETER_RECURRENCE);

        try {
            if (day != -1) {
                return new AddCommandP(
                        day,
                        name,
                        location,
                        description,
                        startTime,
                        duration,
                        tags,
                        recurrences
                );
            } else {
                return new AddCommandP(
                        date,
                        name,
                        location,
                        description,
                        startTime,
                        duration,
                        tags,
                        recurrences
                );
            }
        } catch (IllegalValueException ive) {
            return new IncorrectCommandP(ive.getMessage());
        }
    }

    /**
     * Parses arguments in the context of the edit command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private CommandP prepareEdit(String args) {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);
        String stringIndex = getStartingArgument(args);
        int index = Utils.parseInteger(stringIndex);
        Set<String> tags = arguments.get(PARAMETER_TAG);

        if ((index == -1 && tags == null) || (index != -1 && tags != null)) {
            return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommandP.MESSAGE_USAGE));
        }

        String nst = getFirstInSet(arguments.get(PARAMETER_NEW_START_TIME));
        LocalTime startTime = null;
        if (nst != null) {
            startTime = Utils.parseTime(nst);
            if (startTime == null) {
                return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommandP.MESSAGE_USAGE));
            }
        }

        // determine if "end time" is a duration or time
        String net = getFirstInSet(arguments.get(PARAMETER_NEW_END_TIME));
        int duration = -1;
        if (net != null) {
            duration = Utils.parseInteger(net);
            if (duration == -1) {
                LocalTime endTime = Utils.parseTime(net);
                if (endTime == null) {
                    return new IncorrectCommandP(String.format(
                            MESSAGE_INVALID_COMMAND_FORMAT, EditCommandP.MESSAGE_USAGE));
                } else {
                    duration = Utils.getDuration(startTime, endTime);
                }
            }
        }

        String name = getFirstInSet(arguments.get(PARAMETER_NEW_NAME));
        String location = getFirstInSet(arguments.get(PARAMETER_NEW_LOCATION));
        String description = getFirstInSet(arguments.get(PARAMETER_NEW_DESCRIPTION));
        Set<String> newTags = arguments.get(PARAMETER_NEW_TAG);

        if (index == -1) {
            try {
                return new EditCommandP(name, startTime, duration, location, description, tags, newTags);
            } catch (IllegalValueException ive) {
                return new IncorrectCommandP(ive.getMessage());
            }
        } else {
            String nd = getFirstInSet(arguments.get(PARAMETER_NEW_DATE));
            LocalDate date = Utils.parseDate(nd);
            if (date == null) {
                return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommandP.MESSAGE_USAGE));
            }

            try {
                return new EditCommandP(index, name, date, startTime, duration, location, description, newTags);
            } catch (IllegalValueException ive) {
                return new IncorrectCommandP(ive.getMessage());
            }
        }
    }

    /**
     * Parses arguments in the context of the delete command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private CommandP prepareDelete(String args) {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);
        String stringIndex = getStartingArgument(args);
        int index = Utils.parseInteger(stringIndex);
        Set<String> tags = arguments.get(PARAMETER_TAG);

        if ((index == -1 && tags == null) || (index != -1 && tags != null)) {
            return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommandP.MESSAGE_USAGE));
        }

        if (index == -1) {
            try {
                return new DeleteCommandP(tags);
            } catch (IllegalValueException ive) {
                return new IncorrectCommandP(ive.getMessage());
            }
        } else {
            return new DeleteCommandP(index);
        }
    }

    /**
     * Parses arguments in the context of the add Slot command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private CommandP prepareList(String args) {
        HashMap<String, Set<String>> arguments = getParametersWithArguments(args);


        // Validate arg string format
        if (args.isEmpty() || arguments.isEmpty()) {
            return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommandP.MESSAGE_USAGE));
        }

        // TODO: prepare list
        //        try {
        // TODO: add exception handling
        return new ListCommandP(arguments.get(PARAMETER_START_TIME));
        //        } catch (IllegalValueException ive) {
        //            return new IncorrectCommandP(ive.getMessage());
        //        }
    }

    /**
     * Parses arguments in the context of the view command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareView(String args) {

        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new ViewCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the given arguments string as a single index number.
     *
     * @param args arguments string to parse as index number
     * @return the parsed index number
     * @throws ParseException        if no region of the args string could be found for the index
     * @throws NumberFormatException the args string region is not a valid number
     */
    private int parseArgsAsDisplayedIndex(String args) throws ParseException, NumberFormatException {
        final Matcher matcher = PERSON_INDEX_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException("Could not find index number to parse");
        }
        return Integer.parseInt(matcher.group("targetIndex"));
    }

    /**
     * Parses arguments in the context of the find person command.
     *
     * @param args partial args string
     * @return the arguments sorted by its relevant options
     */
    private Command prepareFind(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindCommand.MESSAGE_USAGE));
        }

        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        return new FindCommand(keywordSet);
    }

    /**
     * Parses arguments in the context of the add slots command.
     *
     * @return hashmap of parameter command with set of parameters.
     */
    private static HashMap<String, Set<String>> getParametersWithArguments(String args) {
        String parameters = args.trim();
        String parameter;
        String option;
        int buf;
        HashMap<String, Set<String>> result = new HashMap<>();
        while (true) {
            buf = parameters.indexOf('/');
            if (buf == -1) {
                break;
            }

            option = parameters.substring(0, buf).trim();
            if (option.contains(" ")) {
                parameters = parameters.substring(option.lastIndexOf(" "));
                continue;
            }

            parameters = parameters.substring(buf + 1);

            if (parameters.indexOf('/') != -1) {
                parameter = parameters.substring(0, parameters.indexOf('/'));
                if (parameter.indexOf(' ') != -1) {
                    parameter = parameter.substring(0, parameter.lastIndexOf(" "));
                }
            } else {
                parameter = parameters;
            }

            if (result.get(option) == null) {
                result.put(option, new HashSet<>(Collections.singletonList(parameter.trim())));
            } else {
                Set<String> ss = result.get(option);
                ss.add(parameter.trim());
                result.replace(option, ss);
            }

            if (parameters.length() == 0) {
                break;
            }
            parameters = parameters.substring(parameter.length());
        }

        return result;
    }

    /**
     * Get the first argument.
     */
    private String getStartingArgument(String args) {
        String result = args;

        // test if firstArgument is present
        if (result.trim().length() == 0) {
            return null;
        } else if (result.indexOf('/') != -1) {
            result = result.substring(0, result.indexOf('/'));
            return result.substring(0, result.lastIndexOf(" ")).trim();
        } else {
            return result.trim();
        }
    }

    /**
     * Get the first string in a set.
     */
    private String getFirstInSet(Set<String> set) {
        if (set == null || set.size() == 0) {
            return null;
        }
        return set.stream().findFirst().get();
    }

    /**
     * Signals that the user input could not be parsed.
     */
    public static class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }
}
