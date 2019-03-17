package planmysem.data.semester;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import planmysem.data.slot.Slot;

/**
 * JAXB-friendly adapted address book data holder class.
 */
public class AdaptedSemester {

    private String icsCalendar;

    /**
     * Converts a given Slot into this class for .ics use.
     *
     * @param source Slot object to be converted into .ics format.
     */
    public AdaptedSemester(Semester source) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        this.icsCalendar = "BEGIN:VCALENDAR\r\nVERSION:2.0\r\n";
        for (LocalDate date : source.getDays().keySet()) {
            for (Slot slots : source.getDays().get(date).getSlots()) {
                this.icsCalendar = this.icsCalendar.concat("BEGIN:VEVENT\r\n");
                LocalDateTime startDateTime = date.atTime(slots.getStartTime());
                this.icsCalendar = this.icsCalendar.concat("DTSTART:" + dateFormat.format(startDateTime) + "\r\n");
                LocalDateTime endDateTime = startDateTime.plusMinutes(slots.getDuration());
                this.icsCalendar = this.icsCalendar.concat("DTEND:" + dateFormat.format(endDateTime) + "\r\n");
                this.icsCalendar = this.icsCalendar.concat("SUMMARY:" + slots.getName() + "\r\n");
                this.icsCalendar = this.icsCalendar.concat("LOCATION:" + slots.getLocation() + "\r\n");
                this.icsCalendar = this.icsCalendar.concat("DESCRIPTION:" + slots.getDescription() + "\r\n");
                this.icsCalendar = this.icsCalendar.concat("END:VEVENT\r\n");
            }
        }
        this.icsCalendar = this.icsCalendar.concat("END:VCALENDAR\r\n");
    }

    @Override
    public String toString() {
        return icsCalendar;
    }
}
