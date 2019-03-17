package planmysem.commands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import planmysem.data.semester.AdaptedSemester;

/**
 * Exports the calendar into a .ics file.
 */
public class ExportCommandP extends CommandP {

    public static final String COMMAND_WORD = "export";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Delete single or multiple slots in the Planner."
            + "\n\tExample: " + COMMAND_WORD;
    public static final String MESSAGE_EXPORT_ACKNOWEDGEMENT = "Calendar exported";

    @Override
    public CommandResultP execute() {
        AdaptedSemester semester = new AdaptedSemester(planner.getSemester());
        System.out.println(semester.toString());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("PlanMySem.ics"));
            writer.write(semester.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new CommandResultP(MESSAGE_EXPORT_ACKNOWEDGEMENT);
    }

}
