package planmysem.logic;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import planmysem.data.Planner;
import planmysem.storage.StorageFile;


public class LogicTest {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private StorageFile storgageFile;
    private Planner planner;
    private Logic logic;

    @Before
    public void setup() throws Exception {
        //        saveFile = new StorageFile(saveFolder.newFile("testSaveFile.txt").getPath());
        //        addressBook = new AddressBook();
        //        saveFile.save(addressBook);
        //        logic = new Logic(saveFile, addressBook);
    }

    @Test
    public void constructor() {
        //Constructor is called in the setup() method which executes before every test, no need to call it here again.

        //Confirm the last shown list is empty
        //        assertEquals(Collections.emptyList(), logic.getLastShownList());
    }
}