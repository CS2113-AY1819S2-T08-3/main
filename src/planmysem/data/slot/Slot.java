package planmysem.data.slot;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.tag.TagP;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated.
 */
public class Slot implements ReadOnlySlot {
    private final Set<TagP> tags = new HashSet<>();
    private Name name;
    private Location location;
    private Description description;
    private LocalTime startTime;
    private int duration;

    /**
     * Assumption: Every field must be present and not null.
     */
    public Slot(Name name, Location location, Description description,
                LocalTime startTime, LocalTime endTime, Set<TagP> tags) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.duration = Utils.getDuration(startTime, endTime);
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Assumption: Every field must be present and not null.
     */
    public Slot(Name name, Location location, Description description,
                LocalTime startTime, int duration, Set<TagP> tags) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Copy constructor.
     * @throws IllegalValueException if value is invalid
     */
    public Slot(ReadOnlySlot source) {
        this(source.getName(), source.getLocation(), source.getDescription(),
                source.getStartTime(), source.getDuration(), source.getTags());
    }

    /**
     * Set name.
     * @throws IllegalValueException if value is invalid
     */
    public void setName(String value) throws IllegalValueException {
        if (value == null) {
            return;
        }
        name = new Name(value);
    }

    /**
     * Set location.
     * @throws IllegalValueException if value is invalid
     */
    public void setLocation(String value) throws IllegalValueException {
        if (value == null) {
            return;
        }
        if (value.equals("")) {
            location = new Location(null);
        } else {
            location = new Location(value);
        }
    }

    /**
     * Set description.
     * @throws IllegalValueException if value is invalid
     */
    public void setDescription(String value) throws IllegalValueException {
        if (value == null) {
            return;
        }
        if ("".equals(value)) {
            description = new Description(null);
        } else {
            description = new Description(value);
        }
    }

    /**
     * Set start time.
     */
    public void setStartTime(LocalTime value) {
        startTime = value;
    }

    /**
     * Set duration.
     */
    public void setDuration(int value) {
        duration = value;
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public Set<TagP> getTags() {
        return tags;
    }

    /**
     * Replaces this slot's tags with the tags in {@code replacement}.
     */
    public void setTags(Set<TagP> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ReadOnlySlot // instanceof handles nulls
                && this.isSameStateAs((ReadOnlySlot) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, location, description, startTime, tags);
    }

    @Override
    public String toString() {
        return getAsTextShowAll();
    }

}
