//@@author marcus-pzj
package planmysem.logic.Commands;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.AddCommand;
import planmysem.logic.commands.Command;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.RedoCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.logic.Commands.CommandTestUtil.assertCommandFailure;
import static planmysem.logic.Commands.CommandTestUtil.assertCommandSuccess;

import planmysem.logic.parser.ParserManager;
import planmysem.logic.parser.exceptions.ParseException;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.recurrence.Recurrence;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.testutil.SlotBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class RedoCommandTest {
    private Model model;
    private Model expectedModel;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair1;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair2;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair3;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair4;
    private CommandHistory commandHistory = new CommandHistory();

    private SlotBuilder slotBuilder = new SlotBuilder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        Clock.set("2019-01-14T10:00:00Z");
        // Create typical planner
        model = new ModelManager();
        pair1 = new Pair<>(
                LocalDate.of(2019, 02, 01),
                new Pair<>(
                        new Day(
                                DayOfWeek.FRIDAY,
                                "Week 3"
                        ),
                        slotBuilder.generateSlot(1)
                )
        );
        pair2 = new Pair<>(
                LocalDate.of(2019, 02, 02),
                new Pair<>(
                        new Day(
                                DayOfWeek.SATURDAY,
                                "Week 3"
                        ),
                        slotBuilder.generateSlot(2)
                )
        );
        pair3 = new Pair<>(
                LocalDate.of(2019, 02, 03),
                new Pair<>(
                        new Day(
                                DayOfWeek.SUNDAY,
                                "Week 3"
                        ),
                        slotBuilder.generateSlot(3)
                )
        );
        pair4 = new Pair<>(
                LocalDate.of(2019, 02, 04),
                new Pair<>(
                        new Day(
                                DayOfWeek.MONDAY,
                                "Week 4"
                        ),
                        slotBuilder.generateSlot(3)
                )
        );
        model.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        model.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        model.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        model.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));

        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> list = new TreeMap<>();
        list.put(pair4.getKey(), pair4.getValue());
        list.put(pair3.getKey(), pair3.getValue());
        list.put(pair2.getKey(), pair2.getValue());
        list.put(pair1.getKey(), pair1.getValue());
        model.setLastShownList(list);

        expectedModel = model;

        Set<String> recurrenceStrings = new HashSet<>();
        recurrenceStrings.add("normal");
        recurrenceStrings.add("reading");
        Recurrence recurrence = new Recurrence(recurrenceStrings, 1);

        Command addSlotModel = new AddCommand(slotBuilder.generateSlot(1), recurrence);
        addSlotModel.execute(model,commandHistory);
        addSlotModel.execute(expectedModel,commandHistory);
        model.undo();
        expectedModel.undo();
    }

    @Test
    public void execute() {
        // single redoable state in model
        expectedModel.redo();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // no redoable state in model
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    /**
     * Parser Test
     */

    @Test
    public void execute_Valid_RedoCommand_ParserManager() throws Exception {
        ParserManager parserManager = new ParserManager();
        Command actualRedoCommand = parserManager.parseCommand("redo");
        CommandResult expectedRedoCommandOutput = new RedoCommand().execute(model, commandHistory);
        assertEquals(expectedRedoCommandOutput, actualRedoCommand.execute(model, commandHistory));
    }
}
