package planmysem.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TemporaryFolder;

import planmysem.common.exceptions.IllegalValueException;

public class EncryptorTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private String tempFolderPath;

    @Before
    public void setup() {
        tempFolderPath = tempFolder.getRoot().getPath() + "Test.jceks";
    }

    @Test
    public void encrypt_valid() {
        String testString = Encryptor.encrypt("Hello World!", tempFolderPath);
        assertNotEquals(testString, "HellO World!");
    }
    @Test
    public void decrypt_valid() throws IllegalValueException {
        String testString = Encryptor.encrypt("Hello World!", tempFolderPath);
        assertEquals(Encryptor.decrypt(testString, tempFolderPath), "Hello World!");
    }
}
