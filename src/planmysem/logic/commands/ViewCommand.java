package planmysem.logic.commands;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static planmysem.common.Messages.MESSAGE_DATE_OUT_OF_BOUNDS;
import static planmysem.common.Utils.getNearestDayOfWeek;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import planmysem.common.Clock;
import planmysem.common.Utils;
import planmysem.logic.CommandHistory;
import planmysem.model.Model;
import planmysem.model.semester.Day;
import planmysem.model.semester.Semester;
import planmysem.model.slot.Slot;

/**
 * View the planner.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";
    public static final String COMMAND_WORD_SHORT = "v";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": View month/week/day view of planner."
            + "\n\tFormat: view [viewType] [specifier]"
            + "\n\tParameters:"
            + "\n\t\tMandatory: [viewType]"
            + "\n\t\tOptional: [specifier]"
            + "\n\tView month example: "
            + "\n\t\tExample 1: " + COMMAND_WORD
            + " month"
            + "\n\tView week example: "
            + "\n\t\tExample 1: " + COMMAND_WORD
            + " week 7"
            + "\n\t\tExample 2: " + COMMAND_WORD
            + " week recess"
            + "\n\t\tExample 3: " + COMMAND_WORD
            + " week"
            + "\n\t\tExample 4: " + COMMAND_WORD
            + " week details"
            + "\n\tView day example: "
            + "\n\t\tExample 1: " + COMMAND_WORD
            + " day 01-03-2019"
            + "\n\t\tExample 2: " + COMMAND_WORD
            + " day monday"
            + "\n\t\tExample 3: " + COMMAND_WORD
            + " day";

    private final String[] viewArgs;

    public ViewCommand(String[] viewArgs) {
        this.viewArgs = viewArgs;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory commandHistory) {
        final Semester currentSemester = model.getPlanner().getSemester();
        String viewType = viewArgs[0];
        String output = "";

        switch (viewType) {
        case "month":
            output = displayMonthView(currentSemester);
            break;

        case "week":
            if ((viewArgs.length == 3 && "Details".equals(viewArgs[2]))
                    || (viewArgs.length == 2 && "Details".equals(viewArgs[1]))) {
                output = displayDetailedWeekView(currentSemester, viewArgs[1]);
                break;
            }

            if (viewArgs.length == 2) {
                output = displayWeekView(currentSemester, viewArgs[1]);
            } else {
                output = displayWeekView(currentSemester, null);
            }
            break;

        case "day":
            if (viewArgs.length == 2) {
                output = displayDayView(currentSemester, viewArgs[1]);
            } else {
                output = displayDayView(currentSemester, null);
            }
            break;

        default:
            break;
        }

        return new CommandResult(output);
    }

    /**
     * Display all months for the semester.
     * Solution below adapted from https://introcs.cs.princeton.edu/java/21function/Calendar.java.html
     */
    private String displayMonthView(Semester currentSemester) {
        HashMap<LocalDate, Day> allDays = currentSemester.getDays();
        LocalDate semesterStartDate = currentSemester.getStartDate();
        LocalDate semesterEndDate = currentSemester.getEndDate();
        int year = semesterStartDate.getYear();
        LocalDate firstDay = semesterStartDate.with(firstDayOfYear());
        int spaces = firstDay.getDayOfWeek().getValue();
        int firstMonthOfSem = semesterStartDate.getMonthValue();
        int lastMonthOfSem = semesterEndDate.getMonthValue();
        StringBuilder sb = new StringBuilder();

        String[] months = {"", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
        String[] monthOutput = new String[12];

        int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        for (int m = 1; m <= 12; m++) {
            StringBuilder monthBuilder = new StringBuilder();
            // Set number of days in February to 29 if it is a leap year.
            if ((((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) && m == 2) {
                days[m] = 29;
            }

            // Print calendar header.
            monthBuilder.append("          " + months[m] + " " + year + "\n");
            monthBuilder.append("_____________________________________\n");
            monthBuilder.append("   Sun  Mon Tue   Wed Thu   Fri  Sat\n");

            // Print spaces required for the start of a month.
            spaces = (days[m - 1] + spaces) % 7;
            for (int i = 0; i < spaces; i++) {
                monthBuilder.append("     ");
            }
            // Print the days in the month.
            for (int i = 1; i <= days[m]; i++) {
                monthBuilder.append(String.format("  %3d", i));
                if (((i + spaces) % 7 == 0)) {
                    Day tempDay = allDays.get(LocalDate.of(year, m, i));
                    String weekType = "";
                    if (tempDay != null) {
                        weekType = tempDay.getType();
                    }
                    monthBuilder.append("   | " + weekType + "\n");
                } else if (i == days[m]) {
                    LocalDate tempDate = LocalDate.of(year, m, i);
                    Day tempDay = allDays.get(tempDate);
                    String weekType = "";
                    int extraSpaces = 6 - (tempDate.getDayOfWeek().getValue() % 7);
                    for (int j = 0; j < extraSpaces; j++) {
                        monthBuilder.append("     ");
                    }
                    if (tempDay != null) {
                        weekType = tempDay.getType();
                    }
                    monthBuilder.append("   | " + weekType + "\n");
                }
            }

            monthBuilder.append("\n");
            monthOutput[m - 1] = monthBuilder.toString();
        }

        for (int m = firstMonthOfSem - 1; m < lastMonthOfSem; m++) {
            sb.append(monthOutput[m]);
        }

        return sb.toString();
    }

    /**
     * Display all slots for a given week in a formatted view.
     */
    private String displayWeekView(Semester currentSemester, String week) {
        HashMap<LocalDate, Day> allDays = currentSemester.getDays();
        List<LocalDate> datesList;
        LocalDate weekStart;
        LocalDate weekEnd;
        int[] weekOfYear = {0, 0};
        StringBuilder sb = new StringBuilder();

        if (week == null) {
            week = allDays.get(LocalDate.now(Clock.get())).getType() + " of " + currentSemester.getName();

            weekStart = LocalDate.now(Clock.get()).with(WeekFields.ISO.dayOfWeek(), 1);
            weekEnd = weekStart.plusDays(7);
            datesList = weekStart.datesUntil(weekEnd).collect(Collectors.toList());
        } else {
            HashMap<Integer, String> acadCal = currentSemester.getAcadCal();
            String key;

            if ("Recess".equals(week) || "Reading".equals(week) || "Examination".equals(week)
                    || "Orientation".equals(week)) {
                key = week + " Week" + "_" + currentSemester.getName();
                week = week + " Week of " + currentSemester.getName();
            } else {
                key = "Week " + week + "_" + currentSemester.getName();
                week = "Week " + week + " of " + currentSemester.getName();
            }

            for (Map.Entry<Integer, String> entry: acadCal.entrySet()) {
                if (key.equals(entry.getValue())) {
                    if (weekOfYear[0] == 0) {
                        weekOfYear[0] = entry.getKey();
                    } else {
                        weekOfYear[1] = entry.getKey();
                    }
                }
            }

            weekStart = LocalDate.now(Clock.get()).with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfYear[0]);
            weekStart = weekStart.with(WeekFields.ISO.dayOfWeek(), 1);
            weekEnd = weekStart.with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfYear[0] + 1);
            datesList = weekStart.datesUntil(weekEnd).collect(Collectors.toList());
        }

        // Print academic week header.
        int width = 120;
        sb.append(centerAlignText(width, week) + "\n");

        // Print formatted week view.
        sb.append(getFormattedWeek(allDays, datesList));
        if (weekOfYear[1] != 0) {
            weekStart = weekEnd;
            weekEnd = weekStart.plusDays(7);
            datesList = weekStart.datesUntil(weekEnd).collect(Collectors.toList());
            sb.append("\n" + getFormattedWeek(allDays, datesList));
        }

        return sb.toString();
    }

    /**
     * Display all slots for a given week in a detailed view.
     */
    private String displayDetailedWeekView(Semester currentSemester, String week) {
        HashMap<LocalDate, Day> allDays = currentSemester.getDays();
        List<LocalDate> datesList;
        LocalDate weekStart;
        LocalDate weekEnd;
        int[] weekOfYear = {0, 0};
        StringBuilder sb = new StringBuilder();

        if ("Details".equals(week)) {
            sb.append(allDays.get(LocalDate.now(Clock.get())).getType() + " of " + currentSemester.getName() + "\n");

            weekStart = LocalDate.now(Clock.get()).with(WeekFields.ISO.dayOfWeek(), 1);
            weekEnd = weekStart.plusDays(7);
            datesList = weekStart.datesUntil(weekEnd).collect(Collectors.toList());
        } else {
            HashMap<Integer, String> acadCal = currentSemester.getAcadCal();
            String key;

            if ("Recess".equals(week) || "Reading".equals(week) || "Examination".equals(week)
                    || "Orientation".equals(week)) {
                key = week + " Week" + "_" + currentSemester.getName();
                sb.append(week + " Week" + " of " + currentSemester.getName() + "\n");
            } else {
                key = "Week " + week + "_" + currentSemester.getName();
                sb.append("Week " + week + " of " + currentSemester.getName() + "\n");
            }

            for (Map.Entry<Integer, String> entry: acadCal.entrySet()) {
                if (key.equals(entry.getValue())) {
                    if (weekOfYear[0] == 0) {
                        weekOfYear[0] = entry.getKey();
                    } else {
                        weekOfYear[1] = entry.getKey();
                    }
                }
            }

            weekStart = LocalDate.now(Clock.get()).with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfYear[0]);
            weekStart = weekStart.with(WeekFields.ISO.dayOfWeek(), 1);
            weekEnd = weekStart.with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfYear[0] + 1);
            if (weekOfYear[1] != 0) {
                weekEnd = weekStart.with(WeekFields.ISO.weekOfWeekBasedYear(), weekOfYear[0] + 2);
            }
            datesList = weekStart.datesUntil(weekEnd).collect(Collectors.toList());
        }

        sb.append("__________________________________________________________________________\n\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (LocalDate date : datesList) {
            sb.append(displayDayView(currentSemester, date.format(formatter)));
            sb.append("__________________________________________________________________________\n\n");
        }

        return sb.toString();
    }

    /**
     * Display all slots for a given day/date.
     */
    private String displayDayView(Semester currentSemester, String dateOrDay) {
        HashMap<LocalDate, Day> allDays = currentSemester.getDays();
        StringBuilder sb = new StringBuilder();

        // Parse different formats of given day/date.
        LocalDate givenDate;
        if (dateOrDay == null) {
            givenDate = LocalDate.now(Clock.get());
        } else {
            int day = -1;
            givenDate = Utils.parseDate(dateOrDay);
            if (givenDate == null) {
                day = Utils.parseDay(dateOrDay);
            }
            if (day == -1 && givenDate == null) {
                return MESSAGE_USAGE;
            }
            if (day != -1) {
                givenDate = getNearestDayOfWeek(LocalDate.now(Clock.get()), day);
            }
        }

        if (givenDate.isAfter(currentSemester.getStartDate().minusDays(1))
                && givenDate.isBefore(currentSemester.getEndDate().plusDays(1))) {
            sb.append(givenDate.getDayOfWeek().name() + " , " + givenDate + "\n\n");
        } else {
            return MESSAGE_DATE_OUT_OF_BOUNDS;
        }

        // Retrieve all slots for given day/date in sorted order.
        ArrayList<Slot> allSlotsInDay = allDays.get(givenDate).getSlots();
        Comparator<Slot> comparator = new Comparator<Slot>() {
            @Override
            public int compare(final Slot o1, final Slot o2) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        };
        allSlotsInDay.sort(comparator);

        // Print each slot.
        for (Slot slot : allSlotsInDay) {
            sb.append("* " + slot.getStartTime());
            sb.append(" to ");
            sb.append(Utils.getEndTime(slot.getStartTime(), slot.getDuration()));

            sb.append("\n\t" + slot.getName() + "\n");
            sb.append("\t" + "Location: " + slot.getLocation() + "\n");
            sb.append("\t" + "Description: " + slot.getDescription() + "\n");
            sb.append("\n\tTags: \n");

            int count = 1;
            for (String tag : slot.getTags()) {
                sb.append("\t");
                sb.append(count);
                sb.append(". ");
                sb.append(tag);
                count++;
            }

            sb.append("\n\n");
        }

        return sb.toString();
    }

    /**
     * Center aligns text.
     */
    private String centerAlignText(int width, String text) {
        String formattedString = text;
        int padSize = width - formattedString.length();
        int padStart = formattedString.length() + padSize / 2;
        formattedString = String.format("%" + padStart + "s", formattedString);
        formattedString = String.format("%-" + width + "s", formattedString);

        return formattedString;
    }

    /**
     * Returns formatted week view.
     */
    private String getFormattedWeek(HashMap<LocalDate, Day> allDays, List<LocalDate> datesList) {
        StringBuilder sb = new StringBuilder();

        // Print line divider.
        int width = 120;
        for (int i = 0; i < width; i++) {
            sb.append("-");
        }
        sb.append("\n");

        // Print days of week header.
        width = 16;
        sb.append("|" + centerAlignText(width, "Monday") + "|");
        sb.append(centerAlignText(width, "Tuesday") + "|");
        sb.append(centerAlignText(width, "Wednesday") + "|");
        sb.append(centerAlignText(width, "Thursday") + "|");
        sb.append(centerAlignText(width, "Friday") + "|");
        sb.append(centerAlignText(width, "Saturday") + "|");
        sb.append(centerAlignText(width, "Sunday") + "|");

        sb.append("\n|");
        for (LocalDate date : datesList) {
            sb.append(centerAlignText(width, date.toString()) + "|");
        }
        sb.append("\n");

        width = 120;
        for (int i = 0; i < width; i++) {
            sb.append("-");
        }
        sb.append("\n");

        // Retrieve all slots for each day.
        ArrayList<ArrayList<Slot>> slotsInDayList = new ArrayList<>();
        for (LocalDate date : datesList) {
            ArrayList<Slot> allSlotsInDay = allDays.get(date).getSlots();
            Comparator<Slot> comparator = new Comparator<Slot>() {
                @Override
                public int compare(final Slot o1, final Slot o2) {
                    return o1.getStartTime().compareTo(o2.getStartTime());
                }
            };
            allSlotsInDay.sort(comparator);
            slotsInDayList.add(new ArrayList<>(allSlotsInDay));
        }

        // Print all slots for each day.
        width = 16;
        while (!slotsInDayList.get(0).isEmpty() || !slotsInDayList.get(1).isEmpty()
                || !slotsInDayList.get(2).isEmpty() || !slotsInDayList.get(3).isEmpty()
                || !slotsInDayList.get(4).isEmpty() || !slotsInDayList.get(5).isEmpty()
                || !slotsInDayList.get(6).isEmpty()) {
            StringBuilder slotTimingLine = new StringBuilder();
            StringBuilder slotTitleLine = new StringBuilder();
            StringBuilder emptyLine = new StringBuilder();
            slotTimingLine.append("|");
            slotTitleLine.append("\n|");
            emptyLine.append("\n|");

            for (ArrayList<Slot> allSlotsInDay : slotsInDayList) {
                if (allSlotsInDay.isEmpty()) {
                    slotTimingLine.append(centerAlignText(width, "") + "|");
                    slotTitleLine.append(centerAlignText(width, "") + "|");
                } else {
                    Slot slot = allSlotsInDay.get(0);
                    String slotTiming = "* " + slot.getStartTime() + " - "
                            + Utils.getEndTime(slot.getStartTime(), slot.getDuration()) + " |";
                    slotTimingLine.append(slotTiming);

                    String shortTitle;
                    if (slot.getName().length() < 15) {
                        shortTitle = slot.getName();
                    } else {
                        shortTitle = slot.getName().substring(0, 14) + "..";
                    }
                    slotTitleLine.append(centerAlignText(width, shortTitle) + "|");

                    allSlotsInDay.remove(0);
                }
                emptyLine.append(centerAlignText(width, "") + "|");
            }

            sb.append(slotTimingLine.toString() + slotTitleLine.toString() + emptyLine.toString() + "\n");
        }

        // Print closing border.
        width = 120;
        for (int i = 0; i < width; i++) {
            sb.append("-");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ViewCommand // instanceof handles nulls
                && Arrays.equals(viewArgs, ((ViewCommand) other).viewArgs));
    }
}
