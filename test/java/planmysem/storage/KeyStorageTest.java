package planmysem.storage;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class KeyStorageTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private String tempFolderPath;

    @Before
    public void setup() {
        tempFolderPath = tempFolder.getRoot().getPath() + "Test.jceks";
    }

    @Test
    public void load_keyStore_fileInvalid() throws Exception {
        KeyStorage.load(tempFolderPath);

        assertTrue(new File(tempFolderPath).exists());
    }
}
