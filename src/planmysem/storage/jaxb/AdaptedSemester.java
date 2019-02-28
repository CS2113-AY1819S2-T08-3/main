package planmysem.storage.jaxb;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import planmysem.data.exception.IllegalValueException;
import planmysem.data.semester.Day;
import planmysem.data.semester.ReadOnlySemester;
import planmysem.data.semester.Semester;

/**
 * JAXB-friendly adapted person data holder class.
 */
public class AdaptedSemester {
    @XmlElement(required = false)
    private String name;
    @XmlElement(required = false)
    private String academicYear;
    @XmlElement(required = false)
    private String startDate;
    @XmlElement(required = false)
    private String endDate;
    @XmlElement(required = false)
    private int noOfWeeks;
    @XmlElement(required = true)
    private HashMap<LocalDate, AdaptedDay> days = new HashMap<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public AdaptedSemester() {
    }

    /**
     * Converts a given Person into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created AdaptedPerson
     */
    public AdaptedSemester(ReadOnlySemester source) {
        name = source.getName();
        academicYear = source.getAcademicYear();
        // TODO: remove when initialization of semester is complete
        if (startDate == null || endDate == null) {
            startDate = null;
            endDate = null;
        } else {
            startDate = source.getStartDate().toString();
            endDate = source.getEndDate().toString();
        }
        noOfWeeks = source.getNoOfWeeks();

        days = new HashMap<>();
        for (Map.Entry<LocalDate, Day> day : source.getDays().entrySet()) {
            days.put(day.getKey(), new AdaptedDay(day.getValue()));
        }
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
        for (Map.Entry<LocalDate, AdaptedDay> day : days.entrySet()) {
            if (day.getValue().isAnyRequiredFieldMissing()) {
                return true;
            }
        }

        // second call only happens if phone/email/address are all not null
        // return Utils.isAnyNull(name, academicYear, days, startDate, endDate);
        return false;
    }

    /**
     * Converts this jaxb-friendly adapted person object into the Person object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Semester toModelType() throws IllegalValueException {
        final String name = this.name;
        final String academicYear = this.academicYear;
        final String startDate = this.startDate;
        final String endDate = this.endDate;
        final int noOfWeeks = this.noOfWeeks;

        final HashMap<LocalDate, Day> days = new HashMap<>();
        for (Map.Entry<LocalDate, AdaptedDay> day : this.days.entrySet()) {
            days.put(day.getKey(), day.getValue().toModelType());
        }

        // TODO: remove after initialization of semester is complete.
        if (startDate == null || endDate == null) {
            return new Semester(name, academicYear, days, null, null, noOfWeeks);
        } else {
            return new Semester(name, academicYear, days,
                    LocalDate.parse(startDate), LocalDate.parse(endDate), noOfWeeks);
        }

    }
}
