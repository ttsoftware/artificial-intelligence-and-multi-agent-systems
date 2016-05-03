package dtu.agency.board;

public class Box extends BoardObject {
    public Box(String label) {
        super(label);
    }

    @Override
    public BoardCell getType() {
        return BoardCell.BOX;
    }

    public Box(Box box) {
        super(box.getLabel());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Box) {
            return super.equals(object);
        }
        return false;
    }

    public String getColor() {
        return label.substring(0, label.length() - 2);
    }

    public boolean canSolveGoal(Goal goal) {
        if (label.substring(label.length() - 2, label.length() -1).toLowerCase()
                .equals(goal.getLabel().substring(0, 1))) {
            return true;
        }
        return false;
    }
}

