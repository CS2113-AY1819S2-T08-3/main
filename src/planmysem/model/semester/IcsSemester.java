package planmysem.model.semester;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import planmysem.model.slot.Slot;

/**
 * Converts objects into .ics format.
 */
public class IcsSemester {

    private String icsCalendar;

    /**
     * Converts the semester into this class for .ics use.
     *
     * @param source Semester object to be converted into .ics format.
     */
    public IcsSemester(Semester source) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        this.icsCalendar = "BEGIN:VCALENDAR\r\nVERSION:2.0\r\n";
        for (LocalDate date : source.getDays().keySet()) {
            for (Slot slot : source.getDays().get(date).getSlots()) {
                this.icsCalendar = this.icsCalendar.concat("BEGIN:VEVENT\r\n");
                LocalDateTime startDateTime = date.atTime(slot.getStartTime());
                this.icsCalendar = this.icsCalendar.concat("DTSTART:" + dateFormat.format(startDateTime) + "\r\n");
                LocalDateTime endDateTime = startDateTime.plusMinutes(slot.getDuration());
                this.icsCalendar = this.icsCalendar.concat("DTEND:" + dateFormat.format(endDateTime) + "\r\n");
                this.icsCalendar = this.icsCalendar.concat("SUMMARY:" + slot.getName() + "\r\n");
                if (slot.getLocation() != null) {
                    this.icsCalendar = this.icsCalendar.concat("LOCATION:" + slot.getLocation() + "\r\n");
                }
                this.icsCalendar = this.icsCalendar.concat("DESCRIPTION:" + slot.getDescription() + "\r\n");
                this.icsCalendar = this.icsCalendar.concat("X-TAGS:");
                Set<String> tagSet = slot.getTags();
                for (String tag : tagSet) {
                    this.icsCalendar = this.icsCalendar.concat(tag + ",");
                }
                this.icsCalendar = this.icsCalendar.concat("\r\n");
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
