package planmysem.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import planmysem.commands.AddCommandP;
import planmysem.commands.Command;
import planmysem.commands.CommandP;
import planmysem.commands.DeleteCommand;
import planmysem.commands.ExitCommandP;
import planmysem.commands.FindCommand;
import planmysem.commands.HelpCommand;
import planmysem.commands.HelpCommandP;
import planmysem.commands.IncorrectCommand;
import planmysem.commands.IncorrectCommandP;
import planmysem.commands.ViewAllCommand;
import planmysem.commands.ViewCommand;
import planmysem.data.exception.IllegalValueException;

/**
 * Parses user input.
 */
public class ParserP {

    public static final Pattern PERSON_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    public static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace

    public static final Pattern SLOT_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("n/(?<name>[^/]+)"
                    + " l/(?<location>[^/]+)"
                    + " d/(?<description>[^/]+)"
                    + " st/(?<startTime>[^/]+)"
                    + " et/(?<endTime>[^/]+)"
                    + " duration/(?<duration>[^/]+)"
                    + "(?<tags>(?: t/[^/]+)*)"); // variable number of tags

    public static final Pattern SLOT_NAME_ARGS_FORMAT = Pattern.compile("(?<name>[^/]+)");
    public static final Pattern SLOT_LOCATION_ARGS_FORMAT = Pattern.compile(" l/(?<location>[^/]+)");
    public static final Pattern SLOT_DESCRIPTION_ARGS_FORMAT = Pattern.compile(" d/(?<description>[^/]+)");
    public static final Pattern SLOT_DATE_ARGS_FORMAT = Pattern.compile(" date/(?<date>[^/]+)");
    public static final Pattern SLOT_START_TIME_ARGS_FORMAT = Pattern.compile(" st/(?<startTime>[^/]+)");
    public static final Pattern SLOT_END_TIME_ARGS_FORMAT = Pattern.compile(" et/(?<endTime>[^/]+)");
    public static final Pattern SLOT_DURATION_ARGS_FORMAT = Pattern.compile(" et/(?<duration>[0-9]+)");
    public static final Pattern SLOT_TAG_ARGS_FORMAT = Pattern.compile("(?<tag>(?: t/[^/]+)*)");
    public static final Pattern SLOT_RECURRENCE_ARGS_FORMAT = Pattern.compile("(?<recurrence>(?: r/[^/]+)*)");

    /**
     * Used for initial separation of command word and args.
     */
    public static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Extracts the new Slot's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings =
            Arrays.asList(
                tagArguments.replaceFirst(" t/", "")
                    .split(" t/"));
        return new HashSet<>(tagStrings);
    }

    /**
     * Extracts the new Slot's recursive arguments from the add command's recurse arguments string.
     * Merges duplicate recursive strings.
     */
    private static Set<String> getRecurrencesFromArgs(String recursiveArguments) throws IllegalValueException {
        // no tags
        if (recursiveArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(recursiveArguments.replaceFirst(" r/", "").split(" r/"));
        return new HashSet<>(tagStrings);
    }

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
            return prepareAdd(arguments);

        case ExitCommandP.COMMAND_WORD:
            return new ExitCommandP();

        case HelpCommandP.COMMAND_WORD: // Fallthrough
        default:
            return new HelpCommandP();
        }
    }

    /**
     * Parses arguments in the context of the add Slot command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private CommandP prepareAdd(String args) {
        String arguments = args.trim();
        final Matcher nameMatcher = SLOT_NAME_ARGS_FORMAT.matcher(arguments);
        final Matcher locationMatcher = SLOT_LOCATION_ARGS_FORMAT.matcher(arguments);
        final Matcher descriptionMatcher = SLOT_DESCRIPTION_ARGS_FORMAT.matcher(arguments);
        final Matcher dateMatcher = SLOT_DATE_ARGS_FORMAT.matcher(arguments);
        final Matcher startTimeMatcher = SLOT_START_TIME_ARGS_FORMAT.matcher(arguments);
        final Matcher endTimeMatcher = SLOT_END_TIME_ARGS_FORMAT.matcher(arguments);
        final Matcher durationMatcher = SLOT_DURATION_ARGS_FORMAT.matcher(arguments);
        final Matcher tagsMatcher = SLOT_TAG_ARGS_FORMAT.matcher(arguments);
        final Matcher recurseMatcher = SLOT_RECURRENCE_ARGS_FORMAT.matcher(arguments);

        // Validate arg string format
        if (nameMatcher.find() && startTimeMatcher.find() && (endTimeMatcher.find() || durationMatcher.find())) {
            return new IncorrectCommandP(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommandP.MESSAGE_USAGE));
        }

        // Here it does not matter if it is a day or date stored yet
        String date = null;
        if (dateMatcher.find()) {
            date = dateMatcher.group("date");
        }

        // Description is not mandatory and can be null
        String description = null;
        if (descriptionMatcher.find()) {
            description = descriptionMatcher.group("description");
        }

        // Location is not mandatory and can be null
        String location = null;
        if (locationMatcher.find()) {
            location = descriptionMatcher.group("location");
        }

        if (endTimeMatcher.find()) {
            try {
                return new AddCommandP(
                        date,
                        nameMatcher.group("name"),
                        location,
                        description,
                        startTimeMatcher.group("startTime"),
                        endTimeMatcher.group("endTime"),
                        getTagsFromArgs(tagsMatcher.group("tag")),
                        getRecurrencesFromArgs(recurseMatcher.group("recurrence"))
                );
            } catch (IllegalValueException ive) {
                return new IncorrectCommandP(ive.getMessage());
            }
        } else {
            // parse duration string into int
            try {
                return new AddCommandP(
                        date,
                        nameMatcher.group("name"),
                        location,
                        description,
                        startTimeMatcher.group("startTime"),
                        Integer.parseInt(durationMatcher.group("duration")),
                        getTagsFromArgs(tagsMatcher.group("tag")),
                        getRecurrencesFromArgs(recurseMatcher.group("recurrence"))
                );
            } catch (IllegalValueException ive) {
                return new IncorrectCommandP(ive.getMessage());
            }
        }
    }

    /**
     * Parses arguments in the context of the delete person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new DeleteCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
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
     * Parses arguments in the context of the view all command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareViewAll(String args) {

        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new ViewAllCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewAllCommand.MESSAGE_USAGE));
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
     * @param args full command args string
     * @return the prepared command
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
     * Signals that the user input could not be parsed.
     */
    public static class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }
}
