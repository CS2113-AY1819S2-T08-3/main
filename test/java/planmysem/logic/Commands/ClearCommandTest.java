package planmysem.logic.Commands;

import static planmysem.logic.Commands.CommandTestUtil.assertCommandSuccess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import planmysem.common.Clock;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.ClearCommand;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.Planner;
import planmysem.model.slot.Slot;

public class ClearCommandTest {

    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
    public void execute_emptyPlanner_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        expectedModel.commit();

        assertCommandSuccess(new ClearCommand(), model, commandHistory, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyPlanner_success() throws Exception {
        Model model = new ModelManager(new Planner());
        Model expectedModel = new ModelManager(new Planner());
        expectedModel.commit();

        model.addSlot(
                LocalDate.of(2019, 1, 21),
                new Slot(
                "CS2113T Tutorial",
                "COM2 04-01",
                null,
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                new HashSet<>()
        ));

        assertCommandSuccess(new ClearCommand(), model, commandHistory, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
