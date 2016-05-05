package dtu.agency.events.client;


import dtu.agency.conflicts.Conflict;
import dtu.agency.conflicts.ResolvedConflict;
import dtu.agency.events.AsyncEvent;

public class ConflictResolutionEvent extends AsyncEvent<ResolvedConflict> {

    private final Conflict conflict;

    public ConflictResolutionEvent(Conflict conflict) {
        this.conflict = conflict;
    }

    public Conflict getConflict() {
        return conflict;
    }
}
