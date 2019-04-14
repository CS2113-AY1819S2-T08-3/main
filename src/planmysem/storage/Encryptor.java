package planmysem.storage;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import planmysem.common.exceptions.IllegalValueException;

/**
 * Contains methods to encrypt and decrypt strings
 */
public class Encryptor {

    private static int ivSize = 16;

    public static String encrypt(String toEncrypt, String keyStorage) {
        return executeEncrypt(toEncrypt, keyStorage);
    }

    public static String encrypt(String toEncrypt) {
        return executeEncrypt(toEncrypt, "KeyStorage.jceks");
    }

    /**
     * Encrypts string using the AES algorithm
     * @param toEncrypt string to be encrypted
     * @param keyStorage string .jceks file to load from.
     * @return encrypted string
     */
    private static String executeEncrypt(String toEncrypt, String keyStorage) {
        try {
            //Load key from KeyStore.
            SecretKey key = KeyStorage.load(keyStorage);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getEncoded(), "AES");

            // Generating IV.
            byte[] iv = new byte[ivSize];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());

            // Combine IV and encrypted part.
            byte[] encryptedIvAndText = new byte[ivSize + encrypted.length];
            System.arraycopy(iv, 0, encryptedIvAndText, 0, ivSize);
            System.arraycopy(encrypted, 0, encryptedIvAndText, ivSize, encrypted.length);

            return DatatypeConverter.printBase64Binary(encryptedIvAndText);

        } catch (Exception ex) {
            return null;
        }

    }

    public static String decrypt(String toDecrypt, String keyStorage) throws IllegalValueException {
        return executeDecrypt(toDecrypt, keyStorage);
    }

    public static String decrypt(String toDecrypt) throws IllegalValueException {
        return executeDecrypt(toDecrypt, "KeyStorage.jceks");
    }
    /**
     * Decrypts a string using the AES algorithm
     *
     * @param toDecrypt string to be decrypted
     * @param keyStorage string .jceks file to load from.
     * @return decrypted string
     */
    private static String executeDecrypt(String toDecrypt, String keyStorage) throws IllegalValueException {
        try {
            //Load key from KeyStore.
            SecretKey key = KeyStorage.load(keyStorage);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getEncoded(), "AES");

            //Decode to bytes.
            byte[] decodedData = DatatypeConverter.parseBase64Binary(toDecrypt);

            // Extract IV.
            byte[] iv = new byte[ivSize];
            System.arraycopy(decodedData, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Extract encrypted part.
            int encryptedSize = decodedData.length - ivSize;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(decodedData, ivSize, encryptedBytes, 0, encryptedSize);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedBytes);
            return new String(decryptedData);
        } catch (Exception e) {
            throw new IllegalValueException("Error decrypting file. Reinitializing semester.");
        }
    }

}
