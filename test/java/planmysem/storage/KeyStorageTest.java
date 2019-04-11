package planmysem.storage;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;

public class KeyStorageTest {

    @Test
    public void load_keyStore_fileInvalid() throws Exception {
        Files.deleteIfExists(new File("KeyStorage.jceks").toPath());
        KeyStorage.load();

        assertTrue(new File("KeyStorage.jceks").exists());
    }
}
