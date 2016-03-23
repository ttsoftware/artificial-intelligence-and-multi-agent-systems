package dtu.agency.board;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class GoalComparator implements Comparator<Goal> {

    @Override
    public int compare(Goal goal1, Goal goal2) {
        // TODO: This might not be correct
        return goal1.getWeight() - goal2.getWeight();
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
