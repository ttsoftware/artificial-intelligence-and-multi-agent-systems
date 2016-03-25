package dtu.agency.agent.actions;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class ActionComparator implements Comparator<Action> {

    @Override
    public int compare(Action o1, Action o2) {
        return o1.getHeuristic() - o2.getHeuristic();
    }

    @Override
    public Comparator<Action> reversed() {
        return null;
    }

    @Override
    public Comparator<Action> thenComparing(Comparator<? super Action> other) {
        return null;
    }

    @Override
    public <U> Comparator<Action> thenComparing(Function<? super Action, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return null;
    }

    @Override
    public <U extends Comparable<? super U>> Comparator<Action> thenComparing(Function<? super Action, ? extends U> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<Action> thenComparingInt(ToIntFunction<? super Action> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<Action> thenComparingLong(ToLongFunction<? super Action> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<Action> thenComparingDouble(ToDoubleFunction<? super Action> keyExtractor) {
        return null;
    }
}
