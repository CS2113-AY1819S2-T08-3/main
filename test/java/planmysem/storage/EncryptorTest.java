package planmysem.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import planmysem.common.exceptions.IllegalValueException;

public class EncryptorTest {

    @Test
    public void encrypt_valid() {
        String testString = Encryptor.encrypt("Hello World!");
        assertNotEquals(testString, "HellO World!");
    }
    @Test
    public void decrypt_valid() throws IllegalValueException {
        String testString = Encryptor.encrypt("Hello World!");
        assertEquals(Encryptor.decrypt(testString), "Hello World!");
    }
}
