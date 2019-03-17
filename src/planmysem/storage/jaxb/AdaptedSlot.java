package planmysem.storage.jaxb;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.slot.Description;
import planmysem.data.slot.Location;
import planmysem.data.slot.Name;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;
import planmysem.data.tag.TagP;

/**
 * JAXB-friendly adapted person data holder class.
 */
public class AdaptedSlot {
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String location;
    @XmlElement(required = true)
    private String description;
    @XmlElement(required = true)
    private int duration;
    @XmlElement(required = true)
    private String startTime;
    @XmlElement(required = true)
    private List<AdaptedTag> tags = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedSlot() {
    }

    /**
     * Converts a given Person into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedPerson
     */
    public AdaptedSlot(ReadOnlySlot source) {
        name = source.getName().toString();
        location = source.getLocation().toString();
        description = source.getDescription().toString();
        duration = source.getDuration();
        startTime = source.getStartTime().toString();

        tags = new ArrayList<>();
        for (TagP tag : source.getTags()) {
            tags.add(new AdaptedTag(tag));
        }
    }

    /**
     * Returns true if any required field is missing.
     */
    public boolean isAnyRequiredFieldMissing() {
        for (AdaptedTag tag : tags) {
            if (tag.isAnyRequiredFieldMissing()) {
                return true;
            }
        }
        // second call only happens if name, location, description, time are all not null
        return Utils.isAnyNull(name, duration, startTime);
    }

    /**
     * Converts this jaxb-friendly adapted person object into the Person object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Slot toModelType() throws IllegalValueException {
        final Name name = new Name(this.name);
        final Location location = new Location(this.location);
        final Description description = new Description(this.description);
        final LocalTime startTime = LocalTime.parse(this.startTime);
        final int duration = this.duration;

        final Set<TagP> tags = new HashSet<>();
        for (AdaptedTag tag : this.tags) {
            tags.add(tag.toModelType());
        }

        return new Slot(name, location, description, startTime, duration, tags);
    }
}
