package dtu.agency.agent.actions;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class ActionComparator<T extends Action> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        return o1.getHeuristic() - o2.getHeuristic();
    }
}
