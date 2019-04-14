//@@author marcus-pzj
package planmysem.logic.Commands;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_MULTIPLE_PARAMS;
import static planmysem.logic.commands.ListCommand.MESSAGE_SUCCESS;
import static planmysem.logic.commands.ListCommand.MESSAGE_SUCCESS_NONE;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.common.Messages;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.ListCommand;
import planmysem.logic.parser.ListCommandParser;
import planmysem.logic.parser.exceptions.ParseException;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;
import planmysem.testutil.SlotBuilder;

public class ListCommandTest {
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

        expectedModel = new ModelManager();
        expectedModel.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        expectedModel.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        expectedModel.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        expectedModel.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));
        expectedModel.setLastShownList(model.getLastShownList());
    }

    /**
     *  Parser Tests
     */

    @Test
    public void execute_Invalid_IncorrectPrefix_throwsParserException() throws Exception {
        ListCommandParser listCommandParser = new ListCommandParser();

        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        listCommandParser.parse("st/08:00");
    }

    @Test
    public void execute_Invalid_MultipleParams_throwsParserException() throws Exception {
        ListCommandParser listCommandParser = new ListCommandParser();

        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_MULTIPLE_PARAMS, ListCommand.MESSAGE_USAGE));
        listCommandParser.parse("n/CS2101 t/Tutorial");
    }

    @Test
    public void execute_ValidPrefixNameOnly_SameKeyword() throws Exception {
        ListCommandParser listCommandParser = new ListCommandParser();
        ListCommand expectedListCommand = new ListCommand("CS2113", null);

        ListCommand actualListCommand = listCommandParser.parse("n/CS2113");

        assertEquals(actualListCommand.getKeyword(), expectedListCommand.getKeyword());
    }

    @Test
    public void execute_ValidPrefixTagOnly_SameKeyword() throws Exception {
        ListCommandParser listCommandParser = new ListCommandParser();
        ListCommand expectedListCommand = new ListCommand(null, "Tutorial");

        ListCommand actualListCommand = listCommandParser.parse("t/Tutorial");

        assertEquals(actualListCommand.getKeyword(), expectedListCommand.getKeyword());
    }

    @Test
    public void execute_parseAll() throws ParseException {
        ListCommandParser listCommandParser = new ListCommandParser();
        ListCommand expectedListCommand = new ListCommand();

        ListCommand actualListCommand = listCommandParser.parse("all");

        assertEquals(actualListCommand.getKeyword(), expectedListCommand.getKeyword());
    }

    /**
     * Constructor Tests
     */

    @Test
    public void constructor_ValidName_NullTag() {
        ListCommand actualListCommand = new ListCommand("CS2101", null);
        String expectedKeyword = "CS2101";

        assertEquals(expectedKeyword, actualListCommand.getKeyword());
    }

    @Test
    public void constructor_NullName_ValidTag() {
        ListCommand actualListCommand = new ListCommand(null, "Tutorial");
        String expectedKeyword = "Tutorial";

        assertEquals(expectedKeyword, actualListCommand.getKeyword());
    }

    /**
     * Logic Tests
     */

    @Test
    public void isFindByName_ValidName_NullTag() {
        ListCommand actualListCommand = new ListCommand("Name", null);

        assertTrue(actualListCommand.getIsListByName());
    }

    @Test
    public void isFindByName_NullName_ValidTag() {
        ListCommand actualListCommand = new ListCommand(null, "Tag");

        assertFalse(actualListCommand.getIsListByName());
    }

    @Test
    public void execute_slotAcceptedByModel_ListNameSuccessful() {
        CommandResult commandResult = new ListCommand(slotBuilder.generateSlot(1).getName(), null).execute(model, commandHistory);

        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (slot.getName().equalsIgnoreCase(slotBuilder.generateSlot(1).getName())) {
                    selectedSlots.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(), slot)));
                }
            }
        }

        assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessage(selectedSlots)), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_ListTagSuccessful() {
        Set<String> tags = slotBuilder.generateSlot(1).getTags();
        String tagToTest = tags.iterator().next();

        CommandResult commandResult = new ListCommand(null, tagToTest).execute(model, commandHistory);

        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                Set<String> tagSet = slot.getTags();
                for (String tag : tagSet) {
                    if (tag.equalsIgnoreCase(tagToTest)) {
                        selectedSlots.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(), slot)));
                    }
                }
            }
        }

        assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessage(selectedSlots)), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_ListNameNotFound() {
        String nameToTest = slotBuilder.generateSlot(1).getName().concat("NotTheSame");

        CommandResult commandResult = new ListCommand(nameToTest, null).execute(model, commandHistory);

        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (slot.getName().equalsIgnoreCase(nameToTest)) {
                    selectedSlots.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(), slot)));
                }
            }
        }
        assertEquals(MESSAGE_SUCCESS_NONE, commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_ListTagNotFound() {
        Set<String> tags = slotBuilder.generateSlot(1).getTags();
        String tagToTest = tags.iterator().next();
        tagToTest = tagToTest.concat("NotTheSame");

        CommandResult commandResult = new ListCommand(null, tagToTest).execute(model, commandHistory);

        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                Set<String> tagSet = slot.getTags();
                for (String tag : tagSet) {
                    if (tag.equalsIgnoreCase(tagToTest)) {
                        selectedSlots.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(), slot)));
                    }
                }
            }
        }
        assertEquals(MESSAGE_SUCCESS_NONE, commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_listAll() {

        CommandResult commandResult = new ListCommand().execute(model, commandHistory);

        final List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> selectedSlots = new ArrayList<>();

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                selectedSlots.add(new Pair<>(entry.getKey(), new Pair<>(entry.getValue(), slot)));
            }
        }

        assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessage(selectedSlots)), commandResult.getFeedbackToUser());
    }
}

