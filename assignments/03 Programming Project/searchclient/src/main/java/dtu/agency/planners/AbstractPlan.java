package dtu.agency.planners;


import dtu.agency.AbstractAction;
import java.util.List;

public interface AbstractPlan extends Plan<AbstractAction> {
    List<AbstractAction> getActions();
}
