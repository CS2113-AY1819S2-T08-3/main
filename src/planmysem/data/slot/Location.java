package planmysem.data.slot;

import planmysem.data.exception.IllegalValueException;

/**
 * Represents a Slot's location in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValid(String)}
 */
public class Location {
    public static final String EXAMPLE = "NUS COM2 04-22";
    public static final String MESSAGE_NAME_CONSTRAINTS =
            "Slot's location should be spaces or alphanumeric characters";
    public static final String NAME_VALIDATION_REGEX = "[\\p{Alnum} ]+";

    private String value;

    /**
     * Validates given name.
     *
     * @throws IllegalValueException if given name string is invalid.
     */
    public Location(String value) throws IllegalValueException {
        String location = value.trim();
        if (!isValid(location)) {
            throw new IllegalValueException(MESSAGE_NAME_CONSTRAINTS);
        }
        this.value = location;
    }

    /**
     * Returns true if a given string is a valid Slot description.
     */
    public static boolean isValid(String value) {
        return value.matches(NAME_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Description // instanceof handles nulls
                && this.value.equals(((Description) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
