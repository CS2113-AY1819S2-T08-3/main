package planmysem.logic.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import planmysem.logic.commands.Command;
import planmysem.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public interface Parser<T extends Command> {

    String PREFIX_NAME = "n";
    String PREFIX_DATE_OR_DAY = "d";
    String PREFIX_START_TIME = "st";
    String PREFIX_END_TIME = "et";
    String PREFIX_RECURRENCE = "r";
    String PREFIX_LOCATION = "l";
    String PREFIX_DESCRIPTION = "des";
    String PREFIX_TAG = "t";
    String PREFIX_NEW_NAME = "nn";
    String PREFIX_NEW_DATE = "nd";
    String PREFIX_NEW_START_TIME = "nst";
    String PREFIX_NEW_END_TIME = "net";
    String PREFIX_NEW_LOCATION = "nl";
    String PREFIX_NEW_DESCRIPTION = "ndes";
    String PREFIX_NEW_TAG = "nt";
    String PREFIX_FILE_NAME = "fn";

    /**
     * Parses {@code userInput} into a command and returns it.
     * @throws ParseException if {@code userInput} does not conform the expected format
     */
    T parse(String userInput) throws ParseException;

    /**
     * Parses arguments in the context of the add slots command.
     *
     * @return hashmap of parameter command with set of parameters.
     */
    default HashMap<String, Set<String>> getParametersWithArguments(String args) {
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
    default String getStartingArgument(String args) {
        String result = args;

        // test if firstArgument is present
        if (result.trim().length() == 0) {
            return null;
        } else if (result.indexOf('/') != -1) {
            result = result.substring(0, result.indexOf('/'));
            if (result.lastIndexOf(" ") == -1) {
                return null;
            }
            return result.substring(0, result.lastIndexOf(" ")).trim();
        } else {
            return result.trim();
        }
    }

    /**
     * Get the first string in a set.
     */
    default String getFirstInSet(Set<String> set) {
        if (set == null || set.size() == 0) {
            return null;
        }
        return set.stream().findFirst().get();
    }
}
