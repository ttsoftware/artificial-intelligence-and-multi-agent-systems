package dtu.agency.board;

public class Empty extends BoardObject {

    public Empty(String label) {
        super(label);
    }

    @Override
    public BoardCell getType() {
        return BoardCell.FREE_CELL;
    }

    public Empty(Empty obj) {
        super(obj.getLabel());
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Empty) {
            return super.equals(object);
        }
        return false;
    }
}
