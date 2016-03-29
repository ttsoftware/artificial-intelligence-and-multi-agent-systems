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

    @Override
    public Comparator<T> reversed() {
        return null;
    }

    @Override
    public Comparator<T> thenComparing(Comparator<? super T> other) {
        return null;
    }

    @Override
    public <U> Comparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        return null;
    }

    @Override
    public <U extends Comparable<? super U>> Comparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<T> thenComparingInt(ToIntFunction<? super T> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<T> thenComparingLong(ToLongFunction<? super T> keyExtractor) {
        return null;
    }

    @Override
    public Comparator<T> thenComparingDouble(ToDoubleFunction<? super T> keyExtractor) {
        return null;
    }
}
