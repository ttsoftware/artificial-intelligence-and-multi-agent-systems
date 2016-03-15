package dtu.board;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class GoalWeight implements Comparator<Goal> {

    @Override
    public int compare(Goal o1, Goal o2) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Comparator<Goal> reversed() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Comparator<Goal> thenComparing(Comparator<? super Goal> other) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public <U> Comparator<Goal> thenComparing(Function<? super Goal, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public <U extends Comparable<? super U>> Comparator<Goal> thenComparing(Function<? super Goal, ? extends U> keyExtractor) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Comparator<Goal> thenComparingInt(ToIntFunction<? super Goal> keyExtractor) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Comparator<Goal> thenComparingLong(ToLongFunction<? super Goal> keyExtractor) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Comparator<Goal> thenComparingDouble(ToDoubleFunction<? super Goal> keyExtractor) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
