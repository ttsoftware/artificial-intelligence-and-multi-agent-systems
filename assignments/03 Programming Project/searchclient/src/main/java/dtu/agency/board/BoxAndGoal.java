package dtu.agency.board;

public class BoxAndGoal extends BoardObject {

    private final Box box;
    private final Goal goal;

    public BoxAndGoal(Box box, Goal goal) {
        super("(" + box.getLabel() + goal.getLabel() + ")");
        this.box = box;
        this.goal = goal;
    }

    public BoxAndGoal(BoxAndGoal other) {
        super(other.getLabel());
        this.box = new Box(other.getBox());
        this.goal = new Goal(other.getGoal());
    }

    public Box getBox() {
        return box;
    }

    public Goal getGoal() {
        return goal;
    }

    /**
     *
     * @return True if the @goal is of the same type as the @box
     */
    public boolean isSolved() {
        return box.getLabel().startsWith(goal.getLabel().toUpperCase().substring(0, 1));
    }

    @Override
    public BoardCell getType() {
        return BoardCell.BOX_GOAL;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof BoxAndGoal) {
            return super.equals(object);
        }
        return false;
    }
}
