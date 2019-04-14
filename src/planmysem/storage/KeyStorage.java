package planmysem.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Manages storage and loading of secret key.
 */
public class KeyStorage {

    //creates unique key
    private static char[] password = String.valueOf((System.getProperty("os.name")
    + System.getProperty("java.runtime.version")
    + System.getProperty("user.name")
    + System.getProperty("os.arch")
    + System.getProperty("java.jm.version")
    + System.getProperty("java.vm.name")
    + System.getProperty("java.home")
    + System.getProperty("java.compiler")).hashCode()).toCharArray();

    /**
     * Loads the secret key from the specified key store.
     * @param fileName String .jceks file to load from.
     */
    public static SecretKey load (String fileName) throws Exception {

        //Initialize keystore.
        KeyStore ks = KeyStore.getInstance("JCEKS");
        SecretKey secretKey;
        try {
            ks.load(new FileInputStream(fileName), password);
            secretKey = (SecretKey) ks.getKey("secret-key", password);

        } catch (IOException | CertificateException | UnrecoverableKeyException ex) {
            secretKey = generateSecretKey(fileName);
        }
        return secretKey;
    }

    /**
     * Generates a SecretKey and saves it in the specified file.
     * @param fileName String .jceks file to save to.
     */
    private static SecretKey generateSecretKey(String fileName) throws Exception {
        //Initialize keystore.
        KeyStore ks = KeyStore.getInstance("JCEKS");

        //Generates key.
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        //Save key.
        KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter passwordParam = new KeyStore.PasswordProtection(password);
        ks.load(null, null);
        ks.setEntry("secret-key", secret, passwordParam);

        FileOutputStream fos = new FileOutputStream(fileName);
        ks.store(fos, password);

        return secretKey;
    }
}


