package planmysem.storage.jaxb;

import javax.xml.bind.annotation.XmlValue;

import planmysem.common.Utils;
import planmysem.data.exception.IllegalValueException;
import planmysem.data.tag.TagP;

/**
 * JAXB-friendly adapted tag data holder class.
 */
public class AdaptedTag {
    @XmlValue
    private String value;

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedTag() {
    }

    /**
     * Converts a given Tag into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedTag
     */
    public AdaptedTag(TagP source) {
        value = source.value;
    }

    /**
     * Returns true if any required field is missing.
     * <p>
     * JAXB does not enforce (required = true) without a given XML schema.
     * Since we do most of our validation using the data class constructors, the only extra logic we need
     * is to ensure that every xml element in the document is present. JAXB sets missing elements as null,
     * so we check for that.
     */
    public boolean isAnyRequiredFieldMissing() {
        return Utils.isAnyNull(value);
    }

    /**
     * Converts this jaxb-friendly adapted tag object into the Tag object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public TagP toModelType() throws IllegalValueException {
        return new TagP(value);
    }
}
