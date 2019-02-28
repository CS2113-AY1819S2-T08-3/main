package planmysem.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import planmysem.data.Planner;
import planmysem.data.exception.IllegalValueException;
import planmysem.storage.jaxb.AdaptedPlanner;

/**
 * Represents the file used to store Planner data.
 */
public class StorageFileP {
    /**
     * Default file path used if the user doesn't provide the file name.
     */
    public static final String DEFAULT_STORAGE_FILEPATH = "PlanMySem.txt";

    /* Note: Note the use of nested classes below.
     * More info https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html
     */
    public final Path path;
    private final JAXBContext jaxbContext;

    /**
     * @throws InvalidStorageFilePathException if the default path is invalid
     */
    public StorageFileP() throws InvalidStorageFilePathException {
        this(DEFAULT_STORAGE_FILEPATH);
    }

    /**
     * @throws InvalidStorageFilePathException if the given file path is invalid
     */
    public StorageFileP(String filePath) throws InvalidStorageFilePathException {
        try {
            jaxbContext = JAXBContext.newInstance(AdaptedPlanner.class);
        } catch (JAXBException ex) {
            throw new RuntimeException("jaxb initialisation error" + ex);
        }

        path = Paths.get(filePath);
        if (!isValidPath(path)) {
            throw new InvalidStorageFilePathException("Storage file should end with '.txt'");
        }
    }

    /**
     * Returns true if the given path is acceptable as a storage file.
     * The file path is considered acceptable if it ends with '.txt'
     */
    private static boolean isValidPath(Path filePath) {
        return filePath.toString().endsWith(".txt");
    }

    /**
     * Saves all data to this storage file.
     *
     * @throws StorageOperationException if there were errors converting and/or storing data to file.
     */
    public void save(Planner planner) throws StorageOperationException {
        /* Note: Note the 'try with resource' statement below.
         * More info: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
         */
        try (final Writer fileWriter =
                     new BufferedWriter(new FileWriter(path.toFile()))) {

            final AdaptedPlanner toSave = new AdaptedPlanner(planner);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(toSave, fileWriter);

        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to file: " + path + " error: " + ioe.getMessage());
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error converting Planner into storage format");
        }
    }

    /**
     * Loads data from this storage file.
     *
     * @throws StorageOperationException if there were errors reading and/or converting data from file.
     */
    public Planner load() throws StorageOperationException {
        try (final Reader fileReader =
                new BufferedReader(new FileReader(path.toFile()))) {

            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final AdaptedPlanner loaded = (AdaptedPlanner) unmarshaller.unmarshal(fileReader);
            // manual check for missing elements
            if (loaded.isAnyRequiredFieldMissing()) {
                throw new StorageOperationException("File data missing some elements");
            }
            return loaded.toModelType();

            /* Note: Here, we are using an exception to create the file if it is missing. However, we should minimize
             * using exceptions to facilitate normal paths of execution. If we consider the missing file as a 'normal'
             * situation (i.e. not truly exceptional) we should not use an exception to handle it.
             */

            // create empty file if not found
        } catch (FileNotFoundException ex) {
            final Planner empty = new Planner();
            save(empty);
            return empty;

            // other errors
        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to file: " + path);
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error parsing file data format");
        } catch (IllegalValueException ive) {
            throw new StorageOperationException("File contains illegal data values; data type constraints not met");
        }
    }

    public String getPath() {
        return path.toString();
    }

    /**
     * Signals that the given file path does not fulfill the storage filepath constraints.
     */
    public static class InvalidStorageFilePathException extends IllegalValueException {
        public InvalidStorageFilePathException(String message) {
            super(message);
        }
    }

    /**
     * Signals that some error has occured while trying to convert and read/write data between the application
     * and the storage file.
     */
    public static class StorageOperationException extends Exception {
        public StorageOperationException(String message) {
            super(message);
        }
    }

}
