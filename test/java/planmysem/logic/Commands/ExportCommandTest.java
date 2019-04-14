package planmysem.logic.Commands;

import static planmysem.logic.Commands.CommandTestUtil.assertCommandFailure;
import static planmysem.logic.Commands.CommandTestUtil.assertCommandSuccess;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;

import planmysem.common.Clock;

import planmysem.logic.CommandHistory;
import planmysem.logic.commands.ExportCommand;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.testutil.SlotBuilder;

public class ExportCommandTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private String tempFolderPath;
    private Model model;
    private Model expectedModel;
    private CommandHistory commandHistory = new CommandHistory();

    private SlotBuilder slotBuilder = new SlotBuilder();


    @Before
    public void setup() throws Exception {

        Clock.set("2019-01-14T10:00:00Z");

        tempFolderPath = tempFolder.getRoot().getPath();

        // Create typical planner
        model = new ModelManager();
        model.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        model.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        model.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        model.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));

        expectedModel = new ModelManager();
        expectedModel.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        expectedModel.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        expectedModel.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        expectedModel.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));
    }

    @Test
    public void execute_export_success() throws IOException {
        assertCommandSuccess(new ExportCommand(tempFolderPath + "\\ExportTest"), model,
                commandHistory, ExportCommand.MESSAGE_SUCCESS, expectedModel);
        String expectedIcs = "BEGIN:VCALENDAR\r\n"
                + "VERSION:2.0\r\n"
                + "BEGIN:VEVENT\r\n"
                + "DTSTART:20190204T000000\r\n"
                + "DTEND:20190204T000000\r\n"
                + "SUMMARY:slot 3\r\n"
                + "LOCATION:location 3\r\n"
                + "DESCRIPTION:description 3\r\n"
                + "X-TAGS:tag4,tag3,\r\n"
                + "END:VEVENT\r\n"
                + "BEGIN:VEVENT\r\n"
                + "DTSTART:20190203T000000\r\n"
                + "DTEND:20190203T000000\r\n"
                + "SUMMARY:slot 3\r\n"
                + "LOCATION:location 3\r\n"
                + "DESCRIPTION:description 3\r\n"
                + "X-TAGS:tag4,tag3,\r\n"
                + "END:VEVENT\r\n"
                + "BEGIN:VEVENT\r\n"
                + "DTSTART:20190202T000000\r\n"
                + "DTEND:20190202T000000\r\n"
                + "SUMMARY:slot 2\r\n"
                + "LOCATION:location 2\r\n"
                + "DESCRIPTION:description 2\r\n"
                + "X-TAGS:tag2,tag3,\r\n"
                + "END:VEVENT\r\n"
                + "BEGIN:VEVENT\r\n"
                + "DTSTART:20190201T000000\r\n"
                + "DTEND:20190201T000000\r\n"
                + "SUMMARY:slot 1\r\n"
                + "LOCATION:location 1\r\n"
                + "DESCRIPTION:description 1\r\n"
                + "X-TAGS:tag1,tag2,\r\n"
                + "END:VEVENT\r\n"
                + "END:VCALENDAR\r\n";
        String actualIcs = new String(Files.readAllBytes(Paths.get(tempFolderPath + "\\ExportTest.ics")));

        Assert.assertEquals(actualIcs, expectedIcs);
    }

    @Test
    public void execute_export_fail() {
        Model model = new ModelManager();
        assertCommandFailure(new ExportCommand("\\/:*?\"<>|"), model, commandHistory,
                ExportCommand.MESSAGE_FAILED);
    }
}
