package planmysem.commands;

import static planmysem.ui.Gui.DISPLAYED_INDEX_OFFSET;

import java.time.LocalDate;
import java.util.List;

import javafx.util.Pair;
import planmysem.common.Messages;
import planmysem.data.Planner;
import planmysem.data.slot.ReadOnlySlot;

/**
 * Represents an executable command.
 */
public abstract class Command {
    protected Planner planner;
    protected List<Pair<LocalDate, ? extends ReadOnlySlot>> relevantSlots;
    private int targetIndex = -1;

    /**
     * @param slots last visible listing index of the target person
     */
    //    public Command(List<Pair<LocalDate, ? extends ReadOnlySlot>> slots) {
    //        this.relevantSlots = slots;
    //    }

    /**
     * @param targetIndex last visible listing index of the target person
     */
    public Command(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    protected Command() {
    }

    /**
     * Executes the command and returns the result.
     */
    public abstract CommandResult execute();

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of persons.
     *
     * @param slots used to generate summary
     * @return summary message for persons displayed
     */
    public static String getMessageForSlotsListShownSummary(List<Pair<LocalDate, ? extends ReadOnlySlot>> slots) {
        return String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, slots.size());
    }

    /**
     * Supplies the data the command will operate on.
     */
    public void setData(Planner planner, List<Pair<LocalDate, ? extends ReadOnlySlot>> slots) {
        this.planner = planner;
        this.relevantSlots = slots;
    }

    /**
     * Extracts the the target Day in the last shown list from the given arguments.
     *
     * @throws IndexOutOfBoundsException if the target index is out of bounds of the last viewed listing
     */
    protected List<Pair<LocalDate, ? extends ReadOnlySlot>> getTargetSlots() throws IndexOutOfBoundsException {
        return relevantSlots;
    }

    protected Pair<LocalDate, ? extends ReadOnlySlot> getTargetSlot(int index) throws IndexOutOfBoundsException {
        return relevantSlots.get(index);
    }

    protected Pair<LocalDate, ? extends ReadOnlySlot> getTargetSlot() throws IndexOutOfBoundsException {
        return relevantSlots.get(targetIndex - DISPLAYED_INDEX_OFFSET);
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }
}
