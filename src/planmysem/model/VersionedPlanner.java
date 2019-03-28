package planmysem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code AddressBook} that keeps track of its own history.
 */
public class VersionedPlanner extends Planner {

    private final List<ReadOnlyPlanner> plannerListState;
    private int currentStatePointer;

    public VersionedPlanner(ReadOnlyPlanner initialState) {
        super(initialState);

        plannerListState = new ArrayList<>();
        plannerListState.add(initialState);
        currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current {@code AddressBook} state at the end of the state list.
     * Undone states are removed from the state list.
     */
    public void commit() {
        removeStatesAfterCurrentPointer();
        plannerListState.add(new Planner(this));
        currentStatePointer++;
    }

    private void removeStatesAfterCurrentPointer() {
        plannerListState.subList(currentStatePointer + 1, plannerListState.size()).clear();
    }

    /**
     * Restores the address book to its previous state.
     */
    public void undo() {
        if (!canUndo()) {
            throw new NoUndoableStateException();
        }
        currentStatePointer--;
        resetData(plannerListState.get(currentStatePointer));
    }

    /**
     * Restores the address book to its previously undone state.
     */
    public void redo() {
        if (!canRedo()) {
            throw new NoRedoableStateException();
        }
        currentStatePointer++;
        resetData(plannerListState.get(currentStatePointer));
    }

    /**
     * Returns true if {@code undo()} has address book states to undo.
     */
    public boolean canUndo() {
        return currentStatePointer > 0;
    }

    /**
     * Returns true if {@code redo()} has address book states to redo.
     */
    public boolean canRedo() {
        return currentStatePointer < plannerListState.size() - 1;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VersionedPlanner)) {
            return false;
        }

        VersionedPlanner otherVersionedPlanner = (VersionedPlanner) other;

        // state check
        return super.equals(otherVersionedPlanner)
                && plannerListState.equals(otherVersionedPlanner.plannerListState)
                && currentStatePointer == otherVersionedPlanner.currentStatePointer;
    }

    /**
     * Thrown when trying to {@code undo()} but can't.
     */
    public static class NoUndoableStateException extends RuntimeException {
        private NoUndoableStateException() {
            super("Current state pointer at start of addressBookState list, unable to undo.");
        }
    }

    /**
     * Thrown when trying to {@code redo()} but can't.
     */
    public static class NoRedoableStateException extends RuntimeException {
        private NoRedoableStateException() {
            super("Current state pointer at end of addressBookState list, unable to redo.");
        }
    }
}
