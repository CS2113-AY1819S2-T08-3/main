package planmysem.logic.commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import planmysem.logic.CommandHistory;
import planmysem.logic.commands.exceptions.CommandException;
import planmysem.model.Model;
import planmysem.model.Planner;
import planmysem.model.semester.IcsSemester;
import planmysem.model.semester.Semester;

/**
 * Exports the calendar into a .ics file.
 */
public class ExportCommand extends Command {

    public static final String COMMAND_WORD = "export";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Export the planner as a .ics file."
            + "\n\tParameters: "
            + "\n\t\tOptional: [fn/FILENAME]"
            + "\n\tExample: " + COMMAND_WORD + " my_planner.ics";
    public static final String MESSAGE_SUCCESS = "Calendar exported.";
    public static final String MESSAGE_FAILED = "Export failed. File cannot be created";
    private final String fileName;

    public ExportCommand(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) throws CommandException {
        Planner planner = model.getPlanner();
        Semester semester = planner.getSemester();
        IcsSemester icsSemester = new IcsSemester(semester);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".ics"));
            writer.write(icsSemester.toString());
            writer.close();
        } catch (IOException e) {
            throw new CommandException(MESSAGE_FAILED);
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ExportCommand // instanceof handles nulls
                && fileName.equals(((ExportCommand) other).fileName));
    }
}
