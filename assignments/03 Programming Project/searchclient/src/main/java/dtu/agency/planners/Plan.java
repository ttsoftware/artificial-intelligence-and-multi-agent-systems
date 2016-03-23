package dtu.agency.planners;

import java.util.List;

public interface Plan<T> {
    List<T> getActions();
}
