package planmysem.logic.Commands;

import static planmysem.logic.Commands.CommandTestUtil.assertCommandFailure;
import static planmysem.logic.Commands.CommandTestUtil.assertCommandSuccess;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;

import planmysem.common.Clock;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.ImportCommand;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.testutil.SlotBuilder;

public class ImportCommandTest {

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

        expectedModel = new ModelManager();
        expectedModel.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        expectedModel.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        expectedModel.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        expectedModel.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));
        expectedModel.setLastShownList(model.getLastShownList());

        //create test file with all valid events
        File importTest = tempFolder.newFile("ImportTest.ics");
        FileWriter fileWriter = new FileWriter(importTest);
        fileWriter.write("BEGIN:VCALENDAR\r\n"
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
                + "END:VCALENDAR\r\n");
        fileWriter.close();

        //create test file with 1 invalid event
        File importTest2 = tempFolder.newFile("ImportTest2.ics");
        fileWriter = new FileWriter(importTest2);
        fileWriter.write("BEGIN:VCALENDAR\r\n"
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
                + "BEGIN:VEVENT\n"
                + "DTSTART:20100204T000000\r\n"
                + "DTEND:20100204T000000\r\n"
                + "SUMMARY:slot 4\r\n"
                + "LOCATION:location 4\r\n"
                + "DESCRIPTION:description 4\r\n"
                + "X-TAGS:tag4,tag5,\r\n"
                + "END:VEVENT\r\n"
                + "END:VCALENDAR\r\n");
        fileWriter.close();
    }

    //test for all valid events
    @Test
    public void execute_import_success() {
        String expectedMessage = ImportCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(new ImportCommand(tempFolderPath + "/ImportTest.ics"), model, commandHistory,
                expectedMessage, expectedModel);
    }

    //test for 1 invalid event
    @Test
    public void execute_import_invalidEvent() {
        String expectedMessage = ImportCommand.MESSAGE_SUCCESS + "1 event(s) failed to import.\n";
        assertCommandSuccess(new ImportCommand(tempFolderPath + "/ImportTest2.ics"), model, commandHistory,
                expectedMessage, expectedModel);
    }

    //test for invalid file
    @Test
    public void execute_import_fail() {
        String expectedMessage = ImportCommand.MESSAGE_ERROR_IN_READING_FILE;
        assertCommandFailure(new ImportCommand(tempFolderPath + "/FileDoesNotExist.ics"), model,
                commandHistory, expectedMessage);
    }
}
